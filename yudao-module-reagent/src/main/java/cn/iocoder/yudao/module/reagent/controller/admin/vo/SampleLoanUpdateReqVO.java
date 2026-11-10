package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 领用记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleLoanUpdateReqVO extends SampleLoanCreateReqVO {

    @Schema(description = "领用记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "领用记录编号不能为空")
    private Long id;
    @PastOrPresent(message = "领用时间不能晚于当前时间")
    private LocalDateTime createTime;
    @PastOrPresent(message = "归还时间不能晚于当前时间")
    private LocalDateTime returnTime;

    @NotBlank(message = "需求人不能为空")
    @Size(max = 64, message = "需求人长度不能超过64个字符")
    private String requester;

    @NotNull(message = "提单人不能为空")
    private Long submitterId;

    @NotBlank(message = "提单人不能为空")
    @Size(max = 64, message = "提单人长度不能超过64个字符")
    private String submitter;
}
