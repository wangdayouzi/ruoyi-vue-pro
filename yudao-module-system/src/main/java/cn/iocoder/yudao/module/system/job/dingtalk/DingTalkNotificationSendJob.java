package cn.iocoder.yudao.module.system.job.dingtalk;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.service.dingtalk.DingTalkNotificationQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 部署级共享队列只调度一次；每条通知由队列按其租户恢复上下文。 */
@Component("dingTalkNotificationSendJob")
@RequiredArgsConstructor
public class DingTalkNotificationSendJob implements JobHandler {
    private final DingTalkNotificationQueueService queue;
    @Override @TenantIgnore
    public String execute(String param) { return queue.dispatch(); }
}
