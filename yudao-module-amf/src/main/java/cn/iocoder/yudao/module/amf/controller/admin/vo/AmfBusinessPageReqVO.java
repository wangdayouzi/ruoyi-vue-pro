package cn.iocoder.yudao.module.amf.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析方法文件 - 分页查询 Request VO
 */
@Schema(description = "管理后台 - 分析方法文件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmfBusinessPageReqVO extends PageParam {

    @Schema(description = "BAS编号，模糊匹配", example = "BAS-2024")
    private String basNo;

    @Schema(description = "临床方案编号，模糊匹配", example = "PROTOCOL-2024")
    private String protocolNo;

    @Schema(description = "申办方，模糊匹配", example = "某某医药")
    private String sponsor;

    @Schema(description = "分析方法，模糊匹配", example = "HPLC")
    private String analysisMethod;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
