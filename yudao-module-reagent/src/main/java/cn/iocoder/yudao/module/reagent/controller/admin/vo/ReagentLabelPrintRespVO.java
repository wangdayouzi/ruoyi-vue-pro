package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试剂标签打印 Response VO
 *
 * 数据来源：只读 SQL Server 数据源（PM 系统），按 BASID 查询
 */
@Schema(description = "管理后台 - 试剂标签打印 Response VO")
@Data
public class ReagentLabelPrintRespVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "BASID（试剂编号）", example = "NB-BAS261633")
    private String basId;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "存储位置")
    private String storageLocation;

    @Schema(description = "过期日期")
    private String expireDate;

}
