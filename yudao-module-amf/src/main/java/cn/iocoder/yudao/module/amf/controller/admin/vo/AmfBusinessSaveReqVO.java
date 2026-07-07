package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分析方法文件 - 创建/修改 Request VO
 */
@Schema(description = "管理后台 - 分析方法文件创建/修改 Request VO")
@Data
public class AmfBusinessSaveReqVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "BAS编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "BAS-2024-001")
    @NotBlank(message = "BAS编号不能为空")
    private String basNo;

    @Schema(description = "临床方案编号", example = "PROTOCOL-2024-001")
    private String protocolNo;

    @Schema(description = "申办方", example = "某某医药公司")
    private String sponsor;

    @Schema(description = "分析方法", example = "HPLC法测定含量")
    private String analysisMethod;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

}
