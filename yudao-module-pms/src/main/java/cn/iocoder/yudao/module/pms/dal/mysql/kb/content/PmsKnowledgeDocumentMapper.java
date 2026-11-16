package cn.iocoder.yudao.module.pms.dal.mysql.kb.content;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentSearchPageReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.label.PmsKnowledgeDocumentLabelPageReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Mapper
public interface PmsKnowledgeDocumentMapper extends BaseMapperX<PmsKnowledgeDocumentDO> {

    default List<PmsKnowledgeDocumentDO> selectSearchDocuments(Long tenantId, Collection<Long> ids, Integer status) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .select(PmsKnowledgeDocumentDO::getId, PmsKnowledgeDocumentDO::getSearchText,
                        PmsKnowledgeDocumentDO::getLibraryId, PmsKnowledgeDocumentDO::getPermissionId)
                .in(PmsKnowledgeDocumentDO::getId, ids)
                .apply(tenantId != null, "tenant_id = {0}", tenantId)
                .eq(PmsKnowledgeDocumentDO::getStatus, status)
                .isNotNull(PmsKnowledgeDocumentDO::getSearchText));
    }

    default List<PmsKnowledgeDocumentDO> selectSearchBatch(Long tenantId, long afterId, Integer status) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .select(PmsKnowledgeDocumentDO::getId, PmsKnowledgeDocumentDO::getSearchText,
                        PmsKnowledgeDocumentDO::getLibraryId, PmsKnowledgeDocumentDO::getPermissionId)
                .gt(PmsKnowledgeDocumentDO::getId, afterId)
                .apply(tenantId != null, "tenant_id = {0}", tenantId)
                .eq(PmsKnowledgeDocumentDO::getStatus, status)
                .isNotNull(PmsKnowledgeDocumentDO::getSearchText)
                .orderByAsc(PmsKnowledgeDocumentDO::getId).last("LIMIT 100"));
    }

    @org.apache.ibatis.annotations.Select("SELECT id FROM pms_knowledge_document WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    Long lockDocument(Long id);

    default List<PmsKnowledgeDocumentDO> selectListByLabelId(Long labelId) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .apply(MyBatisUtils.findInSet("label_ids"), labelId));
    }

    default Set<Long> selectExistingPermissionIdSet(Collection<Long> permissionIds) {
        return convertSet(selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .in(PmsKnowledgeDocumentDO::getPermissionId, permissionIds)
                .select(PmsKnowledgeDocumentDO::getPermissionId)), PmsKnowledgeDocumentDO::getPermissionId);
    }

    /**
     * 统计各知识库下不同文档类型的数量
     *
     * @param libraryIds 知识库编号集合
     * @param status 文档状态
     * @return 知识库编号 ->（文档类型 -> 文档数量）
     */
    default Map<Long, Map<Integer, Long>> selectTypeCountMapByLibraryIdsAndStatus(
            Collection<Long> libraryIds, Integer status) {
        if (CollUtil.isEmpty(libraryIds)) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> rows = selectMaps(new QueryWrapperX<PmsKnowledgeDocumentDO>()
                .select("library_id", "type", "COUNT(*) AS count")
                .in("library_id", libraryIds)
                .eq("status", status)
                .groupBy("library_id", "type"));
        Map<Long, Map<Integer, Long>> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long libraryId = ((Number) row.get("library_id")).longValue();
            Integer type = ((Number) row.get("type")).intValue();
            result.computeIfAbsent(libraryId, key -> new LinkedHashMap<>())
                    .put(type, ((Number) row.get("count")).longValue());
        }
        return result;
    }

    default List<PmsKnowledgeDocumentDO> selectListByLibraryIdAndStatus(Long libraryId, Integer status) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .eq(PmsKnowledgeDocumentDO::getLibraryId, libraryId)
                .eq(PmsKnowledgeDocumentDO::getStatus, status)
                .orderByAsc(PmsKnowledgeDocumentDO::getCreateTime)
                .orderByAsc(PmsKnowledgeDocumentDO::getId));
    }

    default List<PmsKnowledgeDocumentDO> selectListByLibraryId(Long libraryId) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .eq(PmsKnowledgeDocumentDO::getLibraryId, libraryId)
                .orderByAsc(PmsKnowledgeDocumentDO::getId));
    }

    default List<PmsKnowledgeDocumentDO> selectListByParentIds(Collection<Long> parentIds) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .in(PmsKnowledgeDocumentDO::getParentId, parentIds));
    }

    default List<PmsKnowledgeDocumentDO> selectListByFolderIds(Collection<Long> folderIds) {
        return selectList(new LambdaQueryWrapperX<PmsKnowledgeDocumentDO>()
                .in(PmsKnowledgeDocumentDO::getFolderId, folderIds));
    }

    default int updateToRestoreByIds(Collection<Long> ids, Integer status) {
        return update(new LambdaUpdateWrapper<PmsKnowledgeDocumentDO>()
                .set(PmsKnowledgeDocumentDO::getStatus, status)
                .set(PmsKnowledgeDocumentDO::getDeleteUserId, null)
                .set(PmsKnowledgeDocumentDO::getDeleteTime, null)
                .in(PmsKnowledgeDocumentDO::getId, ids));
    }

    /**
     * 按标签和状态分页查询文档，并限制在用户可读的知识库和权限范围内
     *
     * @param pageReqVO 标签分页查询条件
     * @param readableLibraryIds 用户可读的知识库编号集合
     * @param readablePermissionIds 用户可读的内容权限编号集合
     * @param statuses 文档状态集合
     * @return 文档分页结果
     */
    default PageResult<PmsKnowledgeDocumentDO> selectPageByLabelIdAndStatuses(
            PmsKnowledgeDocumentLabelPageReqVO pageReqVO, Collection<Long> readableLibraryIds,
            Collection<Long> readablePermissionIds, Collection<Integer> statuses) {
        LambdaQueryWrapperX<PmsKnowledgeDocumentDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.in(PmsKnowledgeDocumentDO::getLibraryId, readableLibraryIds)
                .in(PmsKnowledgeDocumentDO::getPermissionId, readablePermissionIds)
                .in(PmsKnowledgeDocumentDO::getStatus, statuses)
                .orderByDesc(PmsKnowledgeDocumentDO::getUpdateTime)
                .orderByDesc(PmsKnowledgeDocumentDO::getId);
        queryWrapper.apply(MyBatisUtils.findInSet("label_ids"), pageReqVO.getLabelId());
        return selectPage(pageReqVO, queryWrapper);
    }

    /**
     * 分页查询文档，并限制在用户可读的知识库、权限和状态范围内
     *
     * @param pageReqVO 文档分页查询条件
     * @param readableLibraryIds 用户可读的知识库编号集合
     * @param readablePermissionIds 用户可读的内容权限编号集合
     * @param statuses 文档状态集合
     * @return 文档分页结果
     */
    default PageResult<PmsKnowledgeDocumentDO> selectPage(PmsKnowledgeDocumentSearchPageReqVO pageReqVO,
                                                          Collection<Long> readableLibraryIds,
                                                          Collection<Long> readablePermissionIds,
                                                          Collection<Integer> statuses) {
        return selectPage(pageReqVO, readableLibraryIds, readablePermissionIds, statuses, null);
    }

    default PageResult<PmsKnowledgeDocumentDO> selectPage(PmsKnowledgeDocumentSearchPageReqVO pageReqVO,
                                                          Collection<Long> readableLibraryIds,
                                                          Collection<Long> readablePermissionIds,
                                                          Collection<Integer> statuses,
                                                          Collection<Long> candidateIds) {
        LambdaQueryWrapperX<PmsKnowledgeDocumentDO> query = new LambdaQueryWrapperX<>();
        query.in(candidateIds != null, PmsKnowledgeDocumentDO::getId, candidateIds);
        query.and(candidateIds == null && StrUtil.isNotBlank(pageReqVO.getKeyword()),
                wrapper -> wrapper.apply("LOWER(title) LIKE CONCAT('%', LOWER({0}), '%')", pageReqVO.getKeyword())
                        .or().apply("LOWER(content) LIKE CONCAT('%', LOWER({0}), '%')", pageReqVO.getKeyword()));
        if (CollUtil.isNotEmpty(pageReqVO.getAttributeFilters())) {
            for (PmsKnowledgeDocumentSearchPageReqVO.AttributeFilter filter : pageReqVO.getAttributeFilters()) {
                query.apply("""
                        EXISTS (SELECT 1 FROM pms_knowledge_attribute_value av
                            JOIN pms_knowledge_attribute a ON a.id = av.attribute_id AND a.tenant_id = av.tenant_id
                            WHERE av.document_id = pms_knowledge_document.id AND av.deleted = 0
                              AND av.tenant_id = pms_knowledge_document.tenant_id
                              AND a.library_id = pms_knowledge_document.library_id AND a.deleted = 0
                              AND a.enabled = true AND a.field_code = {0}
                              AND LOWER(av.value_text) LIKE CONCAT('%', LOWER({1}), '%'))
                        """, filter.getFieldCode(), filter.getKeyword().trim());
            }
        }
        return selectPage(pageReqVO, query
                .in(PmsKnowledgeDocumentDO::getLibraryId, readableLibraryIds)
                .in(PmsKnowledgeDocumentDO::getPermissionId, readablePermissionIds)
                .in(PmsKnowledgeDocumentDO::getStatus, statuses)
                .eqIfPresent(PmsKnowledgeDocumentDO::getLibraryId, pageReqVO.getLibraryId())
                .eqIfPresent(PmsKnowledgeDocumentDO::getCreator,
                        Convert.toStr(pageReqVO.getCreatorUserId()))
                .betweenIfPresent(PmsKnowledgeDocumentDO::getUpdateTime, pageReqVO.getUpdateTime())
                .apply(StrUtil.isNotBlank(pageReqVO.getDocumentCode()),
                        "LOWER(document_code) LIKE CONCAT('%', LOWER({0}), '%')",
                        StrUtil.trim(pageReqVO.getDocumentCode()))
                .apply("unreviewed".equals(pageReqVO.getReviewResult()),
                        "NOT EXISTS (SELECT 1 FROM pms_knowledge_review_record rr WHERE rr.document_id = pms_knowledge_document.id AND rr.deleted = 0 AND rr.tenant_id = pms_knowledge_document.tenant_id)")
                .apply(pageReqVO.getReviewResult() != null && java.util.Set.of("valid", "pending", "replaced", "abolished").contains(pageReqVO.getReviewResult()),
                        "(SELECT rr.result FROM pms_knowledge_review_record rr WHERE rr.document_id = pms_knowledge_document.id AND rr.deleted = 0 AND rr.tenant_id = pms_knowledge_document.tenant_id ORDER BY rr.id DESC LIMIT 1) = {0}",
                        pageReqVO.getReviewResult())
                .apply(StrUtil.isNotBlank(pageReqVO.getAttributeKeyword()),
                        "EXISTS (SELECT 1 FROM pms_knowledge_attribute_value av "
                                + "WHERE av.document_id = pms_knowledge_document.id AND av.deleted = 0 "
                                + "AND av.tenant_id = pms_knowledge_document.tenant_id "
                                + "AND LOWER(av.value_text) LIKE CONCAT('%', LOWER({0}), '%'))",
                        StrUtil.trim(pageReqVO.getAttributeKeyword()))
                .orderByDesc(PmsKnowledgeDocumentDO::getUpdateTime)
                .orderByDesc(PmsKnowledgeDocumentDO::getId));
    }

}
