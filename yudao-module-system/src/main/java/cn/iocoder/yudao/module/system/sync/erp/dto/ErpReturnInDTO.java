package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 老 ERP 项目退料明细行 DTO（源：sdpm024 主 + sdpm025 明细，方向=入·回库）
 *
 * @author yudao
 */
@Data
public class ErpReturnInDTO {

    /** staging 主键（推送时作 bizId） */
    private Long stagingId;
    /** 退料主内部ID pm02401 */
    private String srcRetId;
    /** 明细行号 pm02502（唯一） */
    private String srcLineId;
    /** 退料单号 pm02403 */
    private String retCode;
    /** 单据日期 pm02402 */
    private String retDate;
    /** 年月 pm02416 */
    private String period;
    /** 物料内部ID pm02503 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 数量 pm02504（回库+） */
    private BigDecimal qty;
    /** 金额 pm02506 */
    private BigDecimal amount;
    /** 项目 pm02404 */
    private String projectId;
    /** 项目名称 pa00102 */
    private String projectName;
    /** 仓库 pm02406 */
    private String warehouseId;
    /** 仓库名称 pm00402 */
    private String warehouseName;
    /** 供应商 pm02408 */
    private String vendorId;
    /** 供应商名称 pf00302 */
    private String vendorName;
    /** 申请人 pm02412 */
    private String applicant;
    /** 申请人名称 pj00402 */
    private String applicantName;
    /** 审核人 pm02417 */
    private String auditor;
    /** 审核人名称 pj00402 */
    private String auditorName;
    /** 来源入库明细行 pm02507 → pm02702（定位批次） */
    private String srcLineReceiptId;

}
