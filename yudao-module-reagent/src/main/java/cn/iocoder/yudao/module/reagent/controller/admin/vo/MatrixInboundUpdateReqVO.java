package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 修改基质预约，保留地区、类型和原 BAS 号")
public record MatrixInboundUpdateReqVO(
        @NotNull @Positive Long id, @NotNull @PositiveOrZero Integer version,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime appointmentDate,
        Boolean limsDesigned, boolean urgent,
        @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> appointmentUserIds) {}
