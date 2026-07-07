package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分析方法文件 - 响应 Response VO
 */
@Schema(description = "管理后台 - 分析方法文件 Response VO")
@Data
public class AmfBusinessRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "BAS编号", example = "BAS-2024-001")
    private String basNo;

    @Schema(description = "临床方案编号", example = "PROTOCOL-2024-001")
    private String protocolNo;

    @Schema(description = "申办方", example = "某某医药公司")
    private String sponsor;

    @Schema(description = "分析方法", example = "HPLC法测定含量")
    private String analysisMethod;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String creator;

}
