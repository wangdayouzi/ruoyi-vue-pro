package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 老ERP同步 试剂基础数据(扁平) Response VO
 */
@Schema(description = "管理后台 - 试剂基础数据(扁平) Response VO")
@Data
public class ReagentBaseFlatRespVO {

    @Schema(description = "主键编号")
    private Long id;

    @Schema(description = "采购入库单号")
    private String basId;

    @Schema(description = "试剂编号(材料编号)")
    private String reagentCode;

    @Schema(description = "试剂名称")
    private String reagentName;

    @Schema(description = "供应商")
    private String vendor;
    @Schema(description = "品牌")
    private String brand;
    @Schema(description = "仓库名")
    private String warehouse;

    @Schema(description = "货号")
    private String catNo;

    @Schema(description = "规格")
    private String spec;

    @Schema(description = "批号")
    private String lotNo;

    @Schema(description = "过期日期")
    private String expireDate;

    @Schema(description = "参考剩余量")
    private String amountLeft;

    @Schema(description = "存储位置")
    private String storageLocation;

    @Schema(description = "储存温度")
    private String storageTemp;

    @Schema(description = "分类名")
    private String itemCategory;

    @Schema(description = "分类键")
    private String categoryKey;

    @Schema(description = "入库明细行ID")
    private String srcLineId;

    @Schema(description = "入库主ID")
    private String srcReceiptId;

    @Schema(description = "状态：0正常, 1停用")
    private Integer status;

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

}
