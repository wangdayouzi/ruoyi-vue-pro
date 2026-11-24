package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Schema(description = "管理后台 - 基质入库预约查询")
@EqualsAndHashCode(callSuper = true)
public class MatrixInboundPageReqVO extends PageParam {
    @Pattern(regexp = "NINGBO|SHANGHAI")
    private String site;
    @Pattern(regexp = "B|P")
    private String matrixKind;
    @Size(max = 12)
    private String basNo;
    @Size(max = 200)
    private String applicantName;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;
    @Pattern(regexp = "all|unregistered|pending|partial|yes")
    private String result;
    @Pattern(regexp = "all|mine|today|urgent")
    private String view;
    @Pattern(regexp = "asc|desc")
    private String dateOrder;
}
