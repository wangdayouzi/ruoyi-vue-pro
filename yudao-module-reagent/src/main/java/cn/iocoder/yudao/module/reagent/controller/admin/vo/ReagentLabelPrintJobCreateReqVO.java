package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 创建试剂标签打印任务")
@Data
public class ReagentLabelPrintJobCreateReqVO {
    @NotNull(message = "打印机不能为空")
    private Long printerId;
    @NotBlank(message = "标签模板不能为空")
    private String templateCode;
    @NotNull(message = "标签内容不能为空")
    @Valid
    private ReagentLabelPrintReqVO label;
    @NotNull(message = "打印份数不能为空")
    @Min(value = 1, message = "打印份数至少为 1")
    @Max(value = 50, message = "单次打印份数不能超过 50")
    private Integer copies = 1;
}
