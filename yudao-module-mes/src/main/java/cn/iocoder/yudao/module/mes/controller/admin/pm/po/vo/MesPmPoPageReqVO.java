package cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 老ERP采购订单数据 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesPmPoPageReqVO extends PageParam {

    @Schema(description = "采购订单号")
    private String poCode;

    @Schema(description = "物料名称/编码")
    private String itemName;

    @Schema(description = "供应商名称")
    private String vendorName;

    @Schema(description = "物料分类")
    private String itemCategory;

    @Schema(description = "订单日期范围")
    private String[] orderDate;

}
