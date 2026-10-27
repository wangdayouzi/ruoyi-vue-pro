package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("pms_knowledge_attribute")
@KeySequence("pms_knowledge_attribute_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PmsKnowledgeAttributeDO extends BaseDO {
    @TableId private Long id;
    private Long libraryId;
    private String fieldCode;
    private String fieldName;
    /** text, textarea, number, date, select, multiselect, user */
    private String fieldType;
    private String optionsJson;
    private Boolean required;
    private Boolean enabled;
    private Integer sort;
}
