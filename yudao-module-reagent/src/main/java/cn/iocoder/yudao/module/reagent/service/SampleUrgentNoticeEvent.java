package cn.iocoder.yudao.module.reagent.service;

/** 提交后通知，操作记录编号作为请求幂等标识。 */
public record SampleUrgentNoticeEvent(Long tenantId, Long urgentId, Long logId,
                                     Long actorId, String action, String content, boolean deadlineChanged) {
    public SampleUrgentNoticeEvent(Long tenantId, Long urgentId, Long logId, Long actorId,
                                  String action, String content) {
        this(tenantId, urgentId, logId, actorId, action, content, false);
    }
}
