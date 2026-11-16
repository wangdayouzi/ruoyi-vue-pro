package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeValueDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PmsKnowledgeAttributeValueMapper extends BaseMapperX<PmsKnowledgeAttributeValueDO> {
    default List<PmsKnowledgeAttributeValueDO> listByDocument(Long documentId) {
        return selectList(PmsKnowledgeAttributeValueDO::getDocumentId, documentId);
    }

    default List<PmsKnowledgeAttributeValueDO> listByDocumentsAndAttributes(
            Collection<Long> documentIds, Collection<Long> attributeIds) {
        if (documentIds.isEmpty() || attributeIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeAttributeValueDO>()
                .in(PmsKnowledgeAttributeValueDO::getDocumentId, documentIds)
                .in(PmsKnowledgeAttributeValueDO::getAttributeId, attributeIds));
    }
}
