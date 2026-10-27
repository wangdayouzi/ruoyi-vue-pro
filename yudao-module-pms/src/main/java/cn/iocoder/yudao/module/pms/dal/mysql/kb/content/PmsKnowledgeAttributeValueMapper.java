package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeValueDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PmsKnowledgeAttributeValueMapper extends BaseMapperX<PmsKnowledgeAttributeValueDO> {
    default List<PmsKnowledgeAttributeValueDO> listByDocument(Long documentId) {
        return selectList(PmsKnowledgeAttributeValueDO::getDocumentId, documentId);
    }
}
