package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** A complete, immutable snapshot of one knowledge document revision. */
@TableName("pms_knowledge_document_version")
@KeySequence("pms_knowledge_document_version_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class PmsKnowledgeDocumentVersionDO extends BaseDO {
    @TableId
    private Long id;
    private Long documentId;
    private Long versionNo;
    private String title;
    private String content;
    private Integer type;
    private String fileType;
    private Long fileSize;
    private Long editorUserId;
}
