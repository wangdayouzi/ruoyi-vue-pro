package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "打印代理 - 回传标签打印结果")
@Data
public class ReagentLabelAgentResultReqVO {
    @NotNull
    private Long jobId;
    @NotBlank
    private String claimToken;
    /** 3-成功，4-失败 */
    @NotNull
    private Integer status;
    private Integer printedCount;
    private String errorCode;
    private String errorMessage;
}
