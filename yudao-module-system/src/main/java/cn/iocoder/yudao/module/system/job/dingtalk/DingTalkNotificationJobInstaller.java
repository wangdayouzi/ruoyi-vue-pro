package cn.iocoder.yudao.module.system.job.dingtalk;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** 首次安装通过原任务服务同时登记列表及 Quartz，不直接写 Quartz 系统表。 */
@Component
@RequiredArgsConstructor
public class DingTalkNotificationJobInstaller {
    private final JobMapper jobs;
    private final JobService service;
    private final JdbcTemplate jdbc;
    private final PlatformTransactionManager transactionManager;
    @EventListener(ApplicationReadyEvent.class) @TenantIgnore
    public void install() {
        new TransactionTemplate(transactionManager).executeWithoutResult(tx -> {
            jdbc.execute("SELECT pg_advisory_xact_lock(7240192601102)");
            if(jobs.selectByHandlerName("dingTalkNotificationSendJob")!=null) return;
            var req=new JobSaveReqVO();
            req.setName("钉钉通知发送"); req.setHandlerName("dingTalkNotificationSendJob");
            req.setHandlerParam(""); req.setCronExpression("0/30 * * * * ?");
            req.setRetryCount(0); req.setRetryInterval(0); req.setMonitorTimeout(0);
            try { service.createJob(req); }
            catch(org.quartz.SchedulerException ex) { throw new IllegalStateException("钉钉通知任务登记失败",ex); }
        });
    }
}
