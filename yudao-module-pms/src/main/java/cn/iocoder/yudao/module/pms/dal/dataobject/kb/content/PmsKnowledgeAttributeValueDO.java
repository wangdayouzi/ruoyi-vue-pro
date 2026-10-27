package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("pms_knowledge_attribute_value")
@KeySequence("pms_knowledge_attribute_value_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PmsKnowledgeAttributeValueDO extends BaseDO {
    @TableId private Long id;
    private Long documentId;
    private Long attributeId;
    private String valueText;
}
