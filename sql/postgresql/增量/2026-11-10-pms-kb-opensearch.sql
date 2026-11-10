-- 在包含 pms_knowledge_document 的 schema 中执行。
-- 文档事务提交时推进租户索引版本。回滚时版本和文档一起回滚。
BEGIN;

CREATE TABLE IF NOT EXISTS pms_kb_search_revision (
    tenant_id bigint PRIMARY KEY,
    revision bigint NOT NULL
);

CREATE OR REPLACE FUNCTION pms_kb_bump_search_revision()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP = 'TRUNCATE' THEN
        EXECUTE format('UPDATE %I.pms_kb_search_revision SET revision = revision + 1', TG_TABLE_SCHEMA);
        RETURN NULL;
    END IF;

    EXECUTE format(
        'INSERT INTO %I.pms_kb_search_revision AS r (tenant_id, revision) '
        'SELECT id, 1 FROM (SELECT DISTINCT unnest($1) AS id) tenants WHERE id IS NOT NULL ORDER BY id '
        'ON CONFLICT (tenant_id) DO UPDATE SET revision = r.revision + 1', TG_TABLE_SCHEMA)
        USING CASE TG_OP
            WHEN 'INSERT' THEN ARRAY[NEW.tenant_id]
            WHEN 'DELETE' THEN ARRAY[OLD.tenant_id]
            ELSE ARRAY[OLD.tenant_id, NEW.tenant_id]
        END;
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS pms_kb_search_revision_row ON pms_knowledge_document;
CREATE TRIGGER pms_kb_search_revision_row
    AFTER INSERT OR DELETE OR UPDATE OF tenant_id, library_id, permission_id, status, deleted, search_text
    ON pms_knowledge_document FOR EACH ROW EXECUTE FUNCTION pms_kb_bump_search_revision();

DROP TRIGGER IF EXISTS pms_kb_search_revision_truncate ON pms_knowledge_document;
CREATE TRIGGER pms_kb_search_revision_truncate
    AFTER TRUNCATE ON pms_knowledge_document FOR EACH STATEMENT EXECUTE FUNCTION pms_kb_bump_search_revision();

COMMIT;
