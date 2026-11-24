package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import cn.iocoder.yudao.module.infra.api.job.JobRegistrationApi;
import cn.iocoder.yudao.module.infra.api.job.dto.JobRegistrationReqDTO;

/** 启动时登记周期预约任务，并在管理员未暂停时补齐停机期间的计划。 */
@Component
@RequiredArgsConstructor
public class SampleUrgentPlanJobInstaller {
    private final JobRegistrationApi jobs;

    @EventListener(ApplicationReadyEvent.class)
    @TenantIgnore
    public void install() {
        var job = jobs.ensureRegistered(new JobRegistrationReqDTO(
                "样品加急入库周期预约生成", "sampleUrgentPlanGenerateJob", "0 5 0 * * ?", 0, 0));
        if (job.enabled()) jobs.triggerIfEnabled(job.id());
    }
}
