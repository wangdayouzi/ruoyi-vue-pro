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
 * 领用台账。领用记录独立于试剂库存，不和 ERP、LIMS 建立关联。
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
    /** 领用类型：SAMPLE-样品，REAGENT-试剂 */
    private String materialType;
    /** 领用地点：4楼、8楼 */
    private String location;
    /** 需求人用户 ID */
    private Long requesterId;
    /** 需求人 */
    private String requester;
    /** 提单人用户 ID */
    private Long submitterId;
    /** 提单人 */
    private String submitter;
    /** 物料信息（选填） */
    private String sampleInfo;
    /** 备注（选填） */
    private String remark;
    /** 1-可领用；2-已归还；3-无需归还 */
    private Integer status;
    /** 实际归还时间 */
    private LocalDateTime returnTime;
}
