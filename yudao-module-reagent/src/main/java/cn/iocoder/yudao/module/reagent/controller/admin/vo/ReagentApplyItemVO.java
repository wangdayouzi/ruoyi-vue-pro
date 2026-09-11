package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 申请单明细 VO（嵌入式，用于 SaveReqVO 的 items 列表）
 */
@Schema(description = "管理后台 - 申请单明细 VO")
@Data
public class ReagentApplyItemVO {

    @Schema(description = "明细主键", example = "1")
    private Long id;

    @Schema(description = "试剂编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "BAS-001")
    @NotBlank(message = "试剂编号不能为空")
    private String basId;

    @Schema(description = "BAS号/采购入库单号 pm02603（选择弹窗带入）", example = "SH-BAS262892")
    private String basNo;

    @Schema(description = "试剂名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Anti-CD3 Antibody")
    @NotBlank(message = "试剂名称不能为空")
    private String reagentName;

    @Schema(description = "供应商（选择弹窗带入，可手改）", example = "Sino Biological")
    private String vendor;

    @Schema(description = "品牌（选择弹窗带入，可手改）", example = "Biolegend")
    private String brand;

    @Schema(description = "货号", example = "SAB5600832")
    private String catNo;

    @Schema(description = "规格/浓度", example = "100μg")
    private String content;

    @Schema(description = "批号", example = "LOT20240001")
    private String lotNo;

    @Schema(description = "储存温度", example = "2-8°C")
    private String storageTemp;

    @Schema(description = "储存位置", example = "A区-1号冰箱-2层")
    private String storageLocation;

    @Schema(description = "过期日期")
    private LocalDateTime expirationDate;

    @Schema(description = "需求总数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    @NotNull(message = "需求数量不能为空")
    @DecimalMin(value = "0.01", message = "需求数量必须大于 0")
    private BigDecimal requestedQty;

    @Schema(description = "已累计发货数量", example = "0.00")
    private BigDecimal shippedQtyTotal;

}
