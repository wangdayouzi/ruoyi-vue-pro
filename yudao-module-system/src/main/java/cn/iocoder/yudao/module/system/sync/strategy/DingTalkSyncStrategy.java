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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
 * 2. 部门同步（listsubid + get 组合，共用 qyapi_get_department 权限）
 *    POST /topapi/v2/department/listsubid      — 获取子部门ID列表，参数 dept_id
 *    POST /topapi/v2/department/get            — 获取部门详情（名称/父部门/排序），参数 dept_id
 *
 * 3. 用户同步
 *    POST /topapi/user/listsimple            — 分页获取部门下用户（userid/name），参数 dept_id/cursor/size
 *    POST /topapi/v2/user/get                — 获取用户详情（手机号/邮箱/unionid），参数 userid
 *
 * 4. 钉钉后台需开通的权限 scope
 *    qyapi_gettoken                          获取 access_token
 *    qyapi_get_department_list               通讯录部门列表读取（listsub 接口）
 *    qyapi_get_department_detail             通讯录部门详情（按需）
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
    private static final String DEPT_LISTSUB_ID_URL  = "https://oapi.dingtalk.com/topapi/v2/department/listsubid";
    private static final String DEPT_GET_URL         = "https://oapi.dingtalk.com/topapi/v2/department/get";
    private static final String USER_LIST_URL        = "https://oapi.dingtalk.com/topapi/user/listsimple";
    private static final String USER_DETAIL_URL      = "https://oapi.dingtalk.com/topapi/v2/user/get";

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
        String token = getAccessToken(client);
        // 先拉所有部门，再按部门拉用户
        List<ThirdPartyDeptDTO> depts = fetchDepartments(client);
        List<ThirdPartyUserDTO> result = new ArrayList<>();
        java.util.Set<String> seenUserIds = new java.util.HashSet<>();

        for (ThirdPartyDeptDTO dept : depts) {
            fetchUsersByDept(token, Long.parseLong(dept.getSourceDeptId()), result, seenUserIds);
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
     * 递归拉取部门树：listsubid(仅取ID) + get(取详情)
     * listsubid 和 get 共用 qyapi_get_department 权限，避免 listsub 的额外 scope 问题
     */
    private void fetchDeptTree(String token, Long deptId, List<ThirdPartyDeptDTO> result) {
        // 1. 获取子部门ID列表
        List<Long> childIds = fetchSubDeptIds(token, deptId);
        if (CollUtil.isEmpty(childIds)) return;

        // 2. 逐个获取部门详情
        for (Long childId : childIds) {
            ThirdPartyDeptDTO dto = fetchDeptDetail(token, childId);
            if (dto != null) {
                result.add(dto);
                // 3. 递归拉子部门
                fetchDeptTree(token, childId, result);
            }
        }
    }

    private List<Long> fetchSubDeptIds(String token, Long deptId) {
        String url = DEPT_LISTSUB_ID_URL + "?access_token=" + token;
        HttpResponse resp = HttpRequest.post(url)
                .body(JsonUtils.toJsonString(Map.of("dept_id", deptId)))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("errcode").asInt() != 0) {
            log.warn("[DingTalk][fetchSubDeptIds] deptId={} 拉取失败: {}", deptId, resp.body());
            return List.of();
        }
        JsonNode idArray = json.get("result").get("dept_id_list");
        if (idArray == null || !idArray.isArray()) return List.of();
        List<Long> ids = new ArrayList<>();
        for (JsonNode idNode : idArray) {
            ids.add(idNode.asLong());
        }
        return ids;
    }

    private ThirdPartyDeptDTO fetchDeptDetail(String token, Long deptId) {
        String url = DEPT_GET_URL + "?access_token=" + token;
        HttpResponse resp = HttpRequest.post(url)
                .body(JsonUtils.toJsonString(Map.of("dept_id", deptId)))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("errcode").asInt() != 0) {
            log.warn("[DingTalk][fetchDeptDetail] deptId={} 拉取失败: {}", deptId, resp.body());
            return null;
        }
        JsonNode result = json.get("result");
        ThirdPartyDeptDTO dto = new ThirdPartyDeptDTO();
        dto.setSourceDeptId(String.valueOf(result.get("dept_id").asLong()));
        dto.setSourceParentId(String.valueOf(result.get("parent_id").asLong()));
        dto.setName(result.get("name").asText());
        dto.setSort(result.has("order") ? result.get("order").asInt() : 0);
        return dto;
    }

    // ==================== 用户 ====================

    private void fetchUsersByDept(String token, Long deptId,
                                   List<ThirdPartyUserDTO> result,
                                   java.util.Set<String> seenUserIds) {
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
            }

            // 翻页
            if (!resultNode.has("has_more") || !resultNode.get("has_more").asBoolean()) break;
            cursor = resultNode.get("next_cursor").asInt();
        }
    }

    private ThirdPartyUserDTO fetchUserDetail(String token, String userId) {
        String url = USER_DETAIL_URL + "?access_token=" + token;
        Map<String, Object> body = Map.of("userid", userId);

        HttpResponse resp = HttpRequest.post(url)
                .body(JsonUtils.toJsonString(body))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("errcode").asInt() != 0) {
            log.warn("[DingTalk][fetchUserDetail] userId={} 拉取失败: {}", userId, resp.body());
            return null;
        }

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

    // ==================== 内部类 ====================

    private record TokenCache(String token, long expireTime) {}

}
