package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("pms_knowledge_review_rule")
@KeySequence("pms_knowledge_review_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PmsKnowledgeReviewRuleDO extends BaseDO {
    @TableId private Long id;
    private Long libraryId;
    /** 0 means the library default; otherwise only a first-level folder may be selected. */
    private Long folderId;
    private Integer intervalMonths;
    private Integer remindDays;
    private Boolean enabled;
}
