package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 老 ERP 领料明细行 DTO（源：sdpm020 主 + sdpm021 明细，冗余名称）
 *
 * @author yudao
 */
@Data
public class ErpRequisitionDTO {

    /** staging 主键（推送时作 bizId） */
    private Long stagingId;
    /** 领料主内部ID pm02001 */
    private String srcReqId;
    /** 明细行号 pm02102（唯一） */
    private String srcLineId;
    /** 领料单号 pm02003 */
    private String reqCode;
    /** 单据日期 pm02002 */
    private String reqDate;
    /** 年月 pm02016 */
    private String period;
    /** 物料内部ID pm02103 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 数量 pm02104 */
    private BigDecimal qty;
    /** 单价 pm02105 */
    private BigDecimal price;
    /** 含税单价 pm02107 */
    private BigDecimal priceTaxIn;
    /** 项目 pm02004 */
    private String projectId;
    /** 项目名称 pa00102 */
    private String projectName;
    /** 项目编号 pa00140 */
    private String projectCode;
    /** 供应商 pm02008 */
    private String vendorId;
    /** 供应商名称 pf00302 */
    private String vendorName;
    /** 仓库 pm02006 */
    private String warehouseId;
    /** 仓库名称 pm00402 */
    private String warehouseName;
    /** 申请人 pm02012 */
    private String applicant;
    /** 申请人名称 pj00402 */
    private String applicantName;
    /** 审核人 pm02017 */
    private String auditor;
    /** 审核人名称 pj00402 */
    private String auditorName;
    /** 来源入库主 pm02015 */
    private String srcReceiptId;
    /** 来源入库明细行 pm02108 → pm02702（定位批次关键） */
    private String srcLineReceiptId;

}
