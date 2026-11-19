package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentSaveReqVO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@TableName(value = "sample_urgent_plan", autoResultMap = true)
@KeySequence("sample_urgent_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleUrgentPlanDO extends TenantBaseDO {
    @TableId private Long id;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private SampleUrgentSaveReqVO template;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime dailyTime;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> weekdays;
    private Long requesterId;
    private String requesterName;
    /** 0启用，1暂停，2结束（包括自然到期）。 */
    private Integer status;
    private Integer revision;
    /** 下一待检查日期；暂停恢复时跳过暂停期间。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate nextDate;
    private String lastError;
}
