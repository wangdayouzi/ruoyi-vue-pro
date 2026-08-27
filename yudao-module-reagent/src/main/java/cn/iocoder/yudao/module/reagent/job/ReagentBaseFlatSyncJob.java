package cn.iocoder.yudao.module.reagent.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.reagent.service.ReagentBaseFlatSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 老ERP → 试剂基础数据(扁平) 同步定时任务
 *
 * <p>触发：基础设施-定时任务 新增任务，Handler 名称选 reagentBaseFlatSyncJob，手动触发或配 cron。
 * Handler 参数（必填）：试剂分类关键词，逗号/顿号/分号/空格分隔，如 "试剂,标准品"；只同步分类名命中这些关键词（含子孙）的入库单明细；为空会报错提示。
 * 数据：正式库 mes_pm_inbound_line → 分类名命中关键词+子孙过滤 → 清洗（货号/过期）→ reagent_base_flat（按 src_line_id 幂等 upsert）。
 *
 * @author yudao
 */
@Component
@Slf4j
public class ReagentBaseFlatSyncJob implements JobHandler {

    @Resource
    private ReagentBaseFlatSyncService reagentBaseFlatSyncService;

    @Override
    public String execute(String param) throws Exception {
        log.info("[reagentBaseFlatSyncJob] 触发，param={}", param);
        return reagentBaseFlatSyncService.sync(param);
    }

}
