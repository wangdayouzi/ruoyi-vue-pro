-- 确认 PMS 关键词搜索已切换到 OpenSearch 后执行。
-- 在包含 pms_knowledge_document 的 schema 下单独执行，不要放在事务中。
DROP INDEX CONCURRENTLY IF EXISTS idx_pms_kb_document_search_trgm;
