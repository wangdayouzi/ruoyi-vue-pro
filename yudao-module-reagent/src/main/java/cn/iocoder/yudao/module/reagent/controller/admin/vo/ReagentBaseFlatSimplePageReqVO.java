package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 老ERP同步 试剂基础数据(扁平) 简易分页 Request VO（申请单选批号用）
 */
@Schema(description = "管理后台 - 试剂基础数据(扁平) 简易分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentBaseFlatSimplePageReqVO extends PageParam {

    @Schema(description = "关键词：匹配 入库单号/试剂编号/试剂名称/货号")
    private String keyword;

    @Schema(description = "仓库名称，模糊匹配")
    private String warehouse;

}
