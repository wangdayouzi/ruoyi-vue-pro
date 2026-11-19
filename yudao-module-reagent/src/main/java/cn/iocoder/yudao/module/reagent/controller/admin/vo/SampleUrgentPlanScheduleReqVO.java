package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class SampleUrgentPlanScheduleReqVO {
    @NotNull @Positive private Long id;
    @NotNull @Min(0) private Integer revision;
    @NotNull private LocalDate endDate;
    @NotNull private LocalTime dailyTime;
    @NotEmpty @Size(max = 7) private List<@NotNull @Min(1) @Max(7) Integer> weekdays;
}
