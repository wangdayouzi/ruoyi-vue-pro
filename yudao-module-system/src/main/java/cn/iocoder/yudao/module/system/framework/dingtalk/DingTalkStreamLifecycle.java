package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkCallbackHandler;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkEventDTO;
import com.alibaba.fastjson2.JSON;
import com.dingtalk.open.app.api.OpenDingTalkClient;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DingTalkStreamLifecycle implements SmartLifecycle {
    private final DingTalkIntegrationProperties properties;
    private final DingTalkEventDispatcher dispatcher;
    private final List<DingTalkCallbackHandler> callbackHandlers;
    private OpenDingTalkClient client;
    private volatile boolean running;

    @Override
    public synchronized void start() {
        if (running || !properties.isEnabled() || !properties.isStreamEnabled()) return;
        properties.validateCredentials();
        var builder = OpenDingTalkStreamClientBuilder.custom()
                .credential(new AuthClientCredential(properties.getClientId(), properties.getClientSecret()))
                .consumeThreads(properties.getConsumeThreads())
                .registerAllEventListener(event -> {
                    try {
                        dispatcher.dispatch(new DingTalkEventDTO(event.getEventId(), event.getEventType(),
                                event.getEventCorpId(), event.getEventUnifiedAppId(), event.getEventBornTime(),
                                JSON.toJSONString(event.getData())));
                        return EventAckStatus.SUCCESS;
                    } catch (Exception ex) {
                        // 不输出异常正文，避免 SDK 或业务异常携带凭证和事件内容。
                        log.warn("钉钉事件处理失败，等待重投: eventId={}, type={}, errorType={}",
                                event.getEventId(), event.getEventType(), ex.getClass().getSimpleName());
                        return EventAckStatus.LATER;
                    }
                });
        Set<String> topics = new HashSet<>();
        for (DingTalkCallbackHandler handler : callbackHandlers) {
            String topic = handler.topic();
            if (topic == null || topic.isBlank() || !topics.add(topic)) {
                throw new IllegalStateException("钉钉回调 topic 必须非空且唯一");
            }
            builder.registerCallbackListener(topic, (Object request) -> {
                try {
                    return JSON.parse(handler.handle(JSON.toJSONString(request)));
                } catch (Exception ex) {
                    throw new IllegalStateException("钉钉回调处理失败");
                }
            });
        }
        client = builder.build();
        try {
            client.start();
            running = true;
            log.info("钉钉 Stream 客户端已启动，事件订阅通道已注册，请在钉钉后台验证通道联通性");
        } catch (Exception ex) {
            stop();
            throw new IllegalStateException("钉钉 Stream 启动失败，请检查凭证和网络");
        }
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (client != null) {
            try { client.stop(); }
            catch (Exception ex) { log.warn("钉钉 Stream 关闭失败: {}", ex.getClass().getSimpleName()); }
            finally { client = null; }
        }
    }

    @Override
    public boolean isRunning() { return running; }
}
