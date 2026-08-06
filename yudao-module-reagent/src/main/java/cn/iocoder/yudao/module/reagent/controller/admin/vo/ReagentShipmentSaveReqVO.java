package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发货单创建 Request VO（样品组操作）
 */
@Schema(description = "管理后台 - 发货单创建 Request VO")
@Data
public class ReagentShipmentSaveReqVO {

    @Schema(description = "关联申请单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申请单ID不能为空")
    private Long applyId;

    @Schema(description = "快递单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "SF1234567890")
    private String trackingNumber;

    @Schema(description = "物流公司", requiredMode = Schema.RequiredMode.REQUIRED, example = "顺丰速运")
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

    // ========== 发货明细 ==========
    @Schema(description = "发货明细列表")
    @NotEmpty(message = "发货明细不能为空")
    @Valid
    private List<ReagentShipmentItemVO> items;

}
