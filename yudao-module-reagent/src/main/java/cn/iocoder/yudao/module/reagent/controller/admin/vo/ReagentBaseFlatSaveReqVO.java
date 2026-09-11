package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mzt.logapi.starter.annotation.DiffLogField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 老ERP同步 试剂基础数据(扁平) 更新 Request VO
 * <p>
 * 只允许改可维护字段；入库单号/试剂编号/批号/过期日期/分类 由同步决定，不改。
 */
@Schema(description = "管理后台 - 试剂基础数据(扁平) 更新 Request VO")
@Data
public class ReagentBaseFlatSaveReqVO {

    @Schema(description = "主键编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键编号不能为空")
    private Long id;

    @Schema(description = "试剂名称")
    @DiffLogField(name = "试剂名称")
    private String reagentName;

    @Schema(description = "供应商")
    @DiffLogField(name = "供应商")
    private String vendor;

    @Schema(description = "仓库名")
    @DiffLogField(name = "仓库")
    private String warehouse;

    @Schema(description = "货号")
    @DiffLogField(name = "货号")
    private String catNo;

    @Schema(description = "规格")
    @DiffLogField(name = "规格")
    private String spec;

    @Schema(description = "储存温度")
    @DiffLogField(name = "储存温度")
    private String storageTemp;

    @Schema(description = "储存位置")
    @DiffLogField(name = "储存位置")
    private String storageLocation;

    @Schema(description = "参考剩余量")
    @DiffLogField(name = "参考剩余量")
    private String amountLeft;

    @Schema(description = "状态：0正常, 1停用")
    @DiffLogField(name = "状态")
    private Integer status;

}
