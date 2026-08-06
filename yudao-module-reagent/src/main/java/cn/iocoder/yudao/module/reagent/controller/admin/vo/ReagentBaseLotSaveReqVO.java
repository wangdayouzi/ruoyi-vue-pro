package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试剂批号创建/修改 Request VO
 */
@Schema(description = "管理后台 - 试剂批号创建/修改 Request VO")
@Data
public class ReagentBaseLotSaveReqVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "关联试剂主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联试剂不能为空")
    private Long baseId;

    @Schema(description = "批号", requiredMode = Schema.RequiredMode.REQUIRED, example = "LOT20240001")
    @NotBlank(message = "批号不能为空")
    private String lotNo;

    @Schema(description = "规格/浓度", example = "50g")
    private String content;

    @Schema(description = "过期日期", example = "2025-12-31")
    private LocalDateTime expirationDate;

    @Schema(description = "参考剩余量", example = "300ml")
    private String amountLeft;

    @Schema(description = "状态：0正常, 1停用", example = "0")
    private Integer status;

}
