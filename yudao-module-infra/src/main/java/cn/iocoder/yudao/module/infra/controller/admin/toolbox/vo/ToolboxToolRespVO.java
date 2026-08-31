package cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IT 工具箱工具 Response VO")
@Data
public class ToolboxToolRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "工具名称", example = "扫描助手")
    private String name;

    @Schema(description = "工具分类", example = "网络工具")
    private String category;

    @Schema(description = "图标（Element Plus 图标名或图标 URL）", example = "ep:box")
    private String icon;

    @Schema(description = "工具说明", example = "批量扫描局域网 IP 端口")
    private String description;

    @Schema(description = "版本号", example = "v1.0.0")
    private String version;

    @Schema(description = "下载地址（exe 文件 URL）", example = "https://xxx.com/a.exe")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", example = "10485760")
    private Long fileSize;

    @Schema(description = "支持平台", example = "Windows")
    private String platform;

    @Schema(description = "排序", example = "10")
    private Integer sort;

    @Schema(description = "状态（0 开启 1 关闭）", example = "0")
    private Integer status;

    @Schema(description = "下载次数", example = "100")
    private Integer downloadCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
