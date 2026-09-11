package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 试剂标签打印任务")
@Data
public class ReagentLabelPrintJobRespVO {
    private Long id;
    private String jobNo;
    private Long printerId;
    private String templateCode;
    private Integer copies;
    private Integer printedCount;
    private Integer status;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime completedTime;
}
