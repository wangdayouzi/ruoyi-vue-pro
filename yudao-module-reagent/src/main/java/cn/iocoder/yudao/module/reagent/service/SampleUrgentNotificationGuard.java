package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationGuard;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentMapper;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SampleUrgentNotificationGuard implements DingTalkNotificationGuard {
    private final SampleUrgentMapper mapper;
    private final ConfigApi config;
    public String businessType() { return "sample-urgent-deadline"; }
    public String displayName() { return "加急入库即将逾期提醒"; }
    public boolean applicable(String businessId,String version) {
        if(businessId==null || !businessId.startsWith("sample-urgent:")) return false;
        if("false".equalsIgnoreCase(config.getConfigValueByKey("reagent.sample-urgent.dingtalk."+TenantContextHolder.getRequiredTenantId()+".enabled"))) return false;
        var row=mapper.selectById(Long.valueOf(businessId.substring("sample-urgent:".length())));
        return row!=null && row.getStatus()<3 && String.valueOf(row.getRevision()).equals(version)
                && row.getDeadline().isAfter(LocalDateTime.now());
    }
}
