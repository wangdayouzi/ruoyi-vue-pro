package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发货单分页 Request VO
 */
@Schema(description = "管理后台 - 发货单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentShipmentPageReqVO extends PageParam {

    @Schema(description = "关联申请单ID")
    private Long applyId;

    @Schema(description = "发货单号，模糊匹配")
    private String shipmentNo;

}
