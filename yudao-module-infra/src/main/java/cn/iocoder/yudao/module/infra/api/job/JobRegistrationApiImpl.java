package cn.iocoder.yudao.module.infra.api.job;

import cn.iocoder.yudao.framework.quartz.core.scheduler.SchedulerManager;
import cn.iocoder.yudao.module.infra.api.job.dto.*;
import cn.iocoder.yudao.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.job.JobDO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobRegistrationLockMapper;
import cn.iocoder.yudao.module.infra.enums.job.JobStatusEnum;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class JobRegistrationApiImpl implements JobRegistrationApi {
    @Resource private JobMapper jobs;
    @Resource private JobRegistrationLockMapper locks;
    @Resource private JobService service;
    @Resource private Scheduler scheduler;
    @Resource private SchedulerManager manager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobRegistrationRespDTO ensureRegistered(JobRegistrationReqDTO request) {
        // 锁覆盖数据库登记与 Quartz 修复，避免多个实例启动时重复创建同一 handler。
        locks.lockHandler("infra-job-registration:" + request.handlerName());
        try {
            JobDO job = jobs.selectByHandlerName(request.handlerName());
            if (job == null) {
                JobSaveReqVO create = new JobSaveReqVO();
                create.setName(request.name());
                create.setHandlerName(request.handlerName());
                create.setHandlerParam("");
                create.setCronExpression(request.cronExpression());
                create.setRetryCount(request.retryCount());
                create.setRetryInterval(request.retryInterval());
                create.setMonitorTimeout(0);
                job = jobs.selectById(service.createJob(create));
            } else {
                if (!scheduler.checkExists(new JobKey(job.getHandlerName()))
                        || !scheduler.checkExists(new TriggerKey(job.getHandlerName()))) {
                    manager.deleteJob(job.getHandlerName());
                    manager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                            job.getRetryCount(), job.getRetryInterval());
                    if (JobStatusEnum.STOP.getStatus().equals(job.getStatus())) manager.pauseJob(job.getHandlerName());
                }
                if (JobStatusEnum.INIT.getStatus().equals(job.getStatus())) {
                    job.setStatus(JobStatusEnum.NORMAL.getStatus());
                    jobs.updateById(job);
                }
            }
            return new JobRegistrationRespDTO(job.getId(), JobStatusEnum.NORMAL.getStatus().equals(job.getStatus()));
        } catch (SchedulerException exception) {
            throw new IllegalStateException("定时任务登记失败，请检查基础设施-定时任务", exception);
        }
    }

    @Override
    public void triggerIfEnabled(Long id) {
        JobDO job = jobs.selectById(id);
        if (job == null || !JobStatusEnum.NORMAL.getStatus().equals(job.getStatus())) return;
        try {
            service.triggerJob(id);
        } catch (SchedulerException exception) {
            throw new IllegalStateException("启动补齐任务触发失败", exception);
        }
    }
}
