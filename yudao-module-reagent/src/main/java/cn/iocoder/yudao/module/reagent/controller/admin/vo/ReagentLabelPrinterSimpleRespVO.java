package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 试剂标签打印机简要信息")
@Data
public class ReagentLabelPrinterSimpleRespVO {
    private Long id;
    private String name;
    private String model;
    private Integer tapeWidthMm;
    private String templateCode;
    /** 0-离线，1-在线，2-故障 */
    private Integer onlineStatus;
}
