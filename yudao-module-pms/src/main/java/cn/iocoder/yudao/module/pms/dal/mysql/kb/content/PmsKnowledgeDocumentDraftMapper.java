package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDraftDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PmsKnowledgeDocumentDraftMapper extends BaseMapperX<PmsKnowledgeDocumentDraftDO> {
    @org.apache.ibatis.annotations.Delete("DELETE FROM pms_knowledge_document_draft WHERE id = #{id}")
    int deletePublishedDraft(Long id);

    default PmsKnowledgeDocumentDraftDO selectByDocumentAndUser(Long documentId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<PmsKnowledgeDocumentDraftDO>()
                .eq(PmsKnowledgeDocumentDraftDO::getDocumentId, documentId)
                .eq(PmsKnowledgeDocumentDraftDO::getUserId, userId));
    }
}
