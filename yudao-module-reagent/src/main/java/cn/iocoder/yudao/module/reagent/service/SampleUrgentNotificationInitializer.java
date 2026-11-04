package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** 升级补齐历史未结束需求，仅启动时执行，不参与定时调度或发送。 */
@Component
@RequiredArgsConstructor
public class SampleUrgentNotificationInitializer {
    private final SampleUrgentMapper mapper;
    private final SampleUrgentNotifier notifier;
    @Value("${yudao.tenant.enable:true}") private boolean tenantEnabled;
    @EventListener(ApplicationReadyEvent.class) @TenantIgnore
    public void initialize() {
        long cursor=0;
        while(true) {
            var rows=mapper.selectList(new LambdaQueryWrapperX<SampleUrgentDO>()
                    .gt(SampleUrgentDO::getId,cursor).in(SampleUrgentDO::getStatus,0,1,2)
                    .gt(SampleUrgentDO::getDeadline,java.time.LocalDateTime.now())
                    .orderByAsc(SampleUrgentDO::getId).last("LIMIT 200"));
            if(rows.isEmpty()) return;
            for(var row:rows) {
                cursor=row.getId();
                long tenant=tenantEnabled?row.getTenantId():1L;
                TenantUtils.execute(tenant,()->notifier.initializeDeadline(row.getId()));
            }
        }
    }
}
