package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目号 / BAS 下拉选项 Response VO
 */
@Schema(description = "管理后台 - 项目号/BAS下拉选项 Response VO")
@Data
public class ReagentItemOptionRespVO {

    @Schema(description = "项目ID（源表 think_items.id）")
    private Long id;

    @Schema(description = "项目号（源表 baseid）")
    private String baseId;

    @Schema(description = "临床方案ID")
    private Long clinicalId;

    @Schema(description = "临床方案名称")
    private String clinicalName;

    @Schema(description = "临床方案编号")
    private String clinicalNumber;

    @Schema(description = "样本类型ID")
    private Long typeId;

}
