package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import lombok.Data;
import java.time.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 管理后台响应，仅暴露业务字段，避免把租户和逻辑删除标记直接返回页面。 */
@Data
public class SampleUrgentLogRespVO {
    private Long id;
    private Long urgentId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creator;
    private String updater;
}
