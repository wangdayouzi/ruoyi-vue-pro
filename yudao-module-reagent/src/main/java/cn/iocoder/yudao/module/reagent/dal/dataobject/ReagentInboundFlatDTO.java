package cn.iocoder.yudao.module.reagent.dal.dataobject;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 正式库入库单明细（mes_pm_inbound_line join mes_pm_inbound）—— 试剂基础数据同步源
 *
 * @author yudao
 */
@Data
public class ReagentInboundFlatDTO {

    /** 入库明细行ID pm02702（幂等键） */
    private String srcLineId;
    /** 入库主ID pm02601 */
    private String srcReceiptId;
    /** 采购入库单号 pm02603（主表） */
    private String basId;
    /** 供应商名称（主表） */
    private String vendorName;
    /** 仓库名称（主表 pm00402） */
    private String warehouseName;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 规格 pm00205 */
    private String spec;
    /** 批号 pm02631 */
    private String batchNo;
    /** 过期日期 pm02725 */
    private String expireDate;
    /** 接收数量 */
    private BigDecimal qty;
    /** 关联采购订单-剩余数量（mes_pm_po.remaining_qty，与入库单明细页同源） */
    private BigDecimal amountLeft;
    /** 存储位置 pm02726 */
    private String storageLocation;
    /** 分类名 pm00102 */
    private String itemCategory;
    /** 分类键 pm00203 */
    private String categoryKey;

}
