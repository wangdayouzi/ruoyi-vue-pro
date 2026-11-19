package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.reagent.service.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("sampleUrgentPlanGenerateJob")
@RequiredArgsConstructor
@Slf4j
public class SampleUrgentPlanGenerateJob implements JobHandler {
    private final SampleUrgentPlanMapper plans;
    private final SampleUrgentPlanGenerator generator;
    private final SampleUrgentPlanService service;
    @Value("${yudao.tenant.enable:true}") private boolean tenantEnabled;

    @Override
    @TenantIgnore
    public String execute(String param) {
        var today = SampleUrgentPlanRules.today();
        long cursor = 0;
        int processed = 0;
        var failures = new java.util.concurrent.atomic.AtomicInteger();
        while (true) {
            var rows = plans.selectList(new LambdaQueryWrapperX<SampleUrgentPlanDO>()
                    .gt(SampleUrgentPlanDO::getId, cursor).eq(SampleUrgentPlanDO::getStatus, 0)
                    .le(SampleUrgentPlanDO::getNextDate, today).orderByAsc(SampleUrgentPlanDO::getId).last("LIMIT 100"));
            if (rows.isEmpty()) {
                String result = "预约日期 " + today + "，检查计划 " + processed + " 个，失败 " + failures.get() + " 个";
                if (failures.get() > 0) throw new IllegalStateException(result + "；失败计划已暂停，请查看周期计划中的异常原因");
                return result;
            }
            for (var plan : rows) {
                processed++;
                cursor = plan.getId();
                TenantUtils.execute(tenantEnabled ? plan.getTenantId() : 1L, () -> {
                    try {
                        generator.generate(plan.getId(), today);
                    } catch (Exception ex) {
                        failures.incrementAndGet();
                        log.error("周期预约计划 {} 生成失败，暂停并保留原因", plan.getId(), ex);
                        service.pauseOnFailure(plan.getId(), plan.getRevision(), plan.getNextDate(), ex.getMessage());
                    }
                });
            }
        }
    }
}
