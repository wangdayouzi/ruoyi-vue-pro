package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

/** 一次样品加急需求，不直接改变 ERP 或样品库存。 */
@TableName(value="sample_urgent",autoResultMap=true)
@KeySequence("sample_urgent_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SampleUrgentDO extends TenantBaseDO {
    @TableId private Long id;
    /** 数据库按登记日期生成，独立于内部主键，不允许业务修改。 */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private String urgentNo;
    private String projectNo;
    /** 临床方案ID（老样本系统 reagent_clinical.id） */
    private Long clinicalId;
    private Boolean newProject;
    private String basNos;
    private String sampleScope;
    private LocalDateTime deadline;
    private LocalDateTime expectedArrival;
    private LocalDateTime plannedTestTime;
    private String contacts;
    private String legacyContacts;
    @TableField(typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private java.util.List<Long> appointmentUserIds;
    @TableField(typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private java.util.List<Long> noticeUserIds;
    @TableField(typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private java.util.List<Long> noticeDeptIds;
    private String noticeNames;
    private String reason;
    private String logisticsNo;
    private String remark;
    private Long requesterId;
    private String requesterName;
    private Long handlerId;
    private String handlerName;
    /** 0待处理 1处理中 2部分入库 3已完成 4已取消 */
    private Integer status;
    /** NONE / NOT_ARRIVED / WAIT_REPLY / SAMPLE_ISSUE / DELAY / OTHER */
    private String exceptionType;
    private String exceptionNote;
    private String completedScope;
    private String remainingScope;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime completedTime;
    private Integer revision;
}
