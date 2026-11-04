package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SampleUrgentSaveReqVO {
    private Long id;
    private Integer revision;
    @NotBlank @Size(max=200) private String projectNo;
    /** 临床方案ID（下拉选择，可为空以兼容历史数据） */
    private Long clinicalId;
    @NotNull private Boolean newProject;
    @Size(max=2000) private String basNos;
    @NotBlank @Size(max=2000) private String sampleScope;
    @NotNull private LocalDateTime deadline;
    private LocalDateTime expectedArrival;
    private LocalDateTime plannedTestTime;
    @Size(max=300) private String contacts;
    @Size(max=2000) private String reason;
    @NotEmpty @Size(max=20) private java.util.List<@NotNull @Positive Long> appointmentUserIds;
    @Size(max=100) private java.util.List<@NotNull @Positive Long> noticeUserIds;
    @Size(max=100) private java.util.List<@NotNull @Positive Long> noticeDeptIds;
    @Size(max=1000) private String logisticsNo;
    @Size(max=4000) private String remark;
    @Size(max=1000) private String changeReason;
}
