package cn.iocoder.yudao.module.system.sync.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.system.sync.service.ThirdPartySyncService;
import cn.iocoder.yudao.module.system.sync.strategy.ThirdPartySyncStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 第三方平台（钉钉/企业微信/飞书）部门&用户同步定时任务
 *
 * 建议 Cron：0 0 2 * * ? （每天凌晨2点执行）
 *
 * @author yudao
 */
@Component
@Slf4j
public class ThirdPartySyncJob implements JobHandler {

    @Resource
    private ThirdPartySyncService syncService;

    /**
     * Spring 自动注入所有 ThirdPartySyncStrategy 实现类
     */
    @Resource
    private List<ThirdPartySyncStrategy> strategies;

    @Override
    @TenantJob // 自动遍历所有租户执行
    public String execute(String param) throws Exception {
        if (strategies == null || strategies.isEmpty()) {
            log.info("[ThirdPartySyncJob] 无同步策略实现，跳过");
            return "无同步策略";
        }

        StringBuilder result = new StringBuilder();
        for (ThirdPartySyncStrategy strategy : strategies) {
            try {
                syncService.sync(strategy);
                result.append(strategy.getSocialType()).append(":OK; ");
            } catch (Exception e) {
                log.error("[ThirdPartySyncJob] [{}] 同步异常", strategy.getSocialType(), e);
                result.append(strategy.getSocialType()).append(":ERR(").append(e.getMessage()).append("); ");
            }
        }
        return result.toString();
    }

}
