package cn.iocoder.yudao.module.system.api.dingtalk.dto;

/** SDK 无关的事件信封；dataJson 保留原始业务数据。事件可能重复投递。 */
public record DingTalkEventDTO(String eventId, String eventType, String corpId,
                               String appId, Long bornTime, String dataJson) {
}
