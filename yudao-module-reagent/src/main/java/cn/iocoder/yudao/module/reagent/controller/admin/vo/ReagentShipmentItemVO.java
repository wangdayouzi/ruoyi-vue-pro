package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发货单明细 VO（嵌入式）
 */
@Schema(description = "管理后台 - 发货单明细 VO")
@Data
public class ReagentShipmentItemVO {

    @Schema(description = "关联申请明细ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申请明细ID不能为空")
    private Long applyItemId;

    @Schema(description = "批号", example = "LOT20240001")
    private String lotNo;

    @Schema(description = "本次实际发货数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "发货数量不能为空")
    private Integer quantityShipped;

}
