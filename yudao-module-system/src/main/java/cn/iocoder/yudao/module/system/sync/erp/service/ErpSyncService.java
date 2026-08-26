package cn.iocoder.yudao.module.system.sync.erp.service;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.system.sync.erp.dal.mysql.ErpSourceMapper;
import cn.iocoder.yudao.module.system.sync.erp.dal.mysql.StgPmMapper;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpInboundDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpItemCategoryDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpRequisitionDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnInDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnOutDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 老 ERP → staging 中间库 同步服务
 *
 * 策略：按日期窗口（默认当天，可回溯 N 天）从 SQL Server 一次性拉取，分批 upsert 到中间库。
 * 只做新增+更新，不做删除；源库全表 NOLOCK。性能优先：窗口一次查完 + 500 一批入库。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ErpSyncService {

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int BATCH_SIZE = 500;

    @Resource
    private ErpSourceMapper erpSourceMapper;
    @Resource
    private StgPmMapper stgPmMapper;

    /**
     * 执行同步（支持三种窗口参数，手动触发用）
     * <p>
     * 参数格式（填在定时任务 handler_param）：
     * <ul>
     *   <li>N —— 回溯 N 天（默认 1，只拉今天）</li>
     *   <li>N:offset —— 分段回填：每段 N 天，从今天往前推 offset 天为终点。如 60:0=最近60天，60:60=往前第2段</li>
     *   <li>begin~end —— 显式日期范围，如 2023-04-01~2023-08-31（老库脆时按段补历史最稳）</li>
     * </ul>
     *
     * @param param 窗口参数
     * @return 结果摘要
     */
    public String sync(String param) {
        Window w = parseWindow(param);
        Long syncBatch = System.currentTimeMillis();
        stgPmMapper.insertSyncLog(syncBatch, w.dayCount);
        log.info("[erp-sync][{}] 开始，窗口 {}-{}（{}）", syncBatch, w.beginYm, w.endYm, w.desc);

        try {
            // 同步：采购入库单 + 分类 + 采购订单明细 + 领料/退料/采购退货（staging 扁平表，供已领用聚合）
            // 阶段2 MES 库存推送 仍停用（不写 mes_wm_* 流水/库存表）
            int inboundCount = syncInbound(syncBatch, w.beginYm, w.endYm);
            int categoryCount = syncItemCategory(syncBatch);
            int poLineCount = syncPoLine(syncBatch, w.beginYm, w.endYm);
            int reqCount = syncRequisition(syncBatch, w.beginYm, w.endYm);
            int retInCount = syncReturnIn(syncBatch, w.beginYm, w.endYm);
            int retOutCount = syncReturnOut(syncBatch, w.beginYm, w.endYm);

            String result = "{\"inbound\":" + inboundCount
                    + ",\"category\":" + categoryCount + ",\"poLine\":" + poLineCount
                    + ",\"requisition\":" + reqCount + ",\"returnIn\":" + retInCount + ",\"returnOut\":" + retOutCount + "}";
            stgPmMapper.updateSyncLogSuccess(syncBatch, result);
            log.info("[erp-sync][{}] 完成，inbound={}, category={}, poLine={}, requisition={}, returnIn={}, returnOut={}",
                    syncBatch, inboundCount, categoryCount, poLineCount, reqCount, retInCount, retOutCount);
            return "采购入库单 " + inboundCount + " 行，分类 " + categoryCount + " 条，采购订单明细 " + poLineCount
                    + " 行，领料 " + reqCount + " 行，退料 " + retInCount + " 行，采购退货 " + retOutCount + " 行";
        } catch (Exception e) {
            log.error("[erp-sync][{}] 失败", syncBatch, e);
            stgPmMapper.updateSyncLogFail(syncBatch, e.getMessage());
            return "失败: " + e.getMessage();
        }
    }

    /** 解析同步窗口：支持 范围 / 分段 / 回溯天数 */
    private Window parseWindow(String param) {
        String p = param == null ? "" : param.trim();
        // 1) 显式日期范围 begin~end（如 2023-04-01~2023-08-31）
        if (p.contains("~")) {
            String[] a = p.split("~", 2);
            String b = a[0].trim();
            String e = a[1].trim();
            if (isDate(b) && isDate(e) && b.compareTo(e) <= 0) {
                int dayCount = (int) ChronoUnit.DAYS.between(LocalDate.parse(b, YMD), LocalDate.parse(e, YMD)) + 1;
                return new Window(b, e, dayCount, "范围 " + b + "~" + e);
            }
        }
        // 2) 分段 N:offset（每段 N 天，从今天往前推 offset 天为终点）
        if (p.contains(":")) {
            String[] a = p.split(":", 2);
            try {
                int days = Integer.parseInt(a[0].trim());
                int offset = Integer.parseInt(a[1].trim());
                int off = Math.max(offset, 0);
                String end = LocalDate.now().minusDays(off).format(YMD);
                String begin = LocalDate.now().minusDays(off + days - 1L).format(YMD);
                return new Window(begin, end, Math.max(days, 1), "分段 " + days + " 天 偏移 " + offset);
            } catch (NumberFormatException ignore) {
                // 不是数字，落到回溯天数兜底
            }
        }
        // 3) 回溯天数（默认 1）
        int days = 1;
        try {
            days = Integer.parseInt(p);
        } catch (NumberFormatException ignore) {
            // 非法参数，用默认 1
        }
        days = Math.max(days, 1);
        String end = LocalDate.now().format(YMD);
        String begin = LocalDate.now().minusDays(days - 1L).format(YMD);
        return new Window(begin, end, days, "回溯 " + days + " 天");
    }

    private boolean isDate(String s) {
        try {
            LocalDate.parse(s, YMD);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 同步窗口 */
    private static class Window {
        final String beginYm;
        final String endYm;
        final int dayCount;
        final String desc;

        Window(String beginYm, String endYm, int dayCount, String desc) {
            this.beginYm = beginYm;
            this.endYm = endYm;
            this.dayCount = dayCount;
            this.desc = desc;
        }
    }

    private int syncInbound(Long syncBatch, String beginYm, String endYm) {
        List<ErpInboundDTO> list = erpSourceMapper.selectInbound(beginYm, endYm);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        list.forEach(this::cleanInbound);
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, list.size());
            stgPmMapper.batchUpsertInbound(list.subList(i, end), syncBatch);
        }
        return list.size();
    }

    private int syncRequisition(Long syncBatch, String beginYm, String endYm) {
        List<ErpRequisitionDTO> list = erpSourceMapper.selectRequisition(beginYm, endYm);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, list.size());
            stgPmMapper.batchUpsertRequisition(list.subList(i, end), syncBatch);
        }
        return list.size();
    }

    private int syncReturnIn(Long syncBatch, String beginYm, String endYm) {
        List<ErpReturnInDTO> list = erpSourceMapper.selectReturnIn(beginYm, endYm);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, list.size());
            stgPmMapper.batchUpsertReturnIn(list.subList(i, end), syncBatch);
        }
        return list.size();
    }

    private int syncReturnOut(Long syncBatch, String beginYm, String endYm) {
        List<ErpReturnOutDTO> list = erpSourceMapper.selectReturnOut(beginYm, endYm);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, list.size());
            stgPmMapper.batchUpsertReturnOut(list.subList(i, end), syncBatch);
        }
        return list.size();
    }

    /** 物料分类全量同步（sdpm001 行数很少，整表一次 upsert；无日期窗口） */
    private int syncItemCategory(Long syncBatch) {
        List<ErpItemCategoryDTO> list = erpSourceMapper.selectItemCategory();
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        stgPmMapper.batchUpsertItemCategory(list, syncBatch);
        return list.size();
    }

    /** 采购订单明细按订单日期窗口同步（累积 upsert，不清空 —— 去掉窗口快照，历史回填的行保留） */
    private int syncPoLine(Long syncBatch, String beginYm, String endYm) {
        List<ErpPoLineDTO> list = erpSourceMapper.selectPoLine(beginYm, endYm);
        // 累积：不清空（按 line_id upsert），只新增/更新本次窗口的订单行，避免分段回填/增量覆盖丢失
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, list.size());
            stgPmMapper.batchUpsertPoLine(list.subList(i, end), syncBatch);
        }
        return list.size();
    }

    /** 老系统过期日期可能是 'NA'/空 → 置 null */
    private void cleanInbound(ErpInboundDTO dto) {
        String expire = dto.getExpireDate();
        if (expire != null && !expire.isBlank()) {
            String trimmed = expire.trim();
            dto.setExpireDate("NA".equalsIgnoreCase(trimmed) ? null : trimmed);
        }
    }

}
