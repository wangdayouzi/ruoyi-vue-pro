package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=true)
public class SampleUrgentPageReqVO extends PageParam {
    @Size(max=200) private String keyword;
    @Size(max=200) private String contacts;
    @Min(0) @Max(4) private Integer status;
    @Pattern(regexp="ALL|MINE|SUBMITTED|HANDLING|TODAY|OVERDUE") private String view;
    private Boolean exceptionOnly;
}
