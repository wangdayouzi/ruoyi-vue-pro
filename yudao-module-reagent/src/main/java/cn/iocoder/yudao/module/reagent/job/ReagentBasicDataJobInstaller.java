package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import cn.iocoder.yudao.module.infra.api.job.JobRegistrationApi;
import cn.iocoder.yudao.module.infra.api.job.dto.JobRegistrationReqDTO;

/** 基础资料同步任务只通过基础设施 API 登记，不直接操作 Quartz 或任务表。 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReagentBasicDataJobInstaller {
    private final JobRegistrationApi jobs;

    @EventListener(ApplicationReadyEvent.class)
    @TenantIgnore
    public void install() {
        try {
            jobs.ensureRegistered(new JobRegistrationReqDTO(
                    "临床方案/项目号同步 Job", "reagentBasicDataSyncJob", "0 0 0 * * ?", 3, 60000));
        } catch (RuntimeException exception) {
            // 基础资料同步仍可手工执行，登记失败留日志，不阻断其他业务启动。
            log.error("[reagent-basic-data-job-installer] 自动登记失败，请在定时任务中检查 reagentBasicDataSyncJob", exception);
        }
    }
}
