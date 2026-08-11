package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新发货物流信息 Request VO（快递单号 / 物流公司）
 */
@Schema(description = "管理后台 - 更新发货物流信息 Request VO")
@Data
public class ReagentShipmentUpdateLogisticsReqVO {

    @Schema(description = "发货单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发货单ID不能为空")
    private Long id;

    @Schema(description = "快递单号", example = "SF1234567890")
    private String trackingNumber;

    @Schema(description = "物流公司", example = "顺丰速运")
    private String expressCompany;

}
