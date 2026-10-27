package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@TableName(value = "pms_knowledge_document_draft", autoResultMap = true)
@KeySequence("pms_knowledge_document_draft_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PmsKnowledgeDocumentDraftDO extends BaseDO {
    @TableId
    private Long id;
    private Long documentId;
    private Long userId;
    private Long baseVersionNo;
    private String title;
    private String content;
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> labelIds;
}
