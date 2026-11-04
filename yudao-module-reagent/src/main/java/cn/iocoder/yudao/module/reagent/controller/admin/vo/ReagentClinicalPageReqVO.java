package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 临床方案 分页 Request VO
 */
@Schema(description = "管理后台 - 临床方案 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentClinicalPageReqVO extends PageParam {

    @Schema(description = "临床方案名称，模糊匹配")
    @Size(max = 255, message = "临床方案名称长度不能超过 255 个字符")
    private String name;

    @Schema(description = "临床方案编号，模糊匹配")
    @Size(max = 100, message = "临床方案编号长度不能超过 100 个字符")
    private String number;

    @Schema(description = "申办方ID")
    private Long sponsorId;

    @Schema(description = "状态：0正常，1停用")
    private Integer status;

}
