package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文件 Response VO")
@Data
public class AmfFileRespVO {

    @Schema(description = "主键编号")
    private Long id;

    @Schema(description = "关联业务单据ID")
    private Long businessId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "当前文件URL")
    private String fileUrl;

    @Schema(description = "当前版本号")
    private Integer fileVersion;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
