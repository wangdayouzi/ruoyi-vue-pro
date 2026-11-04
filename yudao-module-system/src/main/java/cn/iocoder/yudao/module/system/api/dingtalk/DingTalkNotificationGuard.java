package cn.iocoder.yudao.module.system.api.dingtalk;

/** 定时发送前由所属业务重新确认单据是否仍需提醒。 */
public interface DingTalkNotificationGuard {
    String businessType();
    default String displayName() { return businessType(); }
    boolean applicable(String businessId, String version);
}
