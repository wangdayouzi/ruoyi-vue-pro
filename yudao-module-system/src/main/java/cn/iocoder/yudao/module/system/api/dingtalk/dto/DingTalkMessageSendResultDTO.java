package cn.iocoder.yudao.module.system.api.dingtalk.dto;

/** status: 0 处理中、1 钉钉已受理、2 失败、3 结果未知。受理不代表送达或已读。 */
public record DingTalkMessageSendResultDTO(Long logId, Long userId, Integer status, String errorMessage) { }
