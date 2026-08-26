package cn.iocoder.yudao.module.mes.service.wm.erp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemTypeDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.unitmeasure.MesMdUnitMeasureDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.vendor.MesMdVendorDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound.MesPmInboundDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound.MesPmInboundLineDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.po.MesPmPoDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseAreaDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.warehouse.MesWmWarehouseLocationDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdItemTypeMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.unitmeasure.MesMdUnitMeasureMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.md.vendor.MesMdVendorMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.batch.MesWmBatchMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pm.inbound.MesPmInboundMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pm.po.MesPmPoMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseAreaMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseLocationMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.warehouse.MesWmWarehouseMapper;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmTransactionTypeEnum;
import cn.iocoder.yudao.module.mes.service.wm.transaction.MesWmTransactionService;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import cn.iocoder.yudao.module.system.sync.erp.dal.mysql.StgPmMapper;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpInboundDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpItemCategoryDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineQtyDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpRequisitionDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnInDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnOutDTO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 老 ERP → MES 推送服务实现（阶段2）
 *
 * 流程：读 staging 未推送行 → upsert 字典(物料/单位/供应商/仓库+默认库区库位) → upsert 批次
 *       → 调 {@link MesWmTransactionService#createTransaction} 写流水+库存 → 标记已推送。
 * 幂等：按源唯一键 selectByCode 判存；事务流水无唯一约束，靠 pushed 标记防重推。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ErpPushServiceImpl implements ErpPushService {

    /** ERP 专用 bizType（MES 自身用 100-124，这里用 125+ 避免混淆） */
    private static final int BIZ_INBOUND = 125;
    private static final int BIZ_REQUISITION = 126;
    private static final int BIZ_RETURN_IN = 127;
    private static final int BIZ_RETURN_OUT = 128;

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 日期/时间解析格式（兼容老ERP 多种写法，防过期/入库日期静默丢失） */
    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
    };
    private static final DateTimeFormatter[] DATETIME_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
    };

    @Resource
    private StgPmMapper stgPmMapper; // system 模块，读 staging（mes→system 依赖成立）

    @Resource
    private MesMdItemMapper mesMdItemMapper;
    @Resource
    private MesMdItemTypeMapper mesMdItemTypeMapper;
    @Resource
    private MesMdUnitMeasureMapper mesMdUnitMeasureMapper;
    @Resource
    private MesMdVendorMapper mesMdVendorMapper;
    @Resource
    private MesWmWarehouseMapper mesWmWarehouseMapper;
    @Resource
    private MesWmWarehouseLocationMapper mesWmWarehouseLocationMapper;
    @Resource
    private MesWmWarehouseAreaMapper mesWmWarehouseAreaMapper;
    @Resource
    private MesWmBatchMapper mesWmBatchMapper;
    @Resource
    private MesWmTransactionService mesWmTransactionService;
    @Resource
    private MesPmInboundMapper mesPmInboundMapper;
    @Resource
    private MesPmPoMapper mesPmPoMapper;

    // 单次推送内的字典缓存（手动触发，不并发，实例字段足够）
    private final Map<String, Long> itemIdCache = new HashMap<>();
    private final Map<String, Long> unitIdCache = new HashMap<>();
    private final Map<String, Long> vendorIdCache = new HashMap<>();
    private final Map<String, WhLoc> whCache = new HashMap<>();

    @Override
    public String push(int limit) {
        Long pushBatch = System.currentTimeMillis();
        itemIdCache.clear();
        unitIdCache.clear();
        vendorIdCache.clear();
        whCache.clear();
        stgPmMapper.insertPushLog(pushBatch);
        log.info("[erp-push][{}] 开始，limit={}", pushBatch, limit);
        try {
            // 已精简：阶段2 只做 采购入库单 + 采购订单 落地（staging → 正式表）
            // 原 MES 库存/流水推送、领料/退料/退货 已停用（方法保留，需要时再启用）
            int inboundMaster = landInbound(pushBatch);
            int poLines = landPo(pushBatch);
            String result = "{\"inboundMaster\":" + inboundMaster + ",\"poLines\":" + poLines + "}";
            stgPmMapper.updatePushLogSuccess(pushBatch, result);
            log.info("[erp-push][{}] 完成 {}", pushBatch, result);
            return "采购入库单主表 " + inboundMaster + " 张，采购订单明细 " + poLines + " 行";
        } catch (Exception e) {
            log.error("[erp-push][{}] 失败", pushBatch, e);
            stgPmMapper.updatePushLogFail(pushBatch, e.getMessage());
            return "失败: " + e.getMessage();
        }
    }

    /**
     * 采购订单数据落地：staging stg_pm_po_line → 正式表 mes_pm_po（幂等，按 line_id upsert）
     */
    private int landPo(Long pushBatch) {
        List<ErpPoLineDTO> rows = stgPmMapper.selectPoLineAll();
        // 窗口快照：先清空正式表再写入，保持与 staging 一致（只留当前窗口）
        mesPmPoMapper.deleteAll();
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        // 领用/退料/采购退货：从 staging 按订单行(pm02712)聚合（已审核数据在 staging 侧已过滤）
        Map<String, BigDecimal> reqMap = toPoLineQtyMap(stgPmMapper.selectRequisitionQtyByPoLine());
        Map<String, BigDecimal> retInMap = toPoLineQtyMap(stgPmMapper.selectReturnInQtyByPoLine());
        Map<String, BigDecimal> retOutMap = toPoLineQtyMap(stgPmMapper.selectReturnOutQtyByPoLine());
        List<MesPmPoDO> list = new ArrayList<>(rows.size());
        for (ErpPoLineDTO row : rows) {
            if (StrUtil.isBlank(row.getLineId())) {
                continue;
            }
            BigDecimal qtyReceived = nvl(row.getQtyReceived());
            BigDecimal qtyRequisition = nvl(reqMap.get(normKey(row.getLineId())));
            BigDecimal qtyReturn = nvl(retInMap.get(normKey(row.getLineId())));
            BigDecimal qtyReturnOut = nvl(retOutMap.get(normKey(row.getLineId())));
            // 剩余 = 已入库 − 领用 + 退料 − 采购退货
            BigDecimal remaining = qtyReceived.subtract(qtyRequisition).add(qtyReturn).subtract(qtyReturnOut);
            list.add(MesPmPoDO.builder()
                    .poId(row.getPoId()).poCode(row.getPoCode()).orderDate(row.getOrderDate()).vendorName(row.getVendorName())
                    .lineId(row.getLineId())
                    .srcItemId(row.getSrcItemId()).itemCode(row.getItemCode()).itemName(row.getItemName())
                    .brand(row.getBrand()).spec(row.getSpec())
                    .unitCode(row.getUnitCode()).unitName(row.getUnitName()).itemCategory(row.getItemCategory())
                    .qtyOrdered(row.getQtyOrdered()).qtyReceived(qtyReceived)
                    .qtyRequisition(qtyRequisition).qtyReturn(qtyReturn).qtyReturnOut(qtyReturnOut)
                    .remainingQty(remaining)
                    .syncBatch(pushBatch).syncTime(LocalDateTime.now())
                    .build());
        }
        List<List<MesPmPoDO>> batches = CollUtil.split(list, 500);
        for (List<MesPmPoDO> batch : batches) {
            mesPmPoMapper.batchUpsert(batch, pushBatch);
        }
        log.info("[erp-push][{}] 采购订单落地：{} 行", pushBatch, list.size());
        return list.size();
    }

    /** 聚合结果 → poLineId(大写归1) → qty 映射 */
    private Map<String, BigDecimal> toPoLineQtyMap(List<ErpPoLineQtyDTO> list) {
        Map<String, BigDecimal> map = new HashMap<>();
        if (CollUtil.isNotEmpty(list)) {
            for (ErpPoLineQtyDTO it : list) {
                if (StrUtil.isNotBlank(it.getPoLineId())) {
                    map.merge(normKey(it.getPoLineId()), nvl(it.getQty()), BigDecimal::add);
                }
            }
        }
        return map;
    }

    private static String normKey(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * 采购入库单落地：staging stg_pm_inbound（扁平行）→ 正式主子表 mes_pm_inbound / mes_pm_inbound_line
     * 幂等：按 src_receipt_id / src_line_id ON CONFLICT 更新
     */
    private int landInbound(Long pushBatch) {
        List<ErpInboundDTO> rows = stgPmMapper.selectInboundAll();
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        // 1) 主表：按 src_receipt_id 去重
        Map<String, MesPmInboundDO> masterMap = new LinkedHashMap<>();
        for (ErpInboundDTO row : rows) {
            if (StrUtil.isBlank(row.getSrcReceiptId())) {
                continue;
            }
            masterMap.computeIfAbsent(row.getSrcReceiptId(), k -> MesPmInboundDO.builder()
                    .srcReceiptId(row.getSrcReceiptId()).basId(row.getBasId())
                    .receiptDate(row.getReceiptDate()).period(row.getPeriod())
                    .docType(row.getDocType())
                    .vendorId(row.getVendorId()).vendorName(row.getVendorName())
                    .warehouseId(row.getWarehouseId()).warehouseName(row.getWarehouseName())
                    .poId(row.getPoId()).poCode(row.getPoCode())
                    .projectId(row.getProjectId()).projectName(row.getProjectName()).projectCode(row.getProjectCode())
                    .applicant(row.getApplicant()).applicantName(row.getApplicantName())
                    .auditor(row.getAuditor()).auditorName(row.getAuditorName())
                    .syncBatch(pushBatch).syncTime(LocalDateTime.now())
                    .build());
        }
        List<MesPmInboundDO> masters = new ArrayList<>(masterMap.values());
        List<List<MesPmInboundDO>> masterBatches = CollUtil.split(masters, 500);
        for (List<MesPmInboundDO> batch : masterBatches) {
            mesPmInboundMapper.batchUpsertMaster(batch, pushBatch);
        }
        // 2) 查主表 id（按 src_receipt_id）
        Map<String, Long> masterIdMap = new HashMap<>();
        for (List<MesPmInboundDO> batch : masterBatches) {
            List<String> keys = batch.stream().map(MesPmInboundDO::getSrcReceiptId).collect(Collectors.toList());
            List<MesPmInboundDO> upserted = mesPmInboundMapper.selectList(
                    Wrappers.<MesPmInboundDO>lambdaQuery().in(MesPmInboundDO::getSrcReceiptId, keys));
            for (MesPmInboundDO m : upserted) {
                masterIdMap.put(m.getSrcReceiptId(), m.getId());
            }
        }
        // 3) 明细
        List<MesPmInboundLineDO> lines = new ArrayList<>();
        for (ErpInboundDTO row : rows) {
            if (StrUtil.isBlank(row.getSrcReceiptId()) || StrUtil.isBlank(row.getSrcLineId())) {
                continue;
            }
            Long inboundId = masterIdMap.get(row.getSrcReceiptId());
            if (inboundId == null) {
                continue;
            }
            lines.add(MesPmInboundLineDO.builder()
                    .inboundId(inboundId).srcReceiptId(row.getSrcReceiptId()).srcLineId(row.getSrcLineId())
                    .srcPoLineId(row.getPoLineId())
                    .lineNo(row.getLineNo())
                    .srcItemId(row.getSrcItemId()).itemCode(row.getItemCode()).itemName(row.getItemName())
                    .brand(row.getBrand()).spec(row.getSpec()).unitName(row.getUnitName()).itemCategory(row.getItemCategory())
                    .batchNo(row.getBatchNo()).expireDate(row.getExpireDate()).storageLocation(row.getStorageLocation())
                    .qty(row.getQty()).priceTaxIn(row.getPriceTaxIn()).amountTaxIn(row.getAmountTaxIn())
                    .priceExTax(row.getPriceExTax()).amountExTax(row.getAmountExTax())
                    .syncBatch(pushBatch).syncTime(LocalDateTime.now())
                    .build());
        }
        List<List<MesPmInboundLineDO>> lineBatches = CollUtil.split(lines, 500);
        for (List<MesPmInboundLineDO> batch : lineBatches) {
            mesPmInboundMapper.batchUpsertLine(batch, pushBatch);
        }
        log.info("[erp-push][{}] 采购入库单落地：主表 {} 张，明细 {} 行", pushBatch, masters.size(), lines.size());
        return masters.size();
    }

    private int pushInbound(Long pushBatch, int limit) {
        List<ErpInboundDTO> rows = stgPmMapper.selectInboundForPush(normLimit(limit));
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        List<Long> pushedIds = new ArrayList<>();
        for (ErpInboundDTO row : rows) {
            try {
                if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("[erp-push] 入库行数量为空或<=0，跳过 {}", row.getSrcLineId());
                    continue;
                }
                Long itemId = resolveItem(row.getItemCode(), row.getItemName(), row.getSpec(),
                        row.getCatalogNo(), row.getBrand(), row.getUnitName(), row.getItemCategory());
                if (itemId == null) {
                    log.warn("[erp-push] 入库行跳过 {}: 物料编码为空，无法建批次/库存", row.getSrcLineId());
                    continue;
                }
                Long vendorId = resolveVendor(row.getVendorId(), row.getVendorName());
                WhLoc wh = resolveWarehouse(row.getWarehouseId(), row.getWarehouseName());
                if (wh == null) {
                    log.warn("[erp-push] 入库行跳过 {}: 仓库为空", row.getSrcLineId());
                    continue;
                }
                MesWmBatchDO batch = resolveBatch(row.getSrcReceiptId(), row.getLineNo(), row.getSrcLineId(),
                        itemId, vendorId, row.getReceiptDate(), row.getExpireDate(), row.getBatchNo(),
                        row.getBasId(), row.getStorageLocation());
                mesWmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                        .setType(MesWmTransactionTypeEnum.IN.getType())
                        .setItemId(itemId).setQuantity(row.getQty()).setBatchId(batch.getId())
                        .setWarehouseId(wh.whId).setLocationId(wh.locId).setAreaId(wh.areaId)
                        .setVendorId(vendorId).setReceiptTime(parseDate(row.getReceiptDate()))
                        .setErpTime(parseDate(row.getReceiptDate()))
                        .setBizType(BIZ_INBOUND).setBizId(row.getStagingId()).setBizCode(row.getBasId())
                        .setBizLineId(row.getStagingId()));
                pushedIds.add(row.getStagingId());
                ok++;
            } catch (Exception e) {
                log.warn("[erp-push] 入库行跳过 {}: {}", row.getSrcLineId(), e.getMessage());
            }
        }
        if (!pushedIds.isEmpty()) {
            stgPmMapper.markInboundPushed(pushedIds, pushBatch);
        }
        return ok;
    }

    private int pushRequisition(Long pushBatch, int limit) {
        List<ErpRequisitionDTO> rows = stgPmMapper.selectRequisitionForPush(normLimit(limit));
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        List<Long> pushedIds = new ArrayList<>();
        for (ErpRequisitionDTO row : rows) {
            try {
                if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("[erp-push] 领料行数量为空或<=0，跳过 {}", row.getSrcLineId());
                    continue;
                }
                MesWmBatchDO batch = findBatchBySrcLineNo(row.getSrcLineReceiptId());
                if (batch == null) {
                    log.warn("[erp-push] 领料行跳过 {}: 来源批次未推送 {}", row.getSrcLineId(), row.getSrcLineReceiptId());
                    continue;
                }
                Long vendorId = resolveVendor(row.getVendorId(), row.getVendorName());
                WhLoc wh = resolveWarehouse(row.getWarehouseId(), row.getWarehouseName());
                if (wh == null) {
                    log.warn("[erp-push] 领料行跳过 {}: 仓库为空", row.getSrcLineId());
                    continue;
                }
                mesWmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                        .setType(MesWmTransactionTypeEnum.OUT.getType())
                        .setItemId(batch.getItemId()).setQuantity(row.getQty().negate()).setBatchId(batch.getId())
                        .setWarehouseId(wh.whId).setLocationId(wh.locId).setAreaId(wh.areaId)
                        .setVendorId(vendorId)
                        .setErpTime(parseDate(row.getReqDate()))
                        // 历史领料可能超发(调拨/替代/其它出库未同步导致批次剩余不足)，关闭强校验避免永久卡住；MES 剩余与老ERP口径一致
                        .setCheckFlag(false)
                        .setBizType(BIZ_REQUISITION).setBizId(row.getStagingId()).setBizCode(row.getReqCode())
                        .setBizLineId(row.getStagingId()));
                pushedIds.add(row.getStagingId());
                ok++;
            } catch (Exception e) {
                log.warn("[erp-push] 领料行跳过 {}: {}", row.getSrcLineId(), e.getMessage());
            }
        }
        if (!pushedIds.isEmpty()) {
            stgPmMapper.markRequisitionPushed(pushedIds, pushBatch);
        }
        return ok;
    }

    private int pushReturnIn(Long pushBatch, int limit) {
        List<ErpReturnInDTO> rows = stgPmMapper.selectReturnInForPush(normLimit(limit));
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        List<Long> pushedIds = new ArrayList<>();
        for (ErpReturnInDTO row : rows) {
            try {
                if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("[erp-push] 退料行数量为空或<=0，跳过 {}", row.getSrcLineId());
                    continue;
                }
                MesWmBatchDO batch = findBatchBySrcLineNo(row.getSrcLineReceiptId());
                if (batch == null) {
                    log.warn("[erp-push] 退料行跳过 {}: 来源批次未推送 {}", row.getSrcLineId(), row.getSrcLineReceiptId());
                    continue;
                }
                Long vendorId = resolveVendor(row.getVendorId(), row.getVendorName());
                WhLoc wh = resolveWarehouse(row.getWarehouseId(), row.getWarehouseName());
                if (wh == null) {
                    log.warn("[erp-push] 退料行跳过 {}: 仓库为空", row.getSrcLineId());
                    continue;
                }
                mesWmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                        .setType(MesWmTransactionTypeEnum.IN.getType())
                        .setItemId(batch.getItemId()).setQuantity(row.getQty()).setBatchId(batch.getId())
                        .setWarehouseId(wh.whId).setLocationId(wh.locId).setAreaId(wh.areaId)
                        .setVendorId(vendorId)
                        .setErpTime(parseDate(row.getRetDate()))
                        .setBizType(BIZ_RETURN_IN).setBizId(row.getStagingId()).setBizCode(row.getRetCode())
                        .setBizLineId(row.getStagingId()));
                pushedIds.add(row.getStagingId());
                ok++;
            } catch (Exception e) {
                log.warn("[erp-push] 退料行跳过 {}: {}", row.getSrcLineId(), e.getMessage());
            }
        }
        if (!pushedIds.isEmpty()) {
            stgPmMapper.markReturnInPushed(pushedIds, pushBatch);
        }
        return ok;
    }

    private int pushReturnOut(Long pushBatch, int limit) {
        List<ErpReturnOutDTO> rows = stgPmMapper.selectReturnOutForPush(normLimit(limit));
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        List<Long> pushedIds = new ArrayList<>();
        for (ErpReturnOutDTO row : rows) {
            try {
                if (row.getQty() == null || row.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("[erp-push] 退货行数量为空或<=0，跳过 {}", row.getSrcLineId());
                    continue;
                }
                MesWmBatchDO batch = findBatchBySrcLineNo(row.getSrcLineReceiptId());
                if (batch == null) {
                    log.warn("[erp-push] 退货行跳过 {}: 来源批次未推送 {}", row.getSrcLineId(), row.getSrcLineReceiptId());
                    continue;
                }
                Long vendorId = resolveVendor(row.getVendorId(), row.getVendorName());
                WhLoc wh = resolveWarehouse(row.getWarehouseId(), row.getWarehouseName());
                if (wh == null) {
                    log.warn("[erp-push] 退货行跳过 {}: 仓库为空", row.getSrcLineId());
                    continue;
                }
                mesWmTransactionService.createTransaction(new MesWmTransactionSaveReqDTO()
                        .setType(MesWmTransactionTypeEnum.OUT.getType())
                        .setItemId(batch.getItemId()).setQuantity(row.getQty().negate()).setBatchId(batch.getId())
                        .setWarehouseId(wh.whId).setLocationId(wh.locId).setAreaId(wh.areaId)
                        .setVendorId(vendorId)
                        .setErpTime(parseDate(row.getRoDate()))
                        // 同上：历史退货可能超出发放剩余，关闭强校验
                        .setCheckFlag(false)
                        .setBizType(BIZ_RETURN_OUT).setBizId(row.getStagingId()).setBizCode(row.getRoCode())
                        .setBizLineId(row.getStagingId()));
                pushedIds.add(row.getStagingId());
                ok++;
            } catch (Exception e) {
                log.warn("[erp-push] 退货行跳过 {}: {}", row.getSrcLineId(), e.getMessage());
            }
        }
        if (!pushedIds.isEmpty()) {
            stgPmMapper.markReturnOutPushed(pushedIds, pushBatch);
        }
        return ok;
    }

    // ==================== 字典 ====================

    /**
     * 物料分类全量推送（sdpm001 → mes_md_item_type），两趟：
     * 先建/更新所有分类（parentId 暂置 0），再按父分类键 pm00103 补 parent_id，保证层级。
     */
    private int pushItemCategory(Long pushBatch) {
        List<ErpItemCategoryDTO> rows = stgPmMapper.selectItemCategoryForPush(100000);
        if (CollUtil.isEmpty(rows)) {
            return 0;
        }
        int ok = 0;
        List<Long> pushedIds = new ArrayList<>();
        // 第一趟：建/更新分类本身
        for (ErpItemCategoryDTO row : rows) {
            try {
                if (StrUtil.isBlank(row.getCatKey())) {
                    continue;
                }
                MesMdItemTypeDO exist = mesMdItemTypeMapper.selectByCode(row.getCatKey());
                if (exist == null) {
                    MesMdItemTypeDO type = MesMdItemTypeDO.builder()
                            .code(row.getCatKey())
                            .name(StrUtil.nullToDefault(row.getCatName(), row.getCatKey()))
                            .parentId(MesMdItemTypeDO.PARENT_ID_ROOT)
                            .itemOrProduct("ITEM")
                            .sort(0).status(0)
                            .build();
                    mesMdItemTypeMapper.insert(type);
                } else if (StrUtil.isNotBlank(row.getCatName()) && !row.getCatName().equals(exist.getName())) {
                    MesMdItemTypeDO patch = new MesMdItemTypeDO();
                    patch.setId(exist.getId());
                    patch.setName(row.getCatName());
                    mesMdItemTypeMapper.updateById(patch);
                }
                pushedIds.add(row.getStagingId());
                ok++;
            } catch (Exception e) {
                log.warn("[erp-push] 分类跳过 {}: {}", row.getCatKey(), e.getMessage());
            }
        }
        // 第二趟：按父分类键补 parent_id（父键 → 父分类 id）
        for (ErpItemCategoryDTO row : rows) {
            try {
                if (StrUtil.isBlank(row.getParentKey())) {
                    continue;
                }
                MesMdItemTypeDO child = mesMdItemTypeMapper.selectByCode(row.getCatKey());
                MesMdItemTypeDO parent = mesMdItemTypeMapper.selectByCode(row.getParentKey());
                if (child == null || parent == null) {
                    continue;
                }
                if (!Objects.equals(child.getParentId(), parent.getId())) {
                    MesMdItemTypeDO patch = new MesMdItemTypeDO();
                    patch.setId(child.getId());
                    patch.setParentId(parent.getId());
                    mesMdItemTypeMapper.updateById(patch);
                }
            } catch (Exception e) {
                log.warn("[erp-push] 分类挂父跳过 {}: {}", row.getCatKey(), e.getMessage());
            }
        }
        if (!pushedIds.isEmpty()) {
            stgPmMapper.markItemCategoryPushed(pushedIds, pushBatch);
        }
        if (ok > 0) {
            log.info("[erp-push] 分类推送 {} 条（含层级修正）", ok);
        }
        return ok;
    }

    /** 物料分类回填：给 MES 已有但缺分类的物料补 itemTypeId（不重推流水，安全） */
    private void backfillItemCategory() {
        List<ErpInboundDTO> cats = stgPmMapper.selectItemCategoryDistinct();
        if (CollUtil.isEmpty(cats)) {
            return;
        }
        int updated = 0;
        for (ErpInboundDTO c : cats) {
            if (StrUtil.isBlank(c.getItemCode())) {
                continue;
            }
            MesMdItemDO item = mesMdItemMapper.selectByCode(c.getItemCode());
            if (item == null || item.getItemTypeId() != null) {
                continue;
            }
            Long typeId = resolveItemType(c.getCatalogNo(), c.getItemCategory());
            if (typeId == null) {
                continue;
            }
            MesMdItemDO patch = new MesMdItemDO();
            patch.setId(item.getId());
            patch.setItemTypeId(typeId);
            mesMdItemMapper.updateById(patch);
            updated++;
        }
        if (updated > 0) {
            log.info("[erp-push] 物料分类回填 {} 条", updated);
        }
    }

    private Long resolveItem(String code, String name, String spec, String catalog, String brand, String unitName, String itemCategory) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        Long cached = itemIdCache.get(code);
        if (cached != null) {
            return cached;
        }
        MesMdItemDO exist = mesMdItemMapper.selectByCode(code);
        if (exist == null) {
            MesMdItemDO item = MesMdItemDO.builder().code(code)
                    .name(StrUtil.nullToDefault(name, code)).specification(spec)
                    .unitMeasureId(resolveUnit(unitName))
                    .itemTypeId(resolveItemType(catalog, itemCategory))
                    .batchFlag(true).status(0)
                    .brand(brand)
                    .build();
            mesMdItemMapper.insert(item);
            itemIdCache.put(code, item.getId());
            return item.getId();
        }
        // 已有物料：分类为空则回填（老数据补分类，只更新该列）
        if (exist.getItemTypeId() == null) {
            Long typeId = resolveItemType(catalog, itemCategory);
            if (typeId != null) {
                MesMdItemDO patch = new MesMdItemDO();
                patch.setId(exist.getId());
                patch.setItemTypeId(typeId);
                mesMdItemMapper.updateById(patch);
            }
        }
        itemIdCache.put(code, exist.getId());
        return exist.getId();
    }

    /** 物料分类：老ERP pm00203(分类键) → sdpm001.pm00102(分类名) → mes_md_item_type */
    private Long resolveItemType(String categoryKey, String categoryName) {
        String key = StrUtil.blankToDefault(categoryKey, categoryName);
        if (StrUtil.isBlank(key) || StrUtil.isBlank(categoryName)) {
            return null;
        }
        MesMdItemTypeDO exist = mesMdItemTypeMapper.selectByCode(key);
        if (exist != null) {
            return exist.getId();
        }
        MesMdItemTypeDO type = MesMdItemTypeDO.builder()
                .code(key).name(categoryName)
                .parentId(MesMdItemTypeDO.PARENT_ID_ROOT)
                .itemOrProduct("ITEM")
                .sort(0).status(0)
                .build();
        mesMdItemTypeMapper.insert(type);
        return type.getId();
    }

    private Long resolveUnit(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        Long cached = unitIdCache.get(name);
        if (cached != null) {
            return cached;
        }
        MesMdUnitMeasureDO exist = mesMdUnitMeasureMapper.selectByCode(name);
        if (exist == null) {
            MesMdUnitMeasureDO unit = MesMdUnitMeasureDO.builder().code(name).name(name)
                    .primaryFlag(true).status(0).build();
            mesMdUnitMeasureMapper.insert(unit);
            unitIdCache.put(name, unit.getId());
            return unit.getId();
        }
        unitIdCache.put(name, exist.getId());
        return exist.getId();
    }

    private Long resolveVendor(String vendorId, String vendorName) {
        String key = StrUtil.isBlank(vendorId) ? StrUtil.blankToDefault(vendorName, "") : vendorId;
        if (key.isEmpty()) {
            return null;
        }
        Long cached = vendorIdCache.get(key);
        if (cached != null) {
            return cached;
        }
        MesMdVendorDO exist = mesMdVendorMapper.selectByCode(key);
        if (exist == null) {
            MesMdVendorDO vendor = MesMdVendorDO.builder().code(key)
                    .name(StrUtil.nullToDefault(vendorName, key)).status(0).build();
            mesMdVendorMapper.insert(vendor);
            vendorIdCache.put(key, vendor.getId());
            return vendor.getId();
        }
        vendorIdCache.put(key, exist.getId());
        return exist.getId();
    }

    /** 仓库 + 默认库区 + 默认库位（B 方案） */
    private WhLoc resolveWarehouse(String whId, String whName) {
        String key = StrUtil.isBlank(whId) ? StrUtil.blankToDefault(whName, "") : whId;
        if (key.isEmpty()) {
            return null;
        }
        WhLoc cached = whCache.get(key);
        if (cached != null) {
            return cached;
        }
        String name = StrUtil.nullToDefault(whName, key);
        MesWmWarehouseDO wh = mesWmWarehouseMapper.selectByCode(key);
        if (wh == null) {
            // frozen 列为 int2，不走 Boolean，留空走库默认 0（未冻结）
            wh = MesWmWarehouseDO.builder().code(key).name(name).build();
            mesWmWarehouseMapper.insert(wh);
        }
        // 默认库区
        String locCode = key + "_LOC";
        MesWmWarehouseLocationDO loc = mesWmWarehouseLocationMapper.selectByCode(locCode);
        if (loc == null) {
            // frozen 列为 int2，不走 Boolean
            loc = MesWmWarehouseLocationDO.builder().code(locCode).name(name + "-默认库区")
                    .warehouseId(wh.getId()).build();
            mesWmWarehouseLocationMapper.insert(loc);
        }
        // 默认库位
        String areaCode = key + "_AREA";
        MesWmWarehouseAreaDO area = mesWmWarehouseAreaMapper.selectByCode(loc.getId(), areaCode);
        if (area == null) {
            // frozen/allow_item_mixing/allow_batch_mixing 列为 int2，不走 Boolean，留空走库默认(0/1/1)
            area = MesWmWarehouseAreaDO.builder().code(areaCode).name(name + "-默认库位")
                    .locationId(loc.getId()).status(0).build();
            mesWmWarehouseAreaMapper.insert(area);
        }
        WhLoc result = new WhLoc(wh.getId(), loc.getId(), area.getId());
        whCache.put(key, result);
        return result;
    }

    // ==================== 批次 ====================

    private MesWmBatchDO resolveBatch(String receiptId, Integer lineNo, String srcLineId, Long itemId,
                                      Long vendorId, String receiptDate, String expireDate,
                                      String batchNo, String basId, String storageLocation) {
        String code = receiptId + "_" + (lineNo != null ? lineNo : srcLineId);
        MesWmBatchDO batch = mesWmBatchMapper.selectByCode(code);
        if (batch == null) {
            batch = MesWmBatchDO.builder().code(code).itemId(itemId)
                    .vendorId(vendorId)
                    .receiptDate(parseDate(receiptDate))
                    .expireDate(parseDate(expireDate))
                    .lotNumber(batchNo)
                    .srcLineNo(srcLineId)
                    .basId(basId)
                    .storageLocation(storageLocation)
                    .remark(joinRemark("BAS", basId))
                    .build();
            mesWmBatchMapper.insert(batch);
        }
        return batch;
    }

    private MesWmBatchDO findBatchBySrcLineNo(String srcLineNo) {
        if (StrUtil.isBlank(srcLineNo)) {
            return null;
        }
        return mesWmBatchMapper.selectOne(Wrappers.<MesWmBatchDO>lambdaQuery()
                .eq(MesWmBatchDO::getSrcLineNo, srcLineNo).last("LIMIT 1"));
    }

    // ==================== 工具 ====================

    private int normLimit(int limit) {
        return limit > 0 ? limit : 1000;
    }

    private LocalDateTime parseDate(String s) {
        if (StrUtil.isBlank(s)) {
            return null;
        }
        String v = s.trim();
        // 先按带时间格式，再按纯日期格式，最后兜底截前10位，全失败则置空并告警
        for (DateTimeFormatter fmt : DATETIME_FORMATS) {
            try {
                return LocalDateTime.parse(v, fmt);
            } catch (Exception ignore) {
                // 继续尝试下一种格式
            }
        }
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(v, fmt).atStartOfDay();
            } catch (Exception ignore) {
                // 继续尝试下一种格式
            }
        }
        try {
            return LocalDate.parse(v.substring(0, Math.min(10, v.length())), YMD).atStartOfDay();
        } catch (Exception e) {
            log.warn("[erp-push] 无法解析日期，置空: {}", s);
            return null;
        }
    }

    private String joinRemark(String... kv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            if (StrUtil.isNotBlank(kv[i + 1])) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(kv[i]).append(":").append(kv[i + 1]);
            }
        }
        return sb.toString();
    }

    private static class WhLoc {
        final Long whId;
        final Long locId;
        final Long areaId;

        WhLoc(Long whId, Long locId, Long areaId) {
            this.whId = whId;
            this.locId = locId;
            this.areaId = areaId;
        }
    }

}
