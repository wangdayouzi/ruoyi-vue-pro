package cn.iocoder.yudao.module.system.api.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkEventDTO;

/** 业务模块注册 Spring Bean；按 eventId 幂等处理，成功返回后才确认消费。 */
public interface DingTalkEventHandler {
    boolean supports(String eventType);
    void handle(DingTalkEventDTO event) throws Exception;
}
