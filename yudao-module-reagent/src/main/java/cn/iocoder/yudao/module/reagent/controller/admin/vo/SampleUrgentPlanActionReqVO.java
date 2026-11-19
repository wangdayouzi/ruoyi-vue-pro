package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SampleUrgentPlanActionReqVO {
    @NotNull @Positive private Long id;
    @NotNull @Min(0) private Integer revision;
    @NotNull @Pattern(regexp = "PAUSE|RESUME|END") private String action;
}
