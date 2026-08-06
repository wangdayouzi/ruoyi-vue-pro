package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 试剂创建/修改 Request VO
 */
@Schema(description = "管理后台 - 试剂创建/修改 Request VO")
@Data
public class ReagentBaseSaveReqVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "生物试剂编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "BAS-001")
    @NotBlank(message = "试剂编号不能为空")
    private String basId;

    @Schema(description = "试剂名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Anti-CD3 Antibody")
    @NotBlank(message = "试剂名称不能为空")
    private String reagentName;

    @Schema(description = "供应商", example = "Sigma-Aldrich")
    private String vendor;

    @Schema(description = "货号", example = "SAB5600832")
    private String catNo;

    @Schema(description = "储存温度", example = "2-8°C")
    private String storageTemp;

    @Schema(description = "储存位置", example = "A区-3号冰箱")
    private String storageLocation;

    @Schema(description = "状态：0正常, 1停用", example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

}
