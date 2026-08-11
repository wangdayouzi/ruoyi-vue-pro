package cn.iocoder.yudao.module.system.service.oauth2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.framework.oauth2.DingTalkOAuthProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.AUTH_THIRD_LOGIN_NOT_BIND;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * 钉钉新版 OAuth2 服务（login.dingtalk.com）
 *
 * 替代 JustAuth 的钉钉 OAuth，支持账号密码 + 扫码登录。
 *
 * @author yudao
 */
@Service
@Slf4j
public class DingTalkOAuthService {

    private static final String AUTHORIZE_URL  = "https://login.dingtalk.com/oauth2/auth";
    private static final String TOKEN_URL      = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
    private static final String USER_ME_URL    = "https://api.dingtalk.com/v1.0/contact/users/me";

    private final DingTalkOAuthProperties properties;
    private final SocialUserMapper socialUserMapper;
    private final SocialUserBindMapper socialUserBindMapper;
    private final AdminUserMapper adminUserMapper;
    private final OAuth2TokenService oauth2TokenService;

    public DingTalkOAuthService(DingTalkOAuthProperties properties,
                                 SocialUserMapper socialUserMapper,
                                 SocialUserBindMapper socialUserBindMapper,
                                 AdminUserMapper adminUserMapper,
                                 OAuth2TokenService oauth2TokenService) {
        this.properties = properties;
        this.socialUserMapper = socialUserMapper;
        this.socialUserBindMapper = socialUserBindMapper;
        this.adminUserMapper = adminUserMapper;
        this.oauth2TokenService = oauth2TokenService;
    }

    /** 获取前端地址 */
    public String getFrontendUrl() {
        return properties.getFrontendUrl();
    }

    /** 构建钉钉授权页 URL */
    public String buildAuthorizeUrl(String redirect) {
        // 将前端重定向地址编码进 state，随 OAuth2 流程原样带回，避免钉钉回调丢失 redirect 参数
        // （移动端 H5 场景下，redirect 形如 /h5/#/pages-core/auth/login；管理后台为空时默认为 /）
        String state = StrUtil.blankToDefault(redirect, "/");
        String redirectUri = URLEncoder.encode(properties.getRedirectUri(), StandardCharsets.UTF_8);
        return AUTHORIZE_URL +
                "?redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&client_id=" + properties.getClientId() +
                "&scope=openid" +
                "&prompt=consent" +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
    }

    /** 解析回调 state 参数，得到前端重定向地址；解析失败或为空时，回退到默认值 */
    public String parseRedirectFromState(String state, String defaultRedirect) {
        if (StrUtil.isNotBlank(state)) {
            try {
                return URLDecoder.decode(state, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("[DingTalkOAuth] state 解析失败: {}", state, e);
            }
        }
        return defaultRedirect;
    }

    /** 回调处理：authCode → 用户信息 → 返回登录 token */
    public AuthLoginRespVO handleCallback(String authCode) {
        // 1. authCode 换 accessToken
        String accessToken = exchangeToken(authCode);
        // 2. 获取用户信息
        JsonNode userInfo = getUserInfo(accessToken);
        String openid = userInfo.get("openId").asText();
        String unionid = userInfo.has("unionId") ? userInfo.get("unionId").asText() : openid;
        String nickname = userInfo.get("nick").asText();
        String avatar = userInfo.has("avatarUrl") ? userInfo.get("avatarUrl").asText() : null;

        // 3. 查 social_user 并绑定
        Long userId = resolveUserId(openid, unionid, nickname, avatar);
        if (userId == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 4. 创建 token
        OAuth2AccessTokenDO token = oauth2TokenService.createAccessToken(
                userId, UserTypeEnum.ADMIN.getValue(), "default", null);
        return BeanUtil.toBean(token, AuthLoginRespVO.class);
    }

    private String exchangeToken(String authCode) {
        Map<String, String> body = Map.of("clientId", properties.getClientId(),
                "clientSecret", properties.getClientSecret(),
                "code", authCode,
                "grantType", "authorization_code");
        HttpResponse resp = HttpRequest.post(TOKEN_URL)
                .body(JsonUtils.toJsonString(body))
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("accessToken") == null) {
            log.error("[DingTalkOAuth] 换token失败: {}", resp.body());
            throw new RuntimeException("钉钉登录失败: " + resp.body());
        }
        return json.get("accessToken").asText();
    }

    private JsonNode getUserInfo(String accessToken) {
        HttpResponse resp = HttpRequest.get(USER_ME_URL)
                .header("x-acs-dingtalk-access-token", accessToken)
                .execute();
        JsonNode json = JsonUtils.parseObject(resp.body(), JsonNode.class);
        if (json == null || json.get("openId") == null) {
            log.error("[DingTalkOAuth] 获取用户信息失败: {}", resp.body());
            throw new RuntimeException("获取钉钉用户信息失败");
        }
        return json;
    }

    private Long resolveUserId(String openid, String unionid, String nickname, String avatar) {
        // 用 openid + unionid 查找已有的 social_user
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndOpenid(
                SocialTypeEnum.DINGTALK.getType(), unionid);
        if (socialUser == null) {
            socialUser = socialUserMapper.selectByTypeAndOpenid(
                    SocialTypeEnum.DINGTALK.getType(), openid);
        }

        if (socialUser != null) {
            // 更新信息
            socialUser.setNickname(nickname);
            socialUser.setAvatar(avatar);
            socialUser.setOpenid(unionid);
            socialUserMapper.updateById(socialUser);

            SocialUserBindDO bind = socialUserBindMapper.selectByUserTypeAndSocialUserId(
                    UserTypeEnum.ADMIN.getValue(), socialUser.getId());
            if (bind != null && adminUserMapper.selectById(bind.getUserId()) != null) {
                return bind.getUserId();
            }
            // 绑定失效，清除
            if (bind != null) {
                socialUserBindMapper.deleteById(bind.getId());
            }
        }

        // 无绑定 → 创建新的 social_user 但不创建 system_user（需管理员手动绑定）
        if (socialUser == null) {
            socialUser = new SocialUserDO();
            socialUser.setType(SocialTypeEnum.DINGTALK.getType());
            socialUser.setOpenid(unionid);
            socialUser.setNickname(nickname);
            socialUser.setAvatar(avatar);
            socialUser.setToken("OAuth2");
            socialUser.setRawTokenInfo("{}");
            socialUser.setRawUserInfo("{}");
            socialUser.setCode("");
            socialUser.setState("");
            socialUserMapper.insert(socialUser);
        }

        // 新用户 / 绑定失效：返回 null，前端提示绑定
        return null;
    }

}
