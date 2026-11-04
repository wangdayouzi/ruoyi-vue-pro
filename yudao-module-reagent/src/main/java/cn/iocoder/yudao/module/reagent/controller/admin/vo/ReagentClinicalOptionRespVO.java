package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 临床方案下拉选项 Response VO
 */
@Schema(description = "管理后台 - 临床方案下拉选项 Response VO")
@Data
public class ReagentClinicalOptionRespVO {

    @Schema(description = "临床方案ID")
    private Long id;

    @Schema(description = "临床方案名称")
    private String name;

    @Schema(description = "临床方案编号")
    private String number;

}
