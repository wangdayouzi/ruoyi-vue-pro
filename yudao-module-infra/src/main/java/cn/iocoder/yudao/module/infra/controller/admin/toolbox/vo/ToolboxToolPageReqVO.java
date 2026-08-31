package cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - IT 工具箱工具分页 Request VO")
@Data
public class ToolboxToolPageReqVO extends PageParam {

    @Schema(description = "工具名称，模糊匹配", example = "扫描助手")
    private String name;

    @Schema(description = "工具分类", example = "网络工具")
    private String category;

    @Schema(description = "状态（0 开启 1 关闭）", example = "0")
    private Integer status;

}
