package cn.iocoder.yudao.module.infra.dal.dataobject.number;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("infra_number_issue")
@KeySequence("infra_number_issue_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class NumberIssueDO extends BaseDO {
    @TableId
    private Long id;
    private Long ruleId;
    private String numberValue;
    private String bizType;
    private Long bizId;
}
