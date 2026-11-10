package cn.iocoder.yudao.module.pms.service.kb.content;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PmsKnowledgeSearchSyncQueue {

    private final JdbcTemplate jdbc;

    boolean tryLock() {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT pg_try_advisory_xact_lock(1053130025::bigint)", Boolean.class));
    }

    List<Task> due(boolean tenantEnabled) {
        return jdbc.query("""
                SELECT tenant_id, document_id, version, force_rebuild
                FROM pms_kb_search_sync
                WHERE next_attempt_at <= now() AND (? OR document_id <> 0 OR tenant_id = 0)
                ORDER BY next_attempt_at, document_id, tenant_id
                LIMIT 100
                """, (rs, row) -> new Task(rs.getLong("tenant_id"), rs.getLong("document_id"),
                rs.getLong("version"), rs.getBoolean("force_rebuild")), tenantEnabled);
    }

    long maintenanceVersion(Long tenantId) {
        return jdbc.queryForObject("""
                SELECT COALESCE(MAX(version), 0) FROM pms_kb_search_sync
                WHERE tenant_id = ? AND document_id = 0
                """, Long.class, tenantId == null ? 0L : tenantId);
    }

    long replayLimit(Long tenantId) {
        return jdbc.queryForObject("""
                SELECT COALESCE(MAX(document_id), 0) FROM pms_kb_search_sync
                WHERE (?::bigint IS NULL OR tenant_id = ?)
                """, Long.class, tenantId, tenantId);
    }

    List<Long> replayIds(Long tenantId, long afterId, long limitId) {
        return jdbc.queryForList("""
                SELECT DISTINCT document_id FROM pms_kb_search_sync
                WHERE document_id > ? AND document_id <= ? AND (?::bigint IS NULL OR tenant_id = ?)
                ORDER BY document_id
                LIMIT 100
                """, Long.class, afterId, limitId, tenantId, tenantId);
    }

    void complete(Task task) {
        if (task.documentId() == 0) {
            jdbc.update("""
                    UPDATE pms_kb_search_sync
                    SET force_rebuild = false, failures = 0, next_attempt_at = clock_timestamp() + interval '1 minute'
                    WHERE tenant_id = ? AND document_id = 0 AND version = ?
                    """, task.tenantId(), task.version());
        } else {
            jdbc.update("""
                    DELETE FROM pms_kb_search_sync WHERE tenant_id = ? AND document_id = ? AND version = ?
                    """, task.tenantId(), task.documentId(), task.version());
        }
    }

    void retry(Task task) {
        jdbc.update("""
                UPDATE pms_kb_search_sync
                SET next_attempt_at = clock_timestamp() + LEAST(300, 5 * (1 << LEAST(failures, 6))) * interval '1 second',
                    failures = LEAST(failures + 1, 7)
                WHERE tenant_id = ? AND document_id = ? AND version = ?
                """, task.tenantId(), task.documentId(), task.version());
    }

    record Task(long tenantId, long documentId, long version, boolean forceRebuild) {
    }
}
