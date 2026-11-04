package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.reagent.service.ReagentBasicDataSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 临床方案 / 项目号（老样本系统 MySQL → 本地）同步定时任务
 *
 * <p>触发：基础设施 - 定时任务，Handler 名称 reagentBasicDataSyncJob。
 * 默认 cron：0 0 0 * * ?（每天 00:00 同步一次）；无需 Handler 参数。
 * 数据：think_clinical → reagent_clinical，think_items（left join think_clinical）→ reagent_item；
 * 按源表 id 幂等 upsert，源表已删除的行停用（status=1）。
 * 临床方案的 is_auto_receive（是否自动发送寄送通知邮件）由本地维护，同步不覆盖。
 *
 * @author yudao
 */
@Component
@Slf4j
public class ReagentBasicDataSyncJob implements JobHandler {

    @Resource
    private ReagentBasicDataSyncService reagentBasicDataSyncService;

    @Override
    public String execute(String param) {
        log.info("[reagentBasicDataSyncJob] 触发，param={}", param);
        return reagentBasicDataSyncService.sync();
    }

}
