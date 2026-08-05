package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * 分析方法文件 - 创建/修改 Request VO
 */
@Schema(description = "管理后台 - 分析方法文件创建/修改 Request VO")
@Data
public class AmfBusinessSaveReqVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "方法编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "M-2024-001")
    @NotBlank(message = "方法编号不能为空")
    private String methodNo;

    @Schema(description = "版本号", example = "V1.0")
    private String methodVersion;

    @Schema(description = "方法名称", example = "HPLC法测定含量")
    private String methodName;

    @Schema(description = "测试物", example = "阿莫西林")
    private String testArticle;

    @Schema(description = "基质类型", example = "血浆")
    private String matrixType;

    @Schema(description = "SD", example = "张三")
    private String sd;

    @Schema(description = "签字生效日期", example = "2024-01-01")
    private LocalDate effectiveDate;

    @Schema(description = "申办方", example = "某药企")
    private String sponsor;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

}
