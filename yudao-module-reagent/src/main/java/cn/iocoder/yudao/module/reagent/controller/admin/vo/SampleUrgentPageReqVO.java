package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper=true)
public class SampleUrgentPageReqVO extends PageParam {
    @Size(max=200) private String keyword;
    @Size(max=200) private String contacts;
    @Min(0) @Max(4) private Integer status;
    @Pattern(regexp="ALL|MINE|SUBMITTED|HANDLING|TODAY|OVERDUE") private String view;
    @Pattern(regexp = "ALL|NINGBO|SHANGHAI") private String inboundSite;
    private Boolean exceptionOnly;
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Size(max = 2) private LocalDateTime[] deadline;
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Size(max = 2) private LocalDateTime[] createTime;
    @Pattern(regexp = "deadline|createTime") private String sortField;
    @Pattern(regexp = "asc|desc") private String sortOrder;
}
