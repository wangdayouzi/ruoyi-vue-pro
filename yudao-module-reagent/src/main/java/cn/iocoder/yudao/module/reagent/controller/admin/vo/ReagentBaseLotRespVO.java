package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试剂批号 Response VO
 */
@Schema(description = "管理后台 - 试剂批号 Response VO")
@Data
public class ReagentBaseLotRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "关联试剂主键", example = "1")
    private Long baseId;

    @Schema(description = "批号", example = "LOT20240001")
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
