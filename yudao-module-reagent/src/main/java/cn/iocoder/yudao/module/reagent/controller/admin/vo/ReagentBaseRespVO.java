package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试剂 Response VO
 */
@Schema(description = "管理后台 - 试剂 Response VO")
@Data
public class ReagentBaseRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "生物试剂编号", example = "BAS-001")
    private String basId;

    @Schema(description = "试剂名称", example = "Anti-CD3 Antibody")
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
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
