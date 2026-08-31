package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 生物试剂接收单生成 Request VO")
@Data
public class ReagentReceiptReqVO {

    @Schema(description = "试剂名称")
    private String name;

    @Schema(description = "BAS编号")
    private String basId;

    @Schema(description = "供应商")
    private String vendor;

    @Schema(description = "接收日期")
    private String receiveDate;

    @Schema(description = "试剂数量")
    private String qty;

    @Schema(description = "单个容量")
    private String contentPerUnit;

    @Schema(description = "批号")
    private String lotNo;

    @Schema(description = "货号")
    private String catNo;

    @Schema(description = "储存位置")
    private String storageLocation;

    @Schema(description = "储存温度")
    private String storageTemp;

    @Schema(description = "过期日期")
    private String expireDate;

    @Schema(description = "说明")
    private String comment;

}
