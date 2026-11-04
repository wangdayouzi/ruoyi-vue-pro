package cn.iocoder.yudao.module.system.framework.dingtalk;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dingtalk.integration")
public class DingTalkIntegrationProperties {
    private boolean enabled;
    private boolean streamEnabled;
    private String clientId;
    @ToString.Exclude
    private String clientSecret;
    private int consumeThreads = 4;
    /** 不配置时，企业内部应用机器人默认与 Client ID 一致；独立机器人可显式覆盖。 */
    private String robotCode;

    public String getEffectiveRobotCode() {
        return robotCode == null || robotCode.isBlank() ? clientId : robotCode;
    }

    public void validateCredentials() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("钉钉集成启用时必须配置 client-id 和 client-secret");
        }
        if (consumeThreads < 1) {
            throw new IllegalStateException("钉钉 consume-threads 必须大于 0");
        }
    }
}
