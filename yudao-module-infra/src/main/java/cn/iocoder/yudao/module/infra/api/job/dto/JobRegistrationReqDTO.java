package cn.iocoder.yudao.module.infra.api.job.dto;

import jakarta.validation.constraints.*;

public record JobRegistrationReqDTO(
        @NotBlank String name, @NotBlank String handlerName, @NotBlank String cronExpression,
        @PositiveOrZero int retryCount, @PositiveOrZero int retryInterval) {}
