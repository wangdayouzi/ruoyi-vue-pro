package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目号 Response VO
 */
@Schema(description = "管理后台 - 项目号 Response VO")
@Data
public class ReagentItemRespVO {

    @Schema(description = "项目ID")
    private Long id;

    @Schema(description = "城市：1上海，2宁波")
    private Integer cityId;

    @Schema(description = "项目号")
    private String baseId;

    @Schema(description = "临床方案ID")
    private Long clinicalId;

    @Schema(description = "临床方案名称")
    private String clinicalName;

    @Schema(description = "临床方案编号")
    private String clinicalNumber;

    @Schema(description = "项目负责人ID")
    private Long personId;

    @Schema(description = "样本类型ID")
    private Long typeId;

    @Schema(description = "是否时效：0否 1是")
    private Integer isAging;

    @Schema(description = "复核状态：0未复核，1已复核")
    private Integer reviewStatus;

    @Schema(description = "复核人员ID")
    private Long reviewer;

    @Schema(description = "是否附上报告作为附件：0否 1是")
    private Integer isReport;

    @Schema(description = "是否涉及病原微生物：0否 1是")
    private Integer isPm;

    @Schema(description = "源表添加时间")
    private LocalDateTime srcCreateTime;

    @Schema(description = "源表编辑时间")
    private LocalDateTime srcUpdateTime;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

}
