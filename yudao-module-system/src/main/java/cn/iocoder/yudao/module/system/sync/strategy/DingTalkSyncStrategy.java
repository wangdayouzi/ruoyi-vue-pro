package cn.iocoder.yudao.module.system.sync.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialClientDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyDeptDTO;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyUserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 钉钉同步策略实现
 *
 * 调用钉钉开放平台 API，拉取部门列表和用户列表。
 * API 文档：https://open.dingtalk.com/document/
 *
 * <h4>需要调用的开放平台接口</h4>
 * <pre>
 * 1. 基础鉴权
 *    GET  /gettoken                          — 获取 access_token，有效期 7200s
 *
 * 2. 部门同步（listsub 接口，一次返回子部门完整信息，避免 N+1 QPS 问题）
 *    POST /topapi/v2/department/listsub       — 获取子部门列表（含 id/name/parent_id/order），参数 dept_id
 *
 * 3. 用户同步
 *    POST /topapi/user/listsimple            — 分页获取部门下用户（userid/name），参数 dept_id/cursor/size
 *    POST /topapi/v2/user/get                — 获取用户详情（手机号/邮箱/unionid），参数 userid
 *
 * 4. 钉钉后台需开通的权限 scope
 *    qyapi_gettoken                          获取 access_token
 *    qyapi_get_department_list               通讯录部门列表读取（listsub 接口）
 *    qyapi_get_user_list                     通讯录用户列表读取（listsimple 接口）
 *    qyapi_get_user_detail                   通讯录用户详情读取（get 接口）
 * </pre>
 *
 * @author yudao
 */
@Component
@Slf4j
public class DingTalkSyncStrategy implements ThirdPartySyncStrategy {

    // ---------- 钉钉 API 地址 ----------
    private static final String GET_TOKEN_URL        = "https://api.dingtalk.com/v1.0/oauth2/accessToken";
    private static final String DEPT_LISTSUB_URL     = "https://oapi.dingtalk.com/topapi/v2/department/listsub";
    private static final String USER_LIST_URL        = "https://oapi.dingtalk.com/topapi/user/listsimple";
    private static final String USER_DETAIL_URL      = "https://oapi.dingtalk.com/topapi/v2/user/get";

    // ---------- QPS 控制 ----------
    /** 用户详情接口调用间隔（毫秒），避免短时间大量请求触发限流 */
    private static final long USER_DETAIL_DELAY_MS = 200;
    /** 被限流时的最大重试次数 */
    private static final int MAX_RETRIES = 3;
    /** 被限流后的初始等待时间（毫秒），指数退避 */
    private static final long RATE_LIMIT_BASE_WAIT_MS = 1000;

    // ---------- Token 缓存 ----------
    /** 简单的内存缓存：accessToken，key = appKey */
    private final Map<String, TokenCache> tokenCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public Integer getSocialType() {
        return SocialTypeEnum.DINGTALK.getType();
    }

