package cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 老ERP采购订单数据 响应 VO")
@Data
public class MesPmPoRespVO {

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "采购订单主ID pm01402")
    private String poId;
    @Schema(description = "采购订单号 pm01303")
    private String poCode;
    @Schema(description = "订单日期 pm01302")
    private String orderDate;
    @Schema(description = "供应商名称")
    private String vendorName;
    @Schema(description = "明细行ID pm01401")
    private String lineId;
    @Schema(description = "物料ID pm01403")
    private String srcItemId;
    @Schema(description = "物料编码 pm00201")
    private String itemCode;
    @Schema(description = "物料名称 pm00202")
    private String itemName;
    @Schema(description = "品牌 pm00221")
    private String brand;
    @Schema(description = "规格 pm00205")
    private String spec;
    @Schema(description = "单位编码 pm01404")
    private String unitCode;
    @Schema(description = "单位名称 pa01302")
    private String unitName;
    @Schema(description = "物料分类")
    private String itemCategory;
    @Schema(description = "采购数量 pm01406")
    private BigDecimal qtyOrdered;
    @Schema(description = "已入库数量 pm01411")
    private BigDecimal qtyReceived;
    @Schema(description = "领用数量(staging 聚合)")
    private BigDecimal qtyRequisition;
    @Schema(description = "退料数量(staging 聚合)")
    private BigDecimal qtyReturn;
    @Schema(description = "采购退货数量(staging 聚合)")
    private BigDecimal qtyReturnOut;
    @Schema(description = "剩余数量(已入库−领用+退料−采购退货)")
    private BigDecimal remainingQty;
    @Schema(description = "同步时间")
    private LocalDateTime syncTime;
}
