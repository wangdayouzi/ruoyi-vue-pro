package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 申请单分页 Request VO
 */
@Schema(description = "管理后台 - 申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentApplyPageReqVO extends PageParam {

    @Schema(description = "申请单号，模糊匹配")
    private String applyNo;

    @Schema(description = "接收方单位，模糊匹配")
    private String receiverUnit;

    @Schema(description = "物流单号，精确匹配")
    private String trackingNumber;

    @Schema(description = "试剂明细名称，模糊匹配")
    private String reagentName;

    @Schema(description = "状态：0草稿, 1待发货, 2部分发货, 3已完成, 4已拒单退回")
    private Integer status;

}
