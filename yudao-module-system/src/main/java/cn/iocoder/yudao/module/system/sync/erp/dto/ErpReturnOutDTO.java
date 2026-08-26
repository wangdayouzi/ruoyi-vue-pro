package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 老 ERP 采购退货明细行 DTO（源：sdpm022 主 + sdpm023 明细，pm02217=1，方向=出）
 *
 * @author yudao
 */
@Data
public class ErpReturnOutDTO {

    /** staging 主键（推送时作 bizId） */
    private Long stagingId;
    /** 退货主内部ID pm02201 */
    private String srcRoId;
    /** 明细行号 pm02302（唯一） */
    private String srcLineId;
    /** 退货单号 pm02203 */
    private String roCode;
    /** 单据日期 pm02202 */
    private String roDate;
    /** 年月 pm02214 */
    private String period;
    /** 物料内部ID pm02303 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 数量 pm02304（出库-） */
    private BigDecimal qty;
    /** 单价 pm02305 */
    private BigDecimal price;
    /** 供应商 pm02204 */
    private String vendorId;
    /** 供应商名称 pf00302 */
    private String vendorName;
    /** 仓库 pm02205 */
    private String warehouseId;
    /** 仓库名称 pm00402 */
    private String warehouseName;
    /** 采购订单 pm02212 */
    private String poId;
    /** 采购订单号 pm01303 */
    private String poCode;
    /** 申请人 pm02209 */
    private String applicant;
    /** 申请人名称 pj00402 */
    private String applicantName;
    /** 审核人 pm02213 */
    private String auditor;
    /** 审核人名称 pj00402 */
    private String auditorName;
    /** 来源入库明细行 pm02311 → pm02702（定位批次） */
    private String srcLineReceiptId;

}
