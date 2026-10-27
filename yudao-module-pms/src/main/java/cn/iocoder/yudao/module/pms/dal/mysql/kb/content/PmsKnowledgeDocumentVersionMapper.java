package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PmsKnowledgeDocumentVersionMapper extends BaseMapperX<PmsKnowledgeDocumentVersionDO> {
    default List<PmsKnowledgeDocumentVersionDO> selectListByDocumentId(Long documentId) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentVersionDO>()
                .eq(PmsKnowledgeDocumentVersionDO::getDocumentId, documentId)
                .orderByDesc(PmsKnowledgeDocumentVersionDO::getVersionNo));
    }

    default PmsKnowledgeDocumentVersionDO selectByDocumentIdAndVersionNo(Long documentId, Long versionNo) {
        return selectOne(new LambdaQueryWrapperX<PmsKnowledgeDocumentVersionDO>()
                .eq(PmsKnowledgeDocumentVersionDO::getDocumentId, documentId)
                .eq(PmsKnowledgeDocumentVersionDO::getVersionNo, versionNo));
    }

    default void deleteByDocumentIds(Collection<Long> documentIds) {
        if (documentIds != null && !documentIds.isEmpty()) {
            delete(new LambdaQueryWrapperX<PmsKnowledgeDocumentVersionDO>()
                    .in(PmsKnowledgeDocumentVersionDO::getDocumentId, documentIds));
        }
    }
}
