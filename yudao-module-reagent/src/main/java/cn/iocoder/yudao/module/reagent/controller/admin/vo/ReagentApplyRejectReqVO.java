package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请单拒绝/退回 Request VO
 */
@Schema(description = "管理后台 - 申请单拒绝/退回 Request VO")
@Data
public class ReagentApplyRejectReqVO {

    @Schema(description = "申请单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申请单ID不能为空")
    private Long id;

    @Schema(description = "拒绝/退回理由", requiredMode = Schema.RequiredMode.REQUIRED, example = "批号已过期，无法发货")
    @NotBlank(message = "拒绝理由不能为空")
    private String remark;

}
