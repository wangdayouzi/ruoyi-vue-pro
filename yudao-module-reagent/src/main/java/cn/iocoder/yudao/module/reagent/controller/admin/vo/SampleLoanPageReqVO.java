package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 领用台账分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SampleLoanPageReqVO extends PageParam {

    @Schema(description = "BAS 号", example = "BAS-001")
    private String basNo;
    @Schema(description = "需求人用户 ID", example = "1")
    private Long requesterId;
    @Schema(description = "状态，1-可领用；2-已归还；3-无需归还", example = "1")
    private Integer status;
}
