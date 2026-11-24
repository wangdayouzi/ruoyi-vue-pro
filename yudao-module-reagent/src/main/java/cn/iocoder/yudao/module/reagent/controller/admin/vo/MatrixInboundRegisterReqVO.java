package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 基质入库登记或状态更正")
public record MatrixInboundRegisterReqVO(
        @NotNull @Positive Long id, @NotNull @PositiveOrZero Integer version,
        @Schema(description = "1 未入库，2 部分入库，3 已入库")
        @NotNull @Min(1) @Max(3) Integer inboundStatus, @Size(max = 2000) String exceptionRemark) {}