    @Override
    public boolean validate(SocialClientDO client) {
        try {
            String token = getAccessToken(client);
            return StrUtil.isNotBlank(token);
        } catch (Exception e) {
            log.warn("[DingTalk][validate] 验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<ThirdPartyDeptDTO> fetchDepartments(SocialClientDO client) {
        String token = getAccessToken(client);
        List<ThirdPartyDeptDTO> result = new ArrayList<>();
        // 从根部门(1)开始递归拉取
        fetchDeptTree(token, 1L, result);
        log.info("[DingTalk][fetchDepartments] 共拉取 {} 个部门", result.size());
        return result;
    }

    @Override
    public List<ThirdPartyUserDTO> fetchUsers(SocialClientDO client) {
        // 兜底：无 dept 列表时自行拉取
        return fetchUsers(client, fetchDepartments(client));
    }

    /**
     * 拉取用户（传入已拉取好的部门列表，避免重复调用）
     */
    public List<ThirdPartyUserDTO> fetchUsers(SocialClientDO client, List<ThirdPartyDeptDTO> depts) {
        if (CollUtil.isEmpty(depts)) {
            log.warn("[DingTalk][fetchUsers] 部门列表为空，跳过用户拉取");
            return List.of();
        }
        String token = getAccessToken(client);
        List<ThirdPartyUserDTO> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        int deptIdx = 0;
        for (ThirdPartyDeptDTO dept : depts) {
            deptIdx++;
            if (deptIdx % 10 == 0) {
                log.info("[DingTalk][fetchUsers] 进度: {}/{} 个部门, 已收集 {} 个用户",
                        deptIdx, depts.size(), result.size());
            }
            fetchUsersByDept(token, Long.parseLong(dept.getSourceDeptId()), result, seen);
        }

        log.info("[DingTalk][fetchUsers] 共拉取 {} 个用户", result.size());
        return result;
    }

    // ==================== Token 管理 ====================

    private String getAccessToken(SocialClientDO client) {
        String appKey = client.getClientId();

        // 检查缓存
        TokenCache cache = tokenCache.get(appKey);
        if (cache != null && System.currentTimeMillis() < cache.expireTime) {
            return cache.token;
        }

        // 请求新 token（新版 OAuth2 API，token 携带 scope 信息）
        Map<String, String> body = Map.of("appKey", appKey, "appSecret", client.getClientSecret());
        HttpResponse resp = HttpRequest.post(GET_TOKEN_URL)
                .body(JsonUtils.toJsonString(body))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);

        if (json == null || json.get("accessToken") == null) {
            log.error("[DingTalk][getAccessToken] 获取失败: {}", resp.body());
            return null;
        }

        String token = json.get("accessToken").asText();
        long expiresIn = json.get("expireIn").asLong(7200);
        log.info("[DingTalk][getAccessToken] 获取成功, token={}", token);
        tokenCache.put(appKey, new TokenCache(token, System.currentTimeMillis() + expiresIn * 1000 - 60_000));
        return token;
    }

    // ==================== 部门 ====================

    /**
     * 递归拉取部门树：使用 listsub 接口，一次调用返回子部门的完整信息
     * （dept_id、name、parent_id、order），无需再逐个调用 get 接口，
     * 彻底解决 N+1 调用导致的 QPS 限流问题。
     */
    private void fetchDeptTree(String token, Long deptId, List<ThirdPartyDeptDTO> result) {
        List<ThirdPartyDeptDTO> children = fetchSubDepts(token, deptId);
        if (CollUtil.isEmpty(children)) return;

        for (ThirdPartyDeptDTO child : children) {
            result.add(child);
            // 递归拉子部门
            fetchDeptTree(token, Long.parseLong(child.getSourceDeptId()), result);
        }
    }

    /**
     * 调用 listsub 接口，一次获取指定部门下的所有子部门（含完整信息）
     */
    private List<ThirdPartyDeptDTO> fetchSubDepts(String token, Long deptId) {
        String url = DEPT_LISTSUB_URL + "?access_token=" + token;
        HttpResponse resp = HttpRequest.post(url)
                .body(JsonUtils.toJsonString(Map.of("dept_id", deptId)))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("errcode").asInt() != 0) {
            log.warn("[DingTalk][fetchSubDepts] deptId={} 拉取失败: {}", deptId, resp.body());
            return List.of();
        }
        JsonNode deptArray = json.get("result");
        if (deptArray == null || !deptArray.isArray()) return List.of();

        List<ThirdPartyDeptDTO> list = new ArrayList<>();
        for (JsonNode dept : deptArray) {
            ThirdPartyDeptDTO dto = new ThirdPartyDeptDTO();
            dto.setSourceDeptId(String.valueOf(dept.get("dept_id").asLong()));
            dto.setSourceParentId(String.valueOf(dept.get("parent_id").asLong()));
            dto.setName(dept.get("name").asText());
            dto.setSort(dept.has("order") ? dept.get("order").asInt() : 0);
            list.add(dto);
        }
        return list;
    }

    // ==================== 用户 ====================

