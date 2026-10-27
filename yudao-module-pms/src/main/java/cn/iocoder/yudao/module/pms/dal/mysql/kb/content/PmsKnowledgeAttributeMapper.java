package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PmsKnowledgeAttributeMapper extends BaseMapperX<PmsKnowledgeAttributeDO> {
    default List<PmsKnowledgeAttributeDO> listByLibrary(Long libraryId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PmsKnowledgeAttributeDO>()
                .eq(PmsKnowledgeAttributeDO::getLibraryId, libraryId)
                .orderByAsc(PmsKnowledgeAttributeDO::getSort, PmsKnowledgeAttributeDO::getId));
    }
}
