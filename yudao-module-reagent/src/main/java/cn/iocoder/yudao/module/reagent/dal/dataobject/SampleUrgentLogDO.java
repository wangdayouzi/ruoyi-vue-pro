package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@TableName("sample_urgent_log")
@KeySequence("sample_urgent_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleUrgentLogDO extends TenantBaseDO {
    @TableId private Long id;
    private Long urgentId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String content;
}
