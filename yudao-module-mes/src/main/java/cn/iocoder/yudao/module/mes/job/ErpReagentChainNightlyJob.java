package cn.iocoder.yudao.module.mes.job;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.mes.service.wm.erp.ErpPushService;
import cn.iocoder.yudao.module.reagent.service.ReagentBaseFlatSyncService;
import cn.iocoder.yudao.module.system.sync.erp.service.ErpSyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 老ERP → 试剂基础数据 链式同步任务（每晚全历史版）
 *
 * <p>与 {@link ErpReagentChainJob} 完全同逻辑，只是独立 Handler 名——因为 yudao 一个 Handler 只能配一条定时任务，
 * 10 分钟增量（erpReagentChainJob）和每晚全历史回溯（本任务）需要两个 Handler。
 *
 * <p>参数格式与 erpReagentChainJob 一致：[full:][N天:]<试剂分类关键词>
 * 每晚建议填 3650:试剂（回溯全历史≈10 年），把老系统改的任何旧单每晚重新拉齐；data_hash 只重推改动行。
 *
 * @author yudao
 */
@Component
@Slf4j
public class ErpReagentChainNightlyJob implements JobHandler {

    /** 默认试剂分类关键词（参数留空时透传，避免试剂同步因空参报错） */
    private static final String DEFAULT_KEYWORDS = "试剂,标准品,血清,基质,切片,耗材,药品,危化,化学品,PBMC,生物,样本";

    /** 回溯天数前缀：如 "30天:" / "30:" */
    private static final Pattern LOOKBACK_PATTERN = Pattern.compile("^(\\d+)\\s*天?\\s*:");

    @Resource
    private ErpSyncService erpSyncService;
    @Resource
    private ErpPushService erpPushService;
    @Resource
    private ReagentBaseFlatSyncService reagentBaseFlatSyncService;

    @Override
    public String execute(String param) throws Exception {
        // 解析参数：[full:][N天:]<关键词>
        String p = StrUtil.nullToEmpty(param).trim();
        boolean full = StrUtil.startWithIgnoreCase(p, "full:");
        if (full) {
            p = StrUtil.removePrefixIgnoreCase(p, "full:").trim();
        }
        int lookback = 1;
        Matcher m = LOOKBACK_PATTERN.matcher(p);
        if (m.find()) {
            lookback = Math.max(1, Integer.parseInt(m.group(1)));
            p = p.substring(m.end()).trim();
        }
        String keywords = StrUtil.isNotBlank(p) ? p : DEFAULT_KEYWORDS;
        String reagentParam = full ? "full:" + keywords : keywords;
        log.info("[erpReagentChainNightlyJob] 触发，full={}，回溯 {} 天，关键词={}", full, lookback, keywords);
        // 1) 老库 → staging（回溯 N 天；变更检测：data_hash 变化 → 置 pushed=0）
        String step1 = erpSyncService.sync(String.valueOf(lookback));
        // 2) staging → master（推 pushed=0 的行）
        String step2 = erpPushService.push(1000);
        // 3) master → reagent_base_flat（增量水位；full: 前缀=全量+停用缺失）
        String step3 = reagentBaseFlatSyncService.sync(reagentParam);
        log.info("[erpReagentChainNightlyJob] 完成");
        return "链式同步完成（full=" + full + ", 回溯 " + lookback + " 天）：\n1) " + step1 + "\n2) " + step2 + "\n3) " + step3;
    }

}
