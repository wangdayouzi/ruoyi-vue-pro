package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** 试剂标签打印任务。任务内容在创建时固化，避免源数据随后变化而造成标签不一致。 */
@TableName("reagent_label_print_job")
@KeySequence("reagent_label_print_job_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentLabelPrintJobDO extends BaseDO {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CLAIMED = 1;
    public static final int STATUS_PRINTING = 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_FAILED = 4;
    public static final int STATUS_CANCELED = 5;

    @TableId
    private Long id;
    private String jobNo;
    private Long printerId;
    private String templateCode;
    /** 标签字段 JSON 快照 */
    private String dataSnapshot;
    private Integer copies;
    private Integer status;
    /** 领取令牌，只有持有令牌的代理能回写结果 */
    private String claimToken;
    private String agentCode;
    private Integer printedCount;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime claimedTime;
    private LocalDateTime completedTime;
}
