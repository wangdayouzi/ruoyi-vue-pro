package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 首次启动自动登记「临床方案 / 项目号」同步任务（每天 00:00）。
 *
 * <p>本项目 Quartz 使用 JDBC JobStore：仅往 infra_job 插数据不会创建调度，
 * 因此通过 JobService.createJob 同时登记任务与 Quartz（与 DingTalkNotificationJobInstaller 一致）。
 * 已存在（infra_job 有同 handler 记录）时跳过；多实例并发启动时由 Quartz 自身保证唯一。
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReagentBasicDataJobInstaller {

    private static final String HANDLER_NAME = "reagentBasicDataSyncJob";
    private static final String CRON = "0 0 0 * * ?";

    private final JobMapper jobMapper;
    private final JobService jobService;

    @EventListener(ApplicationReadyEvent.class)
    @TenantIgnore
    public void install() {
        try {
            doInstall();
        } catch (Exception ex) {
            // 登记失败不能影响应用启动：管理员仍可在「基础设施-定时任务」手工新增
            log.error("[reagent-basic-data-job-installer] 自动登记定时任务失败，请手工新增 Handler={}", HANDLER_NAME, ex);
        }
    }

    private void doInstall() {
        if (jobMapper.selectByHandlerName(HANDLER_NAME) != null) {
            return;
        }
        JobSaveReqVO reqVO = new JobSaveReqVO();
        reqVO.setName("临床方案/项目号同步 Job");
        reqVO.setHandlerName(HANDLER_NAME);
        reqVO.setHandlerParam("");
        reqVO.setCronExpression(CRON);
        reqVO.setRetryCount(3);
        reqVO.setRetryInterval(60000);
        reqVO.setMonitorTimeout(0);
        try {
            jobService.createJob(reqVO);
            log.info("[reagent-basic-data-job-installer] 已登记定时任务 {}", HANDLER_NAME);
        } catch (Exception ex) {
            // 并发启动时另一个实例可能已创建成功：以数据库是否已有记录为准
            if (jobMapper.selectByHandlerName(HANDLER_NAME) == null) {
                throw new IllegalStateException("登记定时任务失败", ex);
            }
        }
    }

}
