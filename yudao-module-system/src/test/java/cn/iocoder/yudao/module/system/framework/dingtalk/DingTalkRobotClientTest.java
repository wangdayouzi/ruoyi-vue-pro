package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkAccessTokenApi;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkRobotClientTest {
    private final DingTalkAccessTokenApi tokens = mock(DingTalkAccessTokenApi.class);
    private final DingTalkRobotClient client = new DingTalkRobotClient(tokens);
    @Test
    void recordsAcceptedInvalidAndRateLimitedRecipientsIndividually() {
        var result = client.decode(200, "{\"processQueryKey\":\"p1\",\"invalidStaffIdList\":[\"bad\"],\"flowControlledStaffIdList\":[\"slow\"]}", List.of("ok", "bad", "slow"));
        assertEquals(1, result.get("ok").status());
        assertEquals("p1", result.get("ok").processQueryKey());
        assertEquals(2, result.get("bad").status());
        assertEquals("FLOW_CONTROLLED", result.get("slow").errorCode());
    }
    @Test
    void neverTreatsUnrecognizableOrMissingReceiptAsSuccess() {
        assertEquals(3, client.decode(200, "{}", List.of("u")).get("u").status());
        assertEquals(3, client.decode(200, "not json", List.of("u")).get("u").status());
        assertEquals(3, client.decode(503, "", List.of("u")).get("u").status());
    }
    @Test
    void invalidTokenClearsCacheWithoutExposingRemoteErrorContent() {
        var outcome = client.decode(400, "{\"code\":\"invalidParameter.token.invalid\",\"message\":\"secret-token\"}", List.of("u")).get("u");
        assertEquals(2, outcome.status());
        verify(tokens).invalidateAccessToken();
        assertFalse(outcome.errorMessage().contains("secret-token"));
    }
    @Test
    void tokenAcquisitionFailureDoesNotCallRemoteEndpoint() {
        when(tokens.getAccessToken()).thenThrow(new IllegalStateException("secret"));
        var outcome = client.send("robot", "sampleText", java.util.Map.of("content", "hi"), List.of("u")).get("u");
        assertEquals(2, outcome.status());
        assertEquals("TOKEN_UNAVAILABLE", outcome.errorCode());
    }
}
