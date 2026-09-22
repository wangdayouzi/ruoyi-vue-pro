package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 样品领用台账。样品独立于试剂库存，不和 ERP、LIMS 建立关联。
 */
@TableName("sample_loan")
@KeySequence("sample_loan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleLoanDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** BAS 号 */
    private String basNo;
    /** 需求人用户 ID */
    private Long requesterId;
    /** 需求人 */
    private String requester;
    /** 提单人用户 ID */
    private Long submitterId;
    /** 提单人 */
    private String submitter;
    /** 样品信息（选填） */
    private String sampleInfo;
    /** 备注（选填） */
    private String remark;
    /** 1-领用中；2-已归还 */
    private Integer status;
    /** 实际归还时间 */
    private LocalDateTime returnTime;
}
