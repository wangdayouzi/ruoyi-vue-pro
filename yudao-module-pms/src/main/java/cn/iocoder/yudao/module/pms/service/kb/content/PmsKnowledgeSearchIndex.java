package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentSearchPageReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;

import java.util.List;
import java.util.Set;

public interface PmsKnowledgeSearchIndex {

    /**
     * 搜索当前用户可读的文档。返回 null 表示本次索引查询无法提供完整结果。
     */
    PageResult<PmsKnowledgeDocumentDO> search(PmsKnowledgeDocumentSearchPageReqVO request,
                                             List<Long> libraryIds, Set<Long> permissionIds);
}
