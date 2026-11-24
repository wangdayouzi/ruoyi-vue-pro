package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "管理后台 - 新建基质入库预约")
public record MatrixInboundCreateReqVO(
        @NotNull UUID requestId,
        @NotBlank @Pattern(regexp = "NINGBO|SHANGHAI") String site,
        @NotBlank @Pattern(regexp = "B|P") String matrixKind,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime appointmentDate,
        Boolean limsDesigned, boolean urgent,
        @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> appointmentUserIds) {}
