package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件版本记录 - 响应 Response VO
 */
@Schema(description = "管理后台 - 文件版本记录 Response VO")
@Data
public class AmfFileVersionRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "关联业务单据ID", example = "1")
    private Long businessId;

    @Schema(description = "版本号", example = "3")
    private String versionNo;

    @Schema(description = "文件名", example = "分析方法v3.docx")
    private String fileName;

    @Schema(description = "文件URL", example = "/files/amf/xxx.docx")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    @Schema(description = "文件类型", example = "docx")
    private String fileType;

    @Schema(description = "变更说明", example = "更新了分析方法步骤二")
    private String changeDescription;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建者")
    private String creator;

}
