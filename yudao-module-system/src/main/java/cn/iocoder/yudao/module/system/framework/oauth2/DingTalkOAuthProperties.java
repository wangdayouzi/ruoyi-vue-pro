package cn.iocoder.yudao.module.system.framework.oauth2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 钉钉新版 OAuth2 配置（login.dingtalk.com）
 *
 * @author yudao
 */
@Data
@Component
@ConfigurationProperties(prefix = "dingtalk.oauth2")
public class DingTalkOAuthProperties {

    /** 应用 Client ID（新版钉钉开放平台） */
    private String clientId;

    /** 应用 Client Secret */
    private String clientSecret;

    /** 授权回调地址（需与钉钉开放平台配置一致） */
    private String redirectUri;

    /** 前端地址（登录成功后重定向用，如 https://cms.accurantbio.com:64234） */
    private String frontendUrl;

}
