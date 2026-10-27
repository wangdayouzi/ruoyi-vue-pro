package cn.iocoder.yudao.module.infra.dal.dataobject.number;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("infra_number_rule")
@KeySequence("infra_number_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class NumberRuleDO extends BaseDO {
    @TableId
    private Long id;
    private String code;
    private String name;
    private String prefix;
    private Integer digits;
    private Long startNo;
    private Boolean yearlyReset;
    private Integer status;
    private Long lastNo;
    private Integer lastYear;
}
