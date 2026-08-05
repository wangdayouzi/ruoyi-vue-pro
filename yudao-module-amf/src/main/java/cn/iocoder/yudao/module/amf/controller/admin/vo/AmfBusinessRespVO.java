package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分析方法文件 - 响应 Response VO
 */
@Schema(description = "管理后台 - 分析方法文件 Response VO")
@Data
public class AmfBusinessRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "方法编号", example = "M-2024-001")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String creator;

}
