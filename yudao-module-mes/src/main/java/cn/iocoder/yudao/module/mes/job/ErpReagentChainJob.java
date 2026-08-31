package cn.iocoder.yudao.module.mes.job;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.mes.service.wm.erp.ErpPushService;
import cn.iocoder.yudao.module.reagent.service.ReagentBaseFlatSyncService;
import cn.iocoder.yudao.module.system.sync.erp.service.ErpSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 老ERP → 试剂基础数据 链式同步任务
 *
 * <p>一条任务把整条链路串起来（顺序执行，避免三个独立任务 race）：
 * 1) erpSyncJob：老库 → staging（回溯 N 天，走索引增量）
 * 2) erpPushJob：staging → master（推 pushed=0 的行；老系统改单因 data_hash 变化被置 pushed=0 重推）
 * 3) reagentBaseFlatSyncJob：master → reagent_base_flat（增量水位；full 前缀=全量+停用缺失）
 *
 * <p>Handler 参数格式（填在定时任务「处理参数」）：
 * <pre>[full:][<erpSync窗口>]<试剂分类关键词></pre>
 * - erpSync窗口: 可选，透传给 erpSync 的同步窗口，支持：
 *   - N / N天    回溯 N 天（默认 1）
 *   - N:offset   N 天段，从今天往前推 offset 天为终点（如 365:365 = 一年前~两年前）
 *   - begin~end  显式日期范围（如 2024-08-28~2025-08-28）
 * - full: 可选，试剂全量（忽略水位 + 停用源中缺失的行）
 * - 关键词：试剂分类关键词，逗号分隔，如 试剂,标准品；留空用默认
 *
 * <p>示例：
 * <ul>
 *   <li>试剂,标准品                   → 回溯 1 天，试剂增量（10 分钟 cron 用）</li>
 *   <li>30:试剂                       → 回溯 30 天，试剂增量</li>
 *   <li>365:365:试剂                  → 一年前~两年前 的试剂增量</li>
 *   <li>2024-08-28~2025-08-28:试剂    → 该日期范围的试剂增量</li>
 *   <li>full:30:试剂                  → 回溯 30 天，试剂全量 + 停用缺失</li>
 * </ul>
 *
 * @author yudao
 */
@Component
@Slf4j
public class ErpReagentChainJob implements JobHandler {

    /** 默认试剂分类关键词（参数留空时透传，避免试剂同步因空参报错） */
    private static final String DEFAULT_KEYWORDS = "试剂,标准品,血清,基质,切片,耗材,药品,危化,化学品,PBMC,生物,样本";

    @Resource
    private ErpSyncService erpSyncService;
    @Resource
    private ErpPushService erpPushService;
    @Resource
    private ReagentBaseFlatSyncService reagentBaseFlatSyncService;

    @Override
    public String execute(String param) throws Exception {
        // 解析参数：[full:][<erpSync窗口>]<关键词>
        String p = StrUtil.nullToEmpty(param).trim();
        boolean full = StrUtil.startWithIgnoreCase(p, "full:");
        if (full) {
            p = StrUtil.removePrefixIgnoreCase(p, "full:").trim();
        }
        // 关键词在最后一段（关键字不含冒号，用最后一个冒号切分；窗口内可含冒号如 365:365）
        String window;
        String keywords;
        int idx = p.lastIndexOf(':');
        if (idx >= 0) {
            window = p.substring(0, idx).trim();
            keywords = p.substring(idx + 1).trim();
        } else {
            window = "";
            keywords = p;
        }
        if (StrUtil.isBlank(window)) {
            window = "1"; // 默认回溯 1 天
        } else if (window.matches("\\d+\\s*天")) { // 兼容旧写法 "30天" → "30"
            window = window.substring(0, window.length() - 1).trim();
        }
        if (StrUtil.isBlank(keywords)) {
            keywords = DEFAULT_KEYWORDS;
        }
        String reagentParam = full ? "full:" + keywords : keywords;
        log.info("[erpReagentChainJob] 触发，full={}，erpSync窗口={}，关键词={}", full, window, keywords);
        // 1) 老库 → staging（窗口由 erpSync 解析：N / N:offset / begin~end；变更检测：data_hash 变化 → 置 pushed=0）
        String step1 = erpSyncService.sync(window);
        // 2) staging → master（推 pushed=0 的行）
        String step2 = erpPushService.push(1000);
        // 3) master → reagent_base_flat（增量水位；full: 前缀=全量+停用缺失）
        String step3 = reagentBaseFlatSyncService.sync(reagentParam);
        log.info("[erpReagentChainJob] 完成");
        return "链式同步完成（full=" + full + ", erpSync窗口=" + window + "）：\n1) " + step1 + "\n2) " + step2 + "\n3) " + step3;
    }

}
