package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 老 ERP 入库明细行 DTO（源：sdpm026 主 + sdpm027 明细，冗余名称）
 *
 * @author yudao
 */
@Data
public class ErpInboundDTO {

    /** staging 主键（推送时作 bizId） */
    private Long stagingId;
    /** 入库主内部ID pm02601 */
    private String srcReceiptId;
    /** 明细行号 pm02702（唯一） */
    private String srcLineId;
    /** 采购订单明细行ID pm02712（→sdpm014.pm01402，行级关联用） */
    private String poLineId;
    /** 入库单号/BAS pm02603 */
    private String basId;
    /** 单据日期 pm02602 */
    private String receiptDate;
    /** 年月 pm02614 */
    private String period;
    /** 单据类型 pm02623（1=采购入库 6=其他） */
    private Integer docType;
    /** 批号 pm02631 */
    private String batchNo;
    /** 过期日期 pm02725（清洗 NA/空） */
    private String expireDate;
    /** 存放位置 pm02726 */
    private String storageLocation;
    /** 行序 pm02728 */
    private Integer lineNo;
    /** 物料内部ID pm02703 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 货号 pm00203 */
    private String catalogNo;
    /** 品牌 pm00221 */
    private String brand;
    /** 规格 pm00205 */
    private String spec;
    /** 单位 pa01302 */
    private String unitName;
    /** 物料分类 pm00203→sdpm001.pm00102 */
    private String itemCategory;
    /** 数量 pm02706×pm02705 */
    private BigDecimal qty;
    /** 含税单价 pm02707 */
    private BigDecimal priceTaxIn;
    /** 含税金额 pm02708 */
    private BigDecimal amountTaxIn;
    /** 不含税单价 pm02709 */
    private BigDecimal priceExTax;
    /** 不含税金额 pm02710 */
    private BigDecimal amountExTax;
    /** 供应商 pm02604 */
    private String vendorId;
    /** 供应商名称 pf00302 */
    private String vendorName;
    /** 仓库 pm02605 */
    private String warehouseId;
    /** 仓库名称 pm00402 */
    private String warehouseName;
    /** 采购订单 pm02612 */
    private String poId;
    /** 采购订单号 pm01303 */
    private String poCode;
    /** 项目 pm02723 */
    private String projectId;
    /** 项目名称 pa00102 */
    private String projectName;
    /** 项目编号 pa00140 */
    private String projectCode;
    /** 申请人 pm02609 */
    private String applicant;
    /** 申请人名称 pj00402 */
    private String applicantName;
    /** 审核人 pm02616 */
    private String auditor;
    /** 审核人名称 pj00402 */
    private String auditorName;

}
