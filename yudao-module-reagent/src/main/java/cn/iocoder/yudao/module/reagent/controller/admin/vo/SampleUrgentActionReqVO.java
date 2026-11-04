package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SampleUrgentActionReqVO {
    @NotNull private Long id;
    @NotNull private Integer revision;
    @NotBlank @Pattern(regexp="CLAIM|PROGRESS|CANCEL|COMMENT") private String action;
    private Integer status;
    @Size(max=32) private String exceptionType;
    @Size(max=4000) private String exceptionNote;
    @Size(max=4000) private String completedScope;
    @Size(max=4000) private String remainingScope;
    private LocalDateTime completedTime;
    @Size(max=4000) private String content;
}
