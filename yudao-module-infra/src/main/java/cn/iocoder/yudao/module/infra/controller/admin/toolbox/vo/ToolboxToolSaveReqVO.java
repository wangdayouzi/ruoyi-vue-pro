package cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IT 工具箱工具新增/修改 Request VO")
@Data
public class ToolboxToolSaveReqVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "工具名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "扫描助手")
    @NotEmpty(message = "工具名称不能为空")
    private String name;

    @Schema(description = "工具分类（字典：toolbox_tool_category）", example = "网络工具")
    private String category;

    @Schema(description = "图标（Element Plus 图标名或图标 URL）", example = "ep:box")
    private String icon;

    @Schema(description = "工具说明", example = "批量扫描局域网 IP 端口")
    private String description;

    @Schema(description = "版本号", example = "v1.0.0")
    private String version;

    @Schema(description = "下载地址（exe 文件 URL）", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.com/a.exe")
    @NotEmpty(message = "下载地址不能为空")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", example = "10485760")
    private Long fileSize;

    @Schema(description = "支持平台", example = "Windows")
    private String platform;

    @Schema(description = "排序", example = "10")
    private Integer sort;

    @Schema(description = "状态（0 开启 1 关闭）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

}
