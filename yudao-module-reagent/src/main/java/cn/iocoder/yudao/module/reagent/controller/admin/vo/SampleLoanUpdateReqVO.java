package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 领用记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleLoanUpdateReqVO extends SampleLoanCreateReqVO {

    @Schema(description = "领用记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "领用记录编号不能为空")
    private Long id;
}
