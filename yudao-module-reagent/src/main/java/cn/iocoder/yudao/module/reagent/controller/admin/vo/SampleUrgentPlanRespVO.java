package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import lombok.Data;
import java.time.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 管理后台响应，仅暴露业务字段，避免把租户和逻辑删除标记直接返回页面。 */
@Data
public class SampleUrgentPlanRespVO {
    private Long id;
    private SampleUrgentSaveReqVO template;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime dailyTime;
    private List<Integer> weekdays;
    private Long requesterId;
    private String requesterName;
    private Integer status;
    private Integer revision;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate nextDate;
    private String lastError;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creator;
    private String updater;
}
