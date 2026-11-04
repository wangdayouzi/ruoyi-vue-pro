package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.hutool.http.HttpRequest;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkAccessTokenApi;
import cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkIntegrationProperties;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DingTalkAccessTokenService implements DingTalkAccessTokenApi {
    private final DingTalkIntegrationProperties properties;
    private String token;
    private long expiresAt;

    @Override
    public synchronized void invalidateAccessToken() {
        token = null;
        expiresAt = 0;
    }

    @Override
    public synchronized String getAccessToken() {
        if (!properties.isEnabled()) throw new IllegalStateException("钉钉集成未启用");
        properties.validateCredentials();
        if (token != null && System.nanoTime() < expiresAt) return token;
        try (var response = HttpRequest.post("https://api.dingtalk.com/v1.0/oauth2/accessToken")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(Map.of("appKey", properties.getClientId(),
                        "appSecret", properties.getClientSecret())))
                .timeout(10000).execute()) {
            if (!response.isOk()) throw new IllegalStateException();
            JSONObject body = JSON.parseObject(response.body());
            String value = body.getString("accessToken");
            Long seconds = body.getLong("expireIn");
            if (value == null || value.isBlank() || seconds == null || seconds <= 0) {
                throw new IllegalStateException();
            }
            token = value;
            expiresAt = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(
                    Math.max(0, seconds - Math.min(120, seconds / 2)));
            return token;
        } catch (Exception ex) {
            throw new IllegalStateException("获取钉钉应用凭证失败，请检查配置、权限和网络");
        }
    }
}
