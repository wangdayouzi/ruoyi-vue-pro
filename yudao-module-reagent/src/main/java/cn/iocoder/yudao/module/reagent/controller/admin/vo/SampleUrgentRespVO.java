package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import lombok.Data;
import java.time.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 管理后台响应，仅暴露业务字段，避免把租户和逻辑删除标记直接返回页面。 */
@Data
public class SampleUrgentRespVO {
    private Long id;
    private String urgentNo;
    private String projectNo;
    private Long clinicalId;
    private String inboundSite;
    private Boolean newProject;
    private String basNos;
    private String sampleScope;
    private LocalDateTime deadline;
    private LocalDateTime expectedArrival;
    private LocalDateTime sampleReceivedTime;
    private LocalDateTime plannedTestTime;
    private String contacts;
    private String legacyContacts;
    private List<Long> appointmentUserIds;
    private List<Long> noticeUserIds;
    private List<Long> noticeDeptIds;
    private String noticeNames;
    private String reason;
    private String logisticsNo;
    private String remark;
    private Long requesterId;
    private String requesterName;
    private Long handlerId;
    private String handlerName;
    private Integer status;
    private String exceptionType;
    private String exceptionNote;
    private String completedScope;
    private String remainingScope;
    private LocalDateTime completedTime;
    private Integer revision;
    private Long recurringPlanId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recurringDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creator;
    private String updater;
}
