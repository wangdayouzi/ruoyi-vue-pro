package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 领用登记 Request VO")
@Data
public class SampleLoanCreateReqVO {

    @Schema(description = "BAS 号", requiredMode = Schema.RequiredMode.REQUIRED, example = "BAS-001")
    @NotBlank(message = "BAS号不能为空")
    @Size(max = 64, message = "BAS号长度不能超过64个字符")
    private String basNo;

    @Schema(description = "领用类型：SAMPLE-样品，REAGENT-试剂", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "SAMPLE")
    @NotBlank(message = "领用类型不能为空")
    @Pattern(regexp = "SAMPLE|REAGENT", message = "领用类型不合法")
    private String materialType;

    @Schema(description = "领用地点", requiredMode = Schema.RequiredMode.REQUIRED, example = "4楼")
    @NotBlank(message = "地点不能为空")
    @Pattern(regexp = "4楼|8楼", message = "地点不合法")
    private String location;

    @Schema(description = "需求人用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "需求人不能为空")
    private Long requesterId;

    @Schema(description = "需求人", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "需求人不能为空")
    @Size(max = 64, message = "需求人长度不能超过64个字符")
    private String requester;

    @Schema(description = "提单人用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "提单人不能为空")
    private Long submitterId;

    @Schema(description = "提单人", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotBlank(message = "提单人不能为空")
    @Size(max = 64, message = "提单人长度不能超过64个字符")
    private String submitter;

    @Schema(description = "物料信息", example = "血清样品")
    @Size(max = 500, message = "物料信息长度不能超过500个字符")
    private String sampleInfo;

    @Schema(description = "备注", example = "使用后请及时归还")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
