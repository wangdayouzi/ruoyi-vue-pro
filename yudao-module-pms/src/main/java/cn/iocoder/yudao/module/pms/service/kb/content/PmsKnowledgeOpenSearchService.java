package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentSearchPageReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentMapper;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_DOCUMENT_SEARCH_TOO_MANY_MATCHES;

@Service
@Slf4j
@RequiredArgsConstructor
public class PmsKnowledgeOpenSearchService implements PmsKnowledgeSearchIndex {

    private static final int MAX_CANDIDATES = 2000;
    private static final long MAX_BULK_BYTES = 2_000_000;
    private static final List<Integer> SEARCHABLE_STATUSES = List.of(PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus());

    private final PmsKnowledgeDocumentMapper documentMapper;
    private final ObjectProvider<OpenSearchClient> openSearchClientProvider;
    private final PmsKnowledgeSearchSyncQueue syncQueue;
    private final PlatformTransactionManager transactionManager;
    private final AtomicBoolean syncing = new AtomicBoolean();

    @Value("${pms.knowledge.search.index-prefix:pms-kb-v1}")
    private String indexPrefix;
    @Value("${yudao.tenant.enable:false}")
    private boolean tenantEnabled;

    // OpenSearch 负责关键词匹配，数据库负责权限、排序、分页和最终内容。
    @Override
    public PageResult<PmsKnowledgeDocumentDO> search(PmsKnowledgeDocumentSearchPageReqVO request,
                                                    List<Long> libraryIds, Set<Long> permissionIds) {
        String keyword = request.getKeyword();
        Long tenantId = tenantEnabled ? TenantContextHolder.getTenantId() : null;
        if ((tenantEnabled && (tenantId == null || TenantContextHolder.isIgnore()))
                || keyword == null || keyword.isBlank()
                || TransactionSynchronizationManager.isActualTransactionActive()) {
            return null;
        }
        if (!indexPrefix.matches("[a-z0-9][a-z0-9-]{0,79}")) {
            return null;
        }

        String index = indexName(tenantId);
        try {
            OpenSearchClient client = openSearchClientProvider.getIfAvailable();
            if (client == null) {
                return null;
            }
            try {
                return searchIndex(client, request, libraryIds, permissionIds, index + "-current");
            } catch (OpenSearchException e) {
                if (e.status() != 404) {
                    throw e;
                }
                // 旧版使用同名物理索引，后台首次发布前可继续查询。
                return searchIndex(client, request, libraryIds, permissionIds, index);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            log.warn("PMS 知识库索引不可用，索引={}，异常={}，根因={}", index,
                    e.getClass().getSimpleName(),
                    NestedExceptionUtils.getMostSpecificCause(e).getClass().getSimpleName());
            return null;
        }
    }

    @Async
    @Scheduled(initialDelay = 5000, fixedDelay = 2000)
    public void synchronizeDocuments() {
        if (!indexPrefix.matches("[a-z0-9][a-z0-9-]{0,79}") || !syncing.compareAndSet(false, true)) {
            return;
        }
        try {
            OpenSearchClient client = openSearchClientProvider.getIfAvailable();
            if (client != null) {
                TenantUtils.executeIgnore(() -> new TransactionTemplate(transactionManager)
                        .executeWithoutResult(status -> synchronizeBatch(client)));
            }
        } finally {
            syncing.set(false);
        }
    }

    private void synchronizeBatch(OpenSearchClient client) {
        if (!syncQueue.tryLock()) {
            return;
        }
        Map<Long, List<PmsKnowledgeSearchSyncQueue.Task>> scopes = new LinkedHashMap<>();
        for (var task : syncQueue.due(tenantEnabled)) {
            scopes.computeIfAbsent(tenantEnabled ? task.tenantId() : null, id -> new ArrayList<>()).add(task);
        }
        Map<PmsKnowledgeSearchSyncQueue.Task, Boolean> results = new HashMap<>();
        scopes.forEach((tenantId, tasks) -> {
            try {
                Set<Long> failed = synchronizeScope(client, tenantId, tasks);
                tasks.forEach(task -> results.put(task, !failed.contains(task.documentId())));
            } catch (IOException | RuntimeException e) {
                log.warn("PMS 知识库索引同步失败，索引={}，异常={}，根因={}", indexName(tenantId),
                        e.getClass().getSimpleName(),
                        NestedExceptionUtils.getMostSpecificCause(e).getClass().getSimpleName());
                tasks.forEach(task -> results.put(task, false));
            }
        });
        // 外部写入结束后再更新任务行，缩短文档事务等待这些行的时间。
        results.keySet().stream()
                .sorted(Comparator.comparingLong(PmsKnowledgeSearchSyncQueue.Task::tenantId)
                        .thenComparingLong(PmsKnowledgeSearchSyncQueue.Task::documentId))
                .forEach(task -> {
                    if (results.get(task)) {
                        syncQueue.complete(task);
                    } else {
                        syncQueue.retry(task);
                    }
                });
    }

    private Set<Long> synchronizeScope(OpenSearchClient client, Long tenantId,
                                  List<PmsKnowledgeSearchSyncQueue.Task> tasks) throws IOException {
        String index = indexName(tenantId);
        String alias = index + "-current";
        String generationPrefix = index + "-gen-";
        Set<String> previous = publishedGenerations(client, generationPrefix, alias);
        if (previous.isEmpty() || tasks.stream().anyMatch(PmsKnowledgeSearchSyncQueue.Task::forceRebuild)) {
            rebuildAndPublish(client, generationPrefix, alias, tenantId, previous);
            log.info("PMS 知识库搜索索引已同步，索引={}", alias);
        }
        List<Long> ids = tasks.stream().map(PmsKnowledgeSearchSyncQueue.Task::documentId)
                .filter(id -> id != 0).distinct().toList();
        if (ids.isEmpty()) {
            return Set.of();
        }
        Set<Long> failed = syncDocuments(client, alias, tenantId, ids, true);
        refreshIndex(client, alias);
        return failed;
    }

    private String indexName(Long tenantId) {
        return indexPrefix + "-" + (tenantId == null ? "all" : tenantId);
    }

    private PageResult<PmsKnowledgeDocumentDO> searchIndex(OpenSearchClient client,
                                                           PmsKnowledgeDocumentSearchPageReqVO request,
                                                           List<Long> libraryIds, Set<Long> permissionIds,
                                                           String index) throws IOException {
        List<Long> searchLibraries = request.getLibraryId() == null ? libraryIds : List.of(request.getLibraryId());
        List<Long> candidates = findCandidates(client, index, request.getKeyword(), searchLibraries, permissionIds);
        if (candidates == null) {
            return null;
        }
        return candidates.isEmpty() ? PageResult.empty()
                : documentMapper.selectPage(request, libraryIds, permissionIds, SEARCHABLE_STATUSES, candidates);
    }

    private void rebuildAndPublish(OpenSearchClient client, String generationPrefix, String alias,
                                   Long tenantId, Set<String> previous) throws IOException {
        String staging = generationPrefix + UUID.randomUUID();
        long maintenanceVersion = syncQueue.maintenanceVersion(tenantId);
        boolean publishing = false;
        try {
            rebuild(client, staging, tenantId);
            long replayLimit = syncQueue.replayLimit(tenantId);
            long afterId = 0;
            while (afterId < replayLimit) {
                List<Long> ids = syncQueue.replayIds(tenantId, afterId, replayLimit);
                if (ids.isEmpty()) {
                    break;
                }
                requireSuccessful(syncDocuments(client, staging, tenantId, ids, false));
                afterId = ids.get(ids.size() - 1);
            }
            refreshIndex(client, staging);
            if (syncQueue.maintenanceVersion(tenantId) != maintenanceVersion) {
                throw new IOException("OpenSearch rebuild superseded by a newer maintenance task");
            }
            // 一次请求切换别名。超时时服务端可能已执行，因此不能清理这个新索引。
            publishing = true;
            if (!client.indices().updateAliases(req -> req
                    .actions(action -> action.remove(remove -> remove.index(generationPrefix + "*")
                            .alias(alias).mustExist(false)))
                    .actions(action -> action.add(add -> add.index(staging).alias(alias)))).acknowledged()) {
                throw new IOException("OpenSearch alias switch not acknowledged");
            }
        } finally {
            if (!publishing) {
                deleteGeneration(client, staging);
            }
        }
        previous.stream().filter(name -> name.startsWith(generationPrefix))
                .forEach(name -> deleteGeneration(client, name));
    }

    private Set<Long> syncDocuments(OpenSearchClient client, String index, Long tenantId, List<Long> ids,
                                    boolean requireAlias) throws IOException {
        Map<Long, PmsKnowledgeDocumentDO> documents = new HashMap<>();
        for (var document : documentMapper.selectSearchDocuments(tenantId, ids, SEARCHABLE_STATUSES.get(0))) {
            if (document.getLibraryId() != null && document.getPermissionId() != null) {
                documents.put(document.getId(), document);
            }
        }
        List<BulkOperation> bulk = new ArrayList<>();
        Set<Long> failed = new HashSet<>();
        long estimatedBytes = 0;
        for (Long id : ids) {
            PmsKnowledgeDocumentDO document = documents.get(id);
            long bytes = document == null ? 256 : 6L * document.getSearchText().length() + 256;
            if (!bulk.isEmpty() && estimatedBytes + bytes > MAX_BULK_BYTES) {
                failed.addAll(upload(client, index, bulk, requireAlias));
                estimatedBytes = 0;
            }
            bulk.add(document == null ? BulkOperation.of(operation -> operation.delete(item -> item.id(id.toString())))
                    : indexDocument(document));
            estimatedBytes += bytes;
        }
        failed.addAll(upload(client, index, bulk, requireAlias));
        return failed;
    }

    private BulkOperation indexDocument(PmsKnowledgeDocumentDO document) {
        return BulkOperation.of(operation -> operation.index(item -> item.id(document.getId().toString())
                .document(Map.of("search_text", document.getSearchText(),
                        "library_id", document.getLibraryId(), "permission_id", document.getPermissionId()))));
    }

    private Set<String> publishedGenerations(OpenSearchClient client, String generationPrefix, String alias) throws IOException {
        try {
            return client.indices().getAlias(req -> req.index(generationPrefix + "*").name(alias)).result().keySet();
        } catch (OpenSearchException e) {
            if (e.status() == 404) {
                return Set.of();
            }
            throw e;
        }
    }

    private void deleteGeneration(OpenSearchClient client, String index) {
        try {
            client.indices().delete(req -> req.index(index));
        } catch (OpenSearchException e) {
            if (e.status() != 404) {
                log.warn("PMS 知识库旧索引清理失败，索引={}，状态={}", index, e.status());
            }
        } catch (IOException | RuntimeException e) {
            log.warn("PMS 知识库旧索引清理失败，索引={}，异常={}", index, e.getClass().getSimpleName());
        }
    }

    private void rebuild(OpenSearchClient client, String index, Long tenantId) throws IOException {
        client.indices().create(request -> request.index(index)
                .settings(settings -> settings.numberOfShards(1).numberOfReplicas(0)
                        .refreshInterval(interval -> interval.time("-1")))
                .mappings(mapping -> mapping
                        // 正文可以超过 Lucene 的单值 DocValues 字节上限。
                        .properties("search_text", property -> property.wildcard(wildcard ->
                                wildcard.docValues(false).copyTo("search_text_words")))
                        .properties("search_text_words", property -> property.text(words ->
                                words.analyzer("icu_analyzer")))
                        .properties("library_id", property -> property.long_(number -> number))
                        .properties("permission_id", property -> property.long_(number -> number))));
        long afterId = 0;
        while (true) {
            List<PmsKnowledgeDocumentDO> batch = documentMapper.selectSearchBatch(
                    tenantId, afterId, SEARCHABLE_STATUSES.get(0));
            if (batch.isEmpty()) {
                break;
            }
            List<BulkOperation> bulk = new ArrayList<>();
            long estimatedBytes = 0;
            for (PmsKnowledgeDocumentDO doc : batch) {
                afterId = doc.getId();
                if (doc.getLibraryId() == null || doc.getPermissionId() == null) {
                    continue;
                }
                String searchText = doc.getSearchText();
                // JSON 字符转义最多占六字节，另给字段和操作头预留空间。
                long documentBytes = 6L * searchText.length() + 256;
                if (!bulk.isEmpty() && estimatedBytes + documentBytes > MAX_BULK_BYTES) {
                    requireSuccessful(upload(client, index, bulk, false));
                    estimatedBytes = 0;
                }
                bulk.add(indexDocument(doc));
                estimatedBytes += documentBytes;
            }
            requireSuccessful(upload(client, index, bulk, false));
        }
    }

    private Set<Long> upload(OpenSearchClient client, String index, List<BulkOperation> bulk, boolean requireAlias)
            throws IOException {
        if (bulk.isEmpty()) {
            return Set.of();
        }
        var response = client.bulk(BulkRequest.of(request -> request.index(index)
                .requireAlias(requireAlias).operations(bulk)));
        bulk.clear();
        if (!response.errors()) {
            return Set.of();
        }
        Set<Long> failed = new HashSet<>();
        for (var item : response.items()) {
            if (item.error() != null) {
                failed.add(Long.valueOf(item.id()));
                log.warn("PMS 知识库索引写入失败，索引={}，文档={}，HTTP 状态={}，错误类型={}",
                        index, item.id(), item.status(), item.error().type());
            }
        }
        return failed;
    }

    private void requireSuccessful(Set<Long> failed) throws IOException {
        if (!failed.isEmpty()) {
            throw new IOException("OpenSearch bulk write failed");
        }
    }

    private void refreshIndex(OpenSearchClient client, String index) throws IOException {
        if (client.indices().refresh(request -> request.index(index)).shards().failed() > 0) {
            throw new IOException("OpenSearch index refresh failed");
        }
    }

    private List<Long> findCandidates(OpenSearchClient client, String index, String keyword,
                                      List<Long> libraryIds, Set<Long> permissionIds) throws IOException {
        String pattern = "*" + keyword.replace("\\", "\\\\").replace("*", "\\*").replace("?", "\\?") + "*";
        List<Query> filters = List.of(
                idFilter("library_id", libraryIds),
                idFilter("permission_id", permissionIds));
        Query substring = Query.of(query -> query.wildcard(wildcard -> wildcard.field("search_text")
                .value(pattern).caseInsensitive(true)));
        Query words = Query.of(query -> query.match(match -> match.field("search_text_words")
                .query(FieldValue.of(keyword)).operator(Operator.And)));
        SearchResponse<Void> response = client.search(request -> request.index(index)
                .source(source -> source.fetch(false)).size(MAX_CANDIDATES + 1)
                .trackTotalHits(total -> total.enabled(false))
                .query(query -> query.bool(bool -> bool.filter(filters)
                        .should(substring, words).minimumShouldMatch("1"))), Void.class);
        List<Hit<Void>> hits = response.hits().hits();
        if (response.timedOut() || response.shards().failed() > 0) {
            return null;
        }
        if (hits.size() > MAX_CANDIDATES) {
            throw exception(KNOWLEDGE_DOCUMENT_SEARCH_TOO_MANY_MATCHES);
        }
        List<Long> ids = new ArrayList<>(hits.size());
        for (Hit<Void> hit : hits) {
            ids.add(Long.valueOf(hit.id()));
        }
        return ids;
    }

    private Query idFilter(String field, Iterable<Long> ids) {
        List<FieldValue> values = new ArrayList<>();
        ids.forEach(id -> values.add(FieldValue.of(id.longValue())));
        return Query.of(query -> query.terms(terms -> terms.field(field).terms(value -> value.value(values))));
    }
}
