package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目号 分页 Request VO
 */
@Schema(description = "管理后台 - 项目号 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentItemPageReqVO extends PageParam {

    @Schema(description = "项目号，模糊匹配")
    @Size(max = 255, message = "项目号长度不能超过 255 个字符")
    private String baseId;

    @Schema(description = "临床方案ID")
    private Long clinicalId;

    @Schema(description = "临床方案编号，模糊匹配")
    @Size(max = 255, message = "临床方案编号长度不能超过 255 个字符")
    private String clinicalNumber;

    @Schema(description = "临床方案名称，模糊匹配")
    @Size(max = 255, message = "临床方案名称长度不能超过 255 个字符")
    private String clinicalName;

    @Schema(description = "城市：1上海，2宁波")
    private Integer cityId;

    @Schema(description = "复核状态：0未复核，1已复核")
    private Integer reviewStatus;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

}
