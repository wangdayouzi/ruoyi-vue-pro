package cn.iocoder.yudao.module.system.framework.dingtalk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DingTalkConfigurationTest {
    private StandardEnvironment environment(boolean prod) throws Exception {
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().remove("systemEnvironment");
        env.getPropertySources().remove("systemProperties");
        var loader = new YamlPropertySourceLoader();
        Path resources = Path.of("../yudao-server/src/main/resources");
        for (var source : loader.load("base", new FileSystemResource(resources.resolve("application.yaml")))) {
            env.getPropertySources().addLast(source);
        }
        if (prod) {
            for (var source : loader.load("prod", new FileSystemResource(resources.resolve("application-prod.yaml")))) {
                env.getPropertySources().addFirst(source);
            }
        }
        env.getPropertySources().addFirst(new MapPropertySource("fake-login", Map.of(
                "dingtalk.oauth2.client-id", "login-app", "dingtalk.oauth2.client-secret", "test-secret")));
        return env;
    }

    @Test
    void productionEnablesStreamAndReusesLoginCredentials() throws Exception {
        var properties = Binder.get(environment(true)).bind("dingtalk.integration", DingTalkIntegrationProperties.class).get();
        assertTrue(properties.isEnabled());
        assertTrue(properties.isStreamEnabled());
        assertEquals("login-app", properties.getClientId());
        assertEquals("test-secret", properties.getClientSecret());
        properties.validateCredentials();
    }

    @Test
    void otherEnvironmentsRemainDisabled() throws Exception {
        var properties = Binder.get(environment(false)).bind("dingtalk.integration", DingTalkIntegrationProperties.class).get();
        assertFalse(properties.isEnabled());
        assertFalse(properties.isStreamEnabled());
    }

    @Test
    void environmentCanOverrideAppAndDisableProductionStream() throws Exception {
        var env = environment(true);
        env.getPropertySources().addFirst(new MapPropertySource("overrides", Map.of(
                "DINGTALK_INTEGRATION_CLIENT_ID", "separate-app",
                "DINGTALK_INTEGRATION_CLIENT_SECRET", "separate-secret",
                "DINGTALK_STREAM_ENABLED", "false")));
        var properties = Binder.get(env).bind("dingtalk.integration", DingTalkIntegrationProperties.class).get();
        assertEquals("separate-app", properties.getClientId());
        assertEquals("separate-secret", properties.getClientSecret());
        assertFalse(properties.isStreamEnabled());
    }
}
