package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkEventHandler;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkEventDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkIntegrationTest {
    private final DingTalkEventDTO event = new DingTalkEventDTO("id", "change", "corp", "app", 1L, "{}");

    @Test
    void dispatchesOnlyMatchingHandlersAndPropagatesFailureForRetry() throws Exception {
        DingTalkEventHandler matching = mock(DingTalkEventHandler.class);
        DingTalkEventHandler other = mock(DingTalkEventHandler.class);
        when(matching.supports("change")).thenReturn(true);
        var dispatcher = new DingTalkEventDispatcher(List.of(matching, other));
        dispatcher.dispatch(event);
        verify(matching).handle(event);
        verify(other, never()).handle(any());
        doThrow(new IllegalStateException("business failure")).when(matching).handle(event);
        assertThrows(IllegalStateException.class, () -> dispatcher.dispatch(event));
    }

    @Test
    void disabledIntegrationDoesNotOpenConnection() {
        var lifecycle = new DingTalkStreamLifecycle(new DingTalkIntegrationProperties(),
                new DingTalkEventDispatcher(List.of()), List.of());
        lifecycle.start();
        assertFalse(lifecycle.isRunning());
        lifecycle.stop();
    }

    @Test
    void enabledStreamRejectsMissingCredentials() {
        var properties = new DingTalkIntegrationProperties();
        properties.setEnabled(true);
        properties.setStreamEnabled(true);
        var lifecycle = new DingTalkStreamLifecycle(properties, new DingTalkEventDispatcher(List.of()), List.of());
        assertThrows(IllegalStateException.class, lifecycle::start);
        assertFalse(lifecycle.isRunning());
    }
}
