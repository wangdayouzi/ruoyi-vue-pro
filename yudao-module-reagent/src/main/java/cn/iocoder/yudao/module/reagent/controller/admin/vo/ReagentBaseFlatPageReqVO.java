package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 老ERP同步 试剂基础数据(扁平) 分页 Request VO
 */
@Schema(description = "管理后台 - 试剂基础数据(扁平) 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentBaseFlatPageReqVO extends PageParam {

    @Schema(description = "采购入库单号，模糊匹配")
    private String basId;

    @Schema(description = "试剂编号(材料编号)，模糊匹配")
    private String reagentCode;

    @Schema(description = "试剂名称，模糊匹配")
    private String reagentName;

    @Schema(description = "货号，模糊匹配")
    private String catNo;

    @Schema(description = "分类名，模糊匹配")
    private String itemCategory;

    @Schema(description = "状态：0正常, 1停用")
    private Integer status;

}
