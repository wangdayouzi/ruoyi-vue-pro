package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class SampleUrgentPlanSaveReqVO {
    @NotNull @Valid private SampleUrgentSaveReqVO template;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private LocalTime dailyTime;
    /** ISO 星期：周一 1，周日 7。 */
    @NotEmpty @Size(max = 7) private List<@NotNull @Min(1) @Max(7) Integer> weekdays;
}
