package cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 老ERP采购入库单明细 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesPmInboundPageReqVO extends PageParam {

    @Schema(description = "单号/BAS")
    private String basId;

    @Schema(description = "物料名称/编码")
    private String itemName;

    @Schema(description = "供应商名称")
    private String vendorName;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "物料分类")
    private String itemCategory;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "接受日期范围")
    private String[] receiptDate;

}
