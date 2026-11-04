package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkEventHandler;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DingTalkEventDispatcher {
    private final List<DingTalkEventHandler> handlers;

    public void dispatch(DingTalkEventDTO event) throws Exception {
        log.info("钉钉 Stream 收到事件: eventId={}, type={}, receivedAt={}",
                event.eventId(), event.eventType(), java.time.Instant.now());
        for (DingTalkEventHandler handler : handlers) {
            if (handler.supports(event.eventType())) {
                handler.handle(event);
            }
        }
    }
}