    private void fetchUsersByDept(String token, Long deptId,
                                   List<ThirdPartyUserDTO> result, Set<String> seenUserIds) {
        int cursor = 0;
        int size = 100;

        while (true) {
            String url = USER_LIST_URL + "?access_token=" + token;
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("dept_id", deptId);
            body.put("cursor", cursor);
            body.put("size", size);

            HttpResponse resp = HttpRequest.post(url)
                    .body(JsonUtils.toJsonString(body))
                    .execute();
            JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
            if (json == null || json.get("errcode").asInt() != 0) {
                log.warn("[DingTalk][fetchUsersByDept] deptId={} 拉取失败: {}", deptId, resp.body());
                break;
            }

            JsonNode resultNode = json.get("result");
            if (resultNode == null || !resultNode.has("list")) break;

            JsonNode userList = resultNode.get("list");
            if (userList == null || !userList.isArray() || userList.size() == 0) break;

            for (JsonNode user : userList) {
                String userId = user.get("userid").asText();
                if (!seenUserIds.add(userId)) continue; // 去重

                // 拉取用户详情（含手机号、unionid）
                ThirdPartyUserDTO dto = fetchUserDetail(token, userId);
                if (dto != null) {
                    // 补充部门信息
                    List<String> deptIds = new ArrayList<>();
                    deptIds.add(String.valueOf(deptId));
                    dto.setDeptIds(deptIds);
                    result.add(dto);
                }
                // QPS 控制：每次用户详情调用后等待
                safeSleep(USER_DETAIL_DELAY_MS);
            }

            // 翻页
            if (!resultNode.has("has_more") || !resultNode.get("has_more").asBoolean()) break;
            cursor = resultNode.get("next_cursor").asInt();
        }
    }

    private ThirdPartyUserDTO fetchUserDetail(String token, String userId) {
        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            String url = USER_DETAIL_URL + "?access_token=" + token;
            Map<String, Object> body = Map.of("userid", userId);

            HttpResponse resp = HttpRequest.post(url)
                    .body(JsonUtils.toJsonString(body))
                    .execute();
            JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
            if (json == null) {
                log.warn("[DingTalk][fetchUserDetail] userId={} 响应为空，重试 {}/{}", userId, retry + 1, MAX_RETRIES);
                sleepBeforeRetry(retry);
                continue;
            }
            int errcode = json.get("errcode").asInt();
            if (errcode == 0) {
                JsonNode result = json.get("result");
                ThirdPartyUserDTO dto = new ThirdPartyUserDTO();
                dto.setSourceUserId(result.get("userid").asText());
                dto.setOpenid(result.has("unionid") ? result.get("unionid").asText() : result.get("userid").asText());
                dto.setNickname(result.get("name").asText());
                dto.setAvatar(result.has("avatar") ? result.get("avatar").asText() : null);
                dto.setMobile(result.has("mobile") ? result.get("mobile").asText() : null);
                dto.setEmail(result.has("email") ? result.get("email").asText() : null);
                return dto;
            }
            // errcode=88 表示 QPS 限流，需要退避重试
            if (errcode == 88) {
                JsonNode subCode = json.get("sub_code");
                log.warn("[DingTalk][fetchUserDetail] userId={} QPS限流(sub_code={}), 重试 {}/{}",
                        userId, subCode != null ? subCode.asText() : "N/A", retry + 1, MAX_RETRIES);
                sleepBeforeRetry(retry);
                continue;
            }
            log.warn("[DingTalk][fetchUserDetail] userId={} 拉取失败: {}", userId, resp.body());
            return null;
        }
        log.error("[DingTalk][fetchUserDetail] userId={} 重试{}次后仍失败", userId, MAX_RETRIES);
        return null;
    }

    // ==================== 限流与重试工具方法 ====================

    /**
     * 被限流后的指数退避等待
     * @param retry 当前重试次数（0-based）
     */
    private void sleepBeforeRetry(int retry) {
        long waitMs = RATE_LIMIT_BASE_WAIT_MS * (1L << retry); // 1s, 2s, 4s
        safeSleep(waitMs);
    }

    /**
     * 安全的 sleep，忽略中断
     */
    private void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[DingTalk][safeSleep] 线程被中断");
        }
    }

    // ==================== 内部类 ====================

    private record TokenCache(String token, long expireTime) {}

}
