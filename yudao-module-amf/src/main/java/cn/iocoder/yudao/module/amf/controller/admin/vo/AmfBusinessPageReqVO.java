package cn.iocoder.yudao.module.amf.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 分析方法文件 - 分页查询 Request VO
 */
@Schema(description = "管理后台 - 分析方法文件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmfBusinessPageReqVO extends PageParam {

    @Schema(description = "方法编号，模糊匹配", example = "M-2024")
    private String methodNo;

    @Schema(description = "版本号，模糊匹配", example = "V1")
    private String methodVersion;

    @Schema(description = "方法名称，模糊匹配", example = "HPLC")
    private String methodName;

    @Schema(description = "测试物，模糊匹配", example = "阿莫西林")
    private String testArticle;

    @Schema(description = "基质类型，模糊匹配", example = "血浆")
    private String matrixType;

    @Schema(description = "SD，模糊匹配", example = "张三")
    private String sd;

    @Schema(description = "签字生效日期", example = "2024-01-01")
    private LocalDate effectiveDate;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
