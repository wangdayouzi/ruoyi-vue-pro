package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.quartz.core.scheduler.SchedulerManager;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.job.JobDO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.enums.job.JobStatusEnum;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** SQL只登记任务配置；启动时补齐JDBC Quartz调度，不覆盖管理员设置的时间和暂停状态。 */
@Component
@RequiredArgsConstructor
public class SampleUrgentPlanJobInstaller {
    private static final String HANDLER = "sampleUrgentPlanGenerateJob";
    private final JobMapper jobs;
    private final JobService service;
    private final Scheduler scheduler;
    private final SchedulerManager manager;
    private final JdbcTemplate jdbc;
    private final PlatformTransactionManager transactions;

    @EventListener(ApplicationReadyEvent.class)
    @TenantIgnore
    public void install() {
        var job = new TransactionTemplate(transactions).execute(tx -> {
            jdbc.execute("SELECT pg_advisory_xact_lock(7240202611191)");
            try {
                var existing = jobs.selectByHandlerName(HANDLER);
                if (existing == null) {
                    var req = new JobSaveReqVO(); req.setName("样品加急入库周期预约生成"); req.setHandlerName(HANDLER);
                    req.setHandlerParam(""); req.setCronExpression("0 5 0 * * ?");
                    req.setRetryCount(0); req.setRetryInterval(0); req.setMonitorTimeout(0);
                    return jobs.selectById(service.createJob(req));
                }
                if (!scheduler.checkExists(new JobKey(HANDLER)) || !scheduler.checkExists(new TriggerKey(HANDLER))) {
                    manager.deleteJob(HANDLER);
                    manager.addJob(existing.getId(), HANDLER, existing.getHandlerParam(), existing.getCronExpression(),
                            existing.getRetryCount(), existing.getRetryInterval());
                    if (existing.getStatus().equals(JobStatusEnum.STOP.getStatus())) manager.pauseJob(HANDLER);
                }
                if (existing.getStatus().equals(JobStatusEnum.INIT.getStatus())) {
                    existing.setStatus(JobStatusEnum.NORMAL.getStatus()); jobs.updateById(existing);
                }
                return existing;
            } catch (SchedulerException ex) {
                throw new IllegalStateException("周期预约任务登记失败，请检查基础设施-定时任务", ex);
            }
        });
        // 停机期间错过夜间执行时，在启动后补齐；管理员已暂停任务时不触发。
        if (job != null && job.getStatus().equals(JobStatusEnum.NORMAL.getStatus())) {
            try { service.triggerJob(job.getId()); }
            catch (SchedulerException ex) { throw new IllegalStateException("周期预约启动补齐任务触发失败", ex); }
        }
    }
}
