package cn.iocoder.yudao.module.system.sync.erp.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.system.sync.erp.service.ErpSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 老 ERP → staging 中间库 同步定时任务
 *
 * 触发：基础设施-定时任务 新增任务，Handler 名称选 erpSyncJob。
 * 参数 param 支持三种（手动触发为主，不设自动 cron）：
 *   N          —— 回溯 N 天（默认 1，只拉今天）
 *   N:offset   —— 分段回填：每段 N 天，从今天往前推 offset 天为终点。如 60:0=最近60天，60:60=往前第2段
 *   begin~end  —— 显式日期范围，如 2023-04-01~2023-08-31（老库脆时按段补历史最稳）
 *
 * @author yudao
 */
@Component
@Slf4j
public class ErpSyncJob implements JobHandler {

    @Resource
    private ErpSyncService erpSyncService;

    @Override
    public String execute(String param) throws Exception {
        log.info("[ErpSyncJob] 触发，param={}", param);
        return erpSyncService.sync(param);
    }

}
