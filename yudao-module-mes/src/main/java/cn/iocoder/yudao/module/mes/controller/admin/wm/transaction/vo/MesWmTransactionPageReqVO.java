package cn.iocoder.yudao.module.mes.controller.admin.wm.transaction.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - MES 库存事务流水 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MesWmTransactionPageReqVO extends PageParam {

    @Schema(description = "物料分类 id")
    private Long itemTypeId;

    @Schema(description = "物料分类名称，模糊")
    private String itemTypeName;

    @Schema(description = "物料编码/名称，模糊")
    private String itemName;

    @Schema(description = "原批号，模糊")
    private String lotNumber;

    @Schema(description = "供应商 id")
    private Long vendorId;

    @Schema(description = "批次编码")
    private String batchCode;

    @Schema(description = "仓库 id")
    private Long warehouseId;

    @Schema(description = "事务类型")
    private Integer type;

    @Schema(description = "业务类型")
    private Integer bizType;

    @Schema(description = "业务单号")
    private String bizCode;

    @Schema(description = "最小数量")
    private BigDecimal minQty;

    @Schema(description = "最大数量")
    private BigDecimal maxQty;

    @Schema(description = "业务开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime beginTime;

    @Schema(description = "业务结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

}
