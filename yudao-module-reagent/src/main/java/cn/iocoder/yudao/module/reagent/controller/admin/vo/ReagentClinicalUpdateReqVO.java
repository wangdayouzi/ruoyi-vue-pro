package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 临床方案 本地字段修改 Request VO
 *
 * <p>仅允许维护本地字段（是否自动发送寄送通知邮件），源字段由同步任务维护。
 */
@Schema(description = "管理后台 - 临床方案 本地字段修改 Request VO")
@Data
public class ReagentClinicalUpdateReqVO {

    @Schema(description = "临床方案ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "临床方案ID不能为空")
    private Long id;

    @Schema(description = "是否自动发送寄送通知邮件：0否 1是", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否自动发送寄送通知邮件不能为空")
    private Integer isAutoReceive;

}
