package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 试剂标签打印 Req VO
 *
 * 打印时前端将 名称/BASID/批号/存储条件/存储位置/过期日期/接收人/接收日期/备注 一起提交，
 * 后端渲染 Excel 模板（键值对表格，每行一个键值对）。
 */
@Schema(description = "管理后台 - 试剂标签打印 Req VO")
@Data
public class ReagentLabelPrintReqVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "BASID（试剂编号，必填）", example = "NB-BAS261633")
    @NotBlank(message = "BASID 不能为空")
    private String basId;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "存储条件")
    private String storageCondition;

    @Schema(description = "存储位置")
    private String storageLocation;

    @Schema(description = "过期日期")
    private String expireDate;

    @Schema(description = "接收人")
    private String receiverName;

    @Schema(description = "接收日期")
    private String receiveDate;

    @Schema(description = "备注")
    private String remark;

}
