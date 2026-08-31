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
 * 老ERP → 试剂基础数据 链式同步任务（每晚全历史版）
 *
 * <p>与 {@link ErpReagentChainJob} 完全同逻辑，只是独立 Handler 名——因为 yudao 一个 Handler 只能配一条定时任务，
 * 10 分钟增量（erpReagentChainJob）和每晚全历史回溯（本任务）需要两个 Handler。
 *
 * <p>参数格式与 erpReagentChainJob 一致：[full:][<erpSync窗口>]<试剂分类关键词>
 * 每晚建议填 3650:试剂（回溯全历史≈10 年），把老系统改的任何旧单每晚重新拉齐；data_hash 只重推改动行。
 *
 * @author yudao
 */
@Component
@Slf4j
public class ErpReagentChainNightlyJob implements JobHandler {

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
        log.info("[erpReagentChainNightlyJob] 触发，full={}，erpSync窗口={}，关键词={}", full, window, keywords);
        // 1) 老库 → staging（窗口由 erpSync 解析：N / N:offset / begin~end；变更检测：data_hash 变化 → 置 pushed=0）
        String step1 = erpSyncService.sync(window);
        // 2) staging → master（推 pushed=0 的行）
        String step2 = erpPushService.push(1000);
        // 3) master → reagent_base_flat（增量水位；full: 前缀=全量+停用缺失）
        String step3 = reagentBaseFlatSyncService.sync(reagentParam);
        log.info("[erpReagentChainNightlyJob] 完成");
        return "链式同步完成（full=" + full + ", erpSync窗口=" + window + "）：\n1) " + step1 + "\n2) " + step2 + "\n3) " + step3;
    }

}
