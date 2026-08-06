package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发货单 Response VO
 */
@Schema(description = "管理后台 - 发货单 Response VO")
@Data
public class ReagentShipmentRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "发货单号", example = "SHIP-20240001")
    private String shipmentNo;

    @Schema(description = "关联申请单ID", example = "1")
    private Long applyId;

    @Schema(description = "快递单号", example = "SF1234567890")
    private String trackingNumber;

    @Schema(description = "物流公司", example = "顺丰速运")
    private String expressCompany;

    @Schema(description = "运费结算方式", example = "月结")
    private String freightSettlement;

    @Schema(description = "项目号")
    private String projectNo;

    @Schema(description = "运输温度", example = "2-8°C")
    private String transportTemp;

    @Schema(description = "是否放置温度记录仪：0否, 1是", example = "1")
    private Integer hasTempLogger;

    @Schema(description = "发货时间")
    private LocalDateTime shipmentDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "发货明细列表")
    private List<ReagentShipmentItemVO> items;

}
