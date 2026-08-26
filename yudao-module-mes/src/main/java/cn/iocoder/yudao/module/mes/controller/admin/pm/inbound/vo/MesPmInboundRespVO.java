package cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 老ERP采购入库单明细 响应 VO（扁平行 = 明细 + 主表头部）")
@Data
public class MesPmInboundRespVO {

    @Schema(description = "明细行 id")
    private Long id;
    @Schema(description = "主表 id")
    private Long inboundId;
    @Schema(description = "入库单ID pm02601")
    private String srcReceiptId;
    @Schema(description = "单号/BAS pm02603")
    private String basId;
    @Schema(description = "接受日期 pm02602")
    private String receiptDate;
    @Schema(description = "期间 pm02614")
    private String period;
    @Schema(description = "单据类型 pm02623")
    private Integer docType;
    @Schema(description = "供应商名称")
    private String vendorName;
    @Schema(description = "仓库名称")
    private String warehouseName;
    @Schema(description = "采购订单号 pm01303")
    private String poCode;
    @Schema(description = "关联采购订单-已入库数量 pm01411")
    private BigDecimal qtyReceived;
    @Schema(description = "关联采购订单-领用数量")
    private BigDecimal qtyRequisition;
    @Schema(description = "关联采购订单-剩余数量")
    private BigDecimal remainingQty;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "申请人")
    private String applicantName;
    @Schema(description = "审核人")
    private String auditorName;

    @Schema(description = "行号 pm02728")
    private Integer lineNo;
    @Schema(description = "明细行ID pm02702")
    private String srcLineId;
    @Schema(description = "采购订单明细行ID pm02712（行级关联 mes_pm_po.line_id）")
    private String srcPoLineId;
    @Schema(description = "物料编码 pm00201")
    private String itemCode;
    @Schema(description = "物料名称 pm00202")
    private String itemName;
    @Schema(description = "品牌 pm00221")
    private String brand;
    @Schema(description = "规格 pm00205")
    private String spec;
    @Schema(description = "单位 pa01302")
    private String unitName;
    @Schema(description = "物料分类 pm00203→pm00102")
    private String itemCategory;
    @Schema(description = "批号 pm02631")
    private String batchNo;
    @Schema(description = "过期日期 pm02725")
    private String expireDate;
    @Schema(description = "存储位置 pm02726")
    private String storageLocation;
    @Schema(description = "接收数量 pm02706×pm02705")
    private BigDecimal qty;
    @Schema(description = "含税单价 pm02707")
    private BigDecimal priceTaxIn;
    @Schema(description = "含税金额 pm02708")
    private BigDecimal amountTaxIn;
    @Schema(description = "未税单价 pm02709")
    private BigDecimal priceExTax;
    @Schema(description = "未税金额 pm02710")
    private BigDecimal amountExTax;
    @Schema(description = "同步时间")
    private LocalDateTime syncTime;
}
