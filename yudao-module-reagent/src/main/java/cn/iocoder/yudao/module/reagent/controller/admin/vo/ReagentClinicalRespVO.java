package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 临床方案 Response VO
 */
@Schema(description = "管理后台 - 临床方案 Response VO")
@Data
public class ReagentClinicalRespVO {

    @Schema(description = "临床方案ID")
    private Long id;

    @Schema(description = "临床方案名称")
    private String name;

    @Schema(description = "临床方案编号")
    private String number;

    @Schema(description = "申办方ID")
    private Long sponsorId;

    @Schema(description = "是否上传附件：0否 1是")
    private Integer isUpload;

    @Schema(description = "生效日期")
    private String effectiveDate;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "是否自动发送寄送通知邮件：0否 1是")
    private Integer isAutoReceive;

    @Schema(description = "源表添加时间")
    private LocalDateTime srcCreateTime;

    @Schema(description = "源表编辑时间")
    private LocalDateTime srcUpdateTime;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

}
