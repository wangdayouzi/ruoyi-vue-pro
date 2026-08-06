package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.reagent.util.PrintUtil;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 试剂打印 Service
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentPrintService {

    private static final String TEMPLATE = "reagent-shipment-print.xlsx";
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private ReagentShipmentMapper reagentShipmentMapper;
    @Resource
    private ReagentShipmentItemMapper reagentShipmentItemMapper;
    @Resource
    private ReagentApplyMapper reagentApplyMapper;
    @Resource
    private ReagentApplyItemMapper reagentApplyItemMapper;
    @Resource
    private ReagentBaseMapper reagentBaseMapper;
    @Resource
    private ReagentBaseLotMapper reagentBaseLotMapper;
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 单个发货单打印
     */
    public void printShipment(Long shipmentId, HttpServletResponse response) {
        ReagentShipmentDO shipment = reagentShipmentMapper.selectById(shipmentId);
        if (shipment == null) return;

        ReagentApplyDO apply = reagentApplyMapper.selectById(shipment.getApplyId());
        List<ReagentShipmentItemDO> shipmentItems = reagentShipmentItemMapper.selectListByShipmentId(shipmentId);
        List<ReagentApplyItemDO> applyItems = reagentApplyItemMapper.selectListByApplyId(shipment.getApplyId());

        Map<String, Object> data = buildDataMap(
                Collections.singletonList(shipment), apply,
                shipmentItems, applyItems);
        String fileName = "交接单-" + shipment.getShipmentNo() + ".xlsx";
        PrintUtil.render(response, TEMPLATE, data, fileName);
    }

    // ==================== 多选合并打印 ====================

    /**
     * 多个发货单合并为一张交接单（同属一个申请单的多个子发货单 → 汇总试剂明细到一张表）
     */
    public void printShipmentsMerged(Set<Long> shipmentIds, HttpServletResponse response) {
        if (shipmentIds == null || shipmentIds.isEmpty()) return;

        // 1. 加载所有发货单 → 校验同属一个申请单
        List<ReagentShipmentDO> shipments = new ArrayList<>();
        Long applyId = null;
        for (Long id : shipmentIds) {
            ReagentShipmentDO s = reagentShipmentMapper.selectById(id);
            if (s == null) continue;
            if (applyId == null) {
                applyId = s.getApplyId();
            } else if (!applyId.equals(s.getApplyId())) {
                throw new RuntimeException("所选发货单不属于同一个申请单，无法合并打印");
            }
            shipments.add(s);
        }
        if (shipments.isEmpty()) return;

        ReagentApplyDO apply = reagentApplyMapper.selectById(applyId);

        // 2. 汇总所有发货单的试剂明细
        List<ReagentShipmentItemDO> allShipmentItems = new ArrayList<>();
        for (ReagentShipmentDO s : shipments) {
            allShipmentItems.addAll(reagentShipmentItemMapper.selectListByShipmentId(s.getId()));
        }
        List<ReagentApplyItemDO> applyItems = reagentApplyItemMapper.selectListByApplyId(applyId);

        // 3. 组装数据
        log.info("[print] 合并打印 shipmentIds={} 实际加载 shipments={}", shipmentIds, shipments.size());
        Map<String, Object> data = buildDataMap(shipments, apply, allShipmentItems, applyItems);
        String fileName = "交接单-合并" + shipments.size() + "单.xlsx";
        PrintUtil.render(response, TEMPLATE, data, fileName);
    }

    // ==================== 数据组装 ====================

    /**
     * @param shipments      发货单列表（单个打印时传单元素列表）
     * @param apply          申请单（收发方信息来源）
     * @param shipmentItems  本次要打印的所有发货明细
     * @param applyItems     申请单原始明细（用于联查试剂名称等）
     */
    private Map<String, Object> buildDataMap(
            List<ReagentShipmentDO> shipments,
            ReagentApplyDO apply,
            List<ReagentShipmentItemDO> shipmentItems,
            List<ReagentApplyItemDO> applyItems) {

        ReagentShipmentDO first = shipments.get(0);
        PrintUtil.DataBuilder builder = PrintUtil.builder();

        // -- 发货信息（发货单号/快递多单时循环展示，由模板 each 处理） --
        builder.put("shipmentDate", PrintUtil.fmtDateTime(first.getShipmentDate()));
        builder.put("projectNo", joinField(shipments, ReagentShipmentDO::getProjectNo, "、"));
        builder.put("transportTemp", joinField(shipments, ReagentShipmentDO::getTransportTemp, "、"));
        builder.put("freightSettlement", joinField(shipments, ReagentShipmentDO::getFreightSettlement, "、"));
        builder.put("hasTempLogger", PrintUtil.fmtYorN(first.getHasTempLogger()));

        // -- 发货单列表（模板物流发货信息区循环多行） --
        List<Map<String, Object>> shipmentRows = new ArrayList<>();
        for (ReagentShipmentDO s : shipments) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("shipmentNo", PrintUtil.fmtBlk(s.getShipmentNo()));
            row.put("expressCompany", PrintUtil.fmtBlk(s.getExpressCompany()));
            row.put("trackingNumber", PrintUtil.fmtBlk(s.getTrackingNumber()));
            row.put("shipmentDate", PrintUtil.fmtDateTime(s.getShipmentDate()));
            shipmentRows.add(row);
        }
        builder.put("shipments", shipmentRows);

        // -- 收发方信息（来自申请单，所有发货单共享） --
        if (apply != null) {
            builder.put("consignorUnit", apply.getConsignorUnit());
            builder.put("consignorAddress", apply.getConsignorAddress());
            builder.put("consignorName", apply.getConsignorName());
            builder.put("consignorPhone", apply.getConsignorPhone());
            builder.put("receiverUnit", apply.getReceiverUnit());
            builder.put("receiverAddress", apply.getReceiverAddress());
            builder.put("receiverName", apply.getReceiverName());
            builder.put("receiverPhone", apply.getReceiverPhone());
        }

        // -- 制单人/日期 --
        builder.put("creator", getCreatorName(first.getCreator()));
        builder.put("creatorTime", first.getCreateTime() != null
                ? first.getCreateTime().format(DATETIME_FMT) : "");

        // -- 试剂明细列表（汇总所有发货单的明细） --
        List<Map<String, Object>> items = buildItemList(shipmentItems, applyItems);
        builder.put("items", items);

        return builder.build();
    }

    /**
     * 将制单人用户 ID 解析为昵称
     */
    private String getCreatorName(String creator) {
        if (creator == null || creator.isBlank()) {
            return "";
        }
        try {
            AdminUserRespDTO user = adminUserApi.getUser(Long.valueOf(creator));
            return user != null ? user.getNickname() : creator;
        } catch (NumberFormatException e) {
            return creator; // 非数字，直接原样返回
        }
    }

    /**
     * 多字段值去重拼接
     */
    private static <T> String joinField(List<T> list, java.util.function.Function<T, String> getter, String sep) {
        return list.stream()
                .map(getter)
                .filter(v -> v != null && !v.isBlank())
                .distinct()
                .reduce((a, b) -> a + sep + b)
                .orElse("");
    }

    /**
     * 将发货明细转为模板渲染用的 Map 列表，同时联查试剂基础数据补全字段
     */
    private List<Map<String, Object>> buildItemList(
            List<ReagentShipmentItemDO> shipmentItems,
            List<ReagentApplyItemDO> applyItems) {

        // 构建 ApplyItemDO 的 ID → DO 映射，方便快速查找
        Map<Long, ReagentApplyItemDO> applyItemMap = new HashMap<>();
        for (ReagentApplyItemDO ai : applyItems) {
            applyItemMap.put(ai.getId(), ai);
        }

        // 合并相同试剂（basId + lotNo 相同则累加数量）
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        for (ReagentShipmentItemDO si : shipmentItems) {
            ReagentApplyItemDO ai = applyItemMap.get(si.getApplyItemId());

            String reagentName = "";
            String vendor = "";
            String basId = "";
            String storageTemp = "";
            if (ai != null && ai.getBasId() != null) {
                basId = ai.getBasId();
                ReagentBaseDO base = reagentBaseMapper.selectByBasId(ai.getBasId());
                if (base != null) {
                    reagentName = base.getReagentName() != null ? base.getReagentName() : "";
                    vendor = base.getVendor() != null ? base.getVendor() : "";
                    storageTemp = base.getStorageTemp() != null ? base.getStorageTemp() : "";
                }
            }

            String content = "";
            String expirationDate = "";
            if (ai != null && ai.getBasId() != null && si.getLotNo() != null) {
                ReagentBaseDO base = reagentBaseMapper.selectByBasId(ai.getBasId());
                if (base != null) {
                    ReagentBaseLotDO lot = reagentBaseLotMapper.selectByBaseIdAndLotNo(base.getId(), si.getLotNo());
                    if (lot != null) {
                        content = lot.getContent() != null ? lot.getContent() : "";
                        expirationDate = PrintUtil.fmtDate(lot.getExpirationDate());
                    }
                }
            }

            String catNo = ai != null ? ai.getCatNo() : "";
            String lotNo = PrintUtil.fmtBlk(si.getLotNo());
            // 合并 key：试剂编号 + 批号
            String mergeKey = basId + "|" + lotNo;

            Map<String, Object> row = merged.get(mergeKey);
            if (row == null) {
                row = new LinkedHashMap<>();
                row.put("reagentName", reagentName);
                row.put("basId", basId);
                row.put("vendor", vendor);
                row.put("content", PrintUtil.fmtBlk(content));
                row.put("catNo", PrintUtil.fmtBlk(catNo));
                row.put("lotNo", lotNo);
                row.put("quantityShipped", 0);
                row.put("storageTemp", storageTemp);
                row.put("expirationDate", PrintUtil.fmtBlk(expirationDate));
                merged.put(mergeKey, row);
            }
            // 累加数量
            int qty = si.getQuantityShipped() != null ? si.getQuantityShipped() : 0;
            row.put("quantityShipped", ((Integer) row.get("quantityShipped")) + qty);
        }
        return new ArrayList<>(merged.values());
    }
}
