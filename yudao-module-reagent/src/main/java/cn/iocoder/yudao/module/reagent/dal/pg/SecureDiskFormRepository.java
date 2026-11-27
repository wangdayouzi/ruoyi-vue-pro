package cn.iocoder.yudao.module.reagent.dal.pg;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SecureDiskFormRepository {

    private static final String APPLICATION_FILTER = """
            FROM form_secure_application
            WHERE tenant_id = ?
              AND (CAST(? AS bigint) IS NULL AND submitted_at IS NOT NULL OR applicant_id = ?)
              AND (strpos(lower(project), lower(?)) > 0
                OR strpos(lower(coalesce(form_no, '')), lower(?)) > 0
                OR strpos(lower(accounts), lower(?)) > 0
                OR id::text = ?)
              AND CASE ?
                    WHEN 'DRAFT' THEN submitted_at IS NULL AND voided_at IS NULL
                    WHEN 'PENDING' THEN submitted_at IS NOT NULL AND form_no IS NULL AND voided_at IS NULL
                    WHEN 'ISSUED' THEN form_no IS NOT NULL AND voided_at IS NULL
                    WHEN 'VOIDED' THEN voided_at IS NOT NULL
                    ELSE true
                  END
            """;

    private final JdbcTemplate jdbc;

    public void lock(String key) {
        jdbc.queryForList("SELECT pg_advisory_xact_lock(hashtextextended(?,0))", key);
    }

    public OffsetDateTime currentTime() {
        return jdbc.queryForObject("SELECT clock_timestamp()",
                (result, rowIndex) -> result.getObject(1, OffsetDateTime.class));
    }

    public void advanceCounter(long tenant, int year, int nextSequence) {
        jdbc.update("UPDATE form_secure_counter SET next_no=? WHERE tenant_id=? AND issue_year=?", nextSequence, tenant, year);
    }

    public List<byte[]> findTemplate(long tenant) {
        return jdbc.query("SELECT content FROM form_secure_template WHERE tenant_id=?",
                (result, rowIndex) -> result.getBytes(1), tenant);
    }

    public List<Map<String, Object>> findEnabledRules(long tenant) {
        return jdbc.queryForList("""
                SELECT id, category, instrument, path, permissions FROM form_secure_rule
                WHERE tenant_id=? AND enabled AND NOT deleted ORDER BY category,instrument,id
                """, tenant);
    }

    public List<Map<String, Object>> findRules(long tenant) {
        return jdbc.queryForList("""
                SELECT id,category,instrument,path,permissions,enabled FROM form_secure_rule
                WHERE tenant_id=? AND NOT deleted ORDER BY category,instrument,id
                """, tenant);
    }

    public List<Map<String, Object>> findCounters(long tenant) {
        return jdbc.queryForList("""
                SELECT issue_year,next_no,version,initialized_by,initialized_at,source
                FROM form_secure_counter WHERE tenant_id=? ORDER BY issue_year DESC
                """, tenant);
    }

    public int insertRule(long tenant, String category, String instrument, String path, String permissions, boolean enabled) {
        return jdbc.update("""
                INSERT INTO form_secure_rule(tenant_id,category,instrument,path,permissions,enabled)
                VALUES (?,?,?,?,CAST(? AS jsonb),?)
                """, tenant, category, instrument, path, permissions, enabled);
    }

    public int updateRule(String category, String instrument, String path, String permissions, boolean enabled, long tenant, long id) {
        return jdbc.update("""
                UPDATE form_secure_rule SET category=?,instrument=?,path=?,permissions=CAST(? AS jsonb),enabled=?
                WHERE tenant_id=? AND id=? AND NOT deleted
                """, category, instrument, path, permissions, enabled, tenant, id);
    }

    public int deleteRule(long tenant, long id) {
        return jdbc.update("""
                UPDATE form_secure_rule SET deleted=true,enabled=false
                WHERE tenant_id=? AND id=? AND NOT deleted
                """, tenant, id);
    }

    public List<Map<String, Object>> findPeople(long tenant, Long dataTenant, Long userId, boolean boundOnly, String query) {
        return jdbc.queryForList("""
                SELECT u.id,u.nickname,u.username,a.nas_account,a.domain_account
                FROM system_users u LEFT JOIN form_secure_account a ON a.tenant_id=? AND a.user_id=u.id
                WHERE (CAST(? AS bigint) IS NULL OR u.tenant_id=?) AND u.deleted=0 AND u.status=0
                AND (CAST(? AS bigint) IS NULL OR u.id=?)
                AND (NOT ? OR a.nas_account IS NOT NULL)
                AND (strpos(lower(u.nickname),lower(?))>0 OR strpos(lower(u.username),lower(?))>0
                OR strpos(lower(coalesce(a.nas_account,'')),lower(?))>0)
                ORDER BY u.nickname,u.id LIMIT 40
                """, tenant, dataTenant, dataTenant, userId, userId, boundOnly, query, query, query);
    }

    public int saveAccount(long tenant, long userId, String nasAccount, String domainAccount) {
        return jdbc.update("""
                INSERT INTO form_secure_account(tenant_id,user_id,nas_account,domain_account) VALUES (?,?,?,?)
                ON CONFLICT(tenant_id,user_id) DO UPDATE
                SET nas_account=EXCLUDED.nas_account,domain_account=EXCLUDED.domain_account
                """, tenant, userId, nasAccount, domainAccount);
    }

    public int saveTemplate(long tenant, byte[] content) {
        return jdbc.update("""
                INSERT INTO form_secure_template VALUES (?,?)
                ON CONFLICT (tenant_id) DO UPDATE SET content=EXCLUDED.content
                """, tenant, content);
    }

    public List<Map<String, Object>> findSubmission(long tenant, long applicant, UUID requestId) {
        return jdbc.queryForList("""
                SELECT id,form_no FROM form_secure_application WHERE tenant_id=? AND applicant_id=? AND request_id=?
                """, tenant, applicant, requestId);
    }

    public List<Map<String, Object>> lockEnabledRule(long tenant, long id) {
        return jdbc.queryForList("""
                SELECT category,instrument,path,permissions FROM form_secure_rule
                WHERE tenant_id=? AND id=? AND enabled AND NOT deleted FOR SHARE
                """, tenant, id);
    }

    public Long insertApplication(long tenant, long applicant, UUID requestId, String project, String accounts,
                                  String category, String instrument, String path, String permissions,
                                  String peopleIds, String ruleIds, String applicantName, String participantNames,
                                  boolean submit) {
        return jdbc.queryForObject("""
                INSERT INTO form_secure_application(tenant_id,applicant_id,request_id,
                    project,accounts,category,instrument,path,permissions,people_ids,rule_ids,
                    applicant_name,participant_names,submitted_at)
                VALUES (?,?,?,?,?,?,?,?,CAST(? AS jsonb),CAST(? AS jsonb),CAST(? AS jsonb),?,CAST(? AS jsonb),
                    CASE WHEN ? THEN now() ELSE NULL END) RETURNING id
                """, Long.class, tenant, applicant, requestId, project, accounts, category, instrument, path,
                permissions, peopleIds, ruleIds, applicantName, participantNames, submit);
    }

    public void updateDraft(long tenant, long id, UUID requestId, String project, String accounts, String category,
                            String instrument, String path, String permissions, String peopleIds, String ruleIds,
                            String participantNames, boolean submit) {
        jdbc.update("""
                UPDATE form_secure_application SET request_id=?,project=?,accounts=?,category=?,instrument=?,
                    path=?,permissions=CAST(? AS jsonb),people_ids=CAST(? AS jsonb),rule_ids=CAST(? AS jsonb),
                    participant_names=CAST(? AS jsonb),pdf=NULL,submitted_at=CASE WHEN ? THEN now() ELSE NULL END
                WHERE tenant_id=? AND id=?
                """, requestId, project, accounts, category, instrument, path, permissions, peopleIds, ruleIds,
                participantNames, submit, tenant, id);
    }

    public void submitDraft(long tenant, long id) {
        jdbc.update("""
                UPDATE form_secure_application SET submitted_at=now()
                WHERE tenant_id=? AND id=? AND submitted_at IS NULL AND voided_at IS NULL
                """, tenant, id);
    }

    public int replaceApplication(long operator, long replacementId, long tenant, long id) {
        return jdbc.update("""
                UPDATE form_secure_application SET voided_at=now(),voided_by=?,void_reason='修改后替换',replaced_by=?
                WHERE tenant_id=? AND id=?
                """, operator, replacementId, tenant, id);
    }

    public List<Map<String, Object>> findCounter(long tenant, int year) {
        return jdbc.queryForList("""
                SELECT next_no,version FROM form_secure_counter WHERE tenant_id=? AND issue_year=?
                """, tenant, year);
    }

    public List<Map<String, Object>> lockCounter(long tenant, int year) {
        return jdbc.queryForList("""
                SELECT next_no,version FROM form_secure_counter WHERE tenant_id=? AND issue_year=? FOR UPDATE
                """, tenant, year);
    }

    public int issueApplication(String number, int year, int sequence, int suggestedSequence, OffsetDateTime issuedAt, long operator, String approverName, long tenant, long id) {
        return jdbc.update("""
                UPDATE form_secure_application SET form_no=?,issue_year=?,sequence_no=?,suggested_sequence=?,issued_at=?,issued_by=?,approver_name=?
                WHERE tenant_id=? AND id=?
                """, number, year, sequence, suggestedSequence, issuedAt, operator, approverName, tenant, id);
    }

    public int voidApplication(long operator, String reason, long tenant, long id) {
        return jdbc.update("""
                UPDATE form_secure_application SET voided_at=now(),voided_by=?,void_reason=?
                WHERE tenant_id=? AND id=?
                """, operator, reason, tenant, id);
    }

    public List<Map<String, Object>> lockApplication(long tenant, long id, boolean own, long operator) {
        return jdbc.queryForList("""
                SELECT form_no,voided_at,submitted_at FROM form_secure_application WHERE tenant_id=? AND id=?
                AND (NOT ? OR applicant_id=?) FOR UPDATE
                """, tenant, id, own, operator);
    }

    public List<Map<String, Object>> pageApplications(long tenant, Long applicant, String keyword, String status,
                                                       int limit, long offset) {
        return jdbc.queryForList("""
                SELECT id, form_no, project, accounts, category, instrument, path, created_at,
                       issued_at, voided_at, void_reason, replaced_by, pdf IS NOT NULL AS has_pdf, submitted_at,
                       coalesce(applicant_name,(SELECT nickname FROM system_users WHERE id=applicant_id)) AS applicant_name,
                       participant_names::text AS participant_names,
                       coalesce(approver_name,(SELECT nickname FROM system_users WHERE id=issued_by)) AS approver_name,
                       people_ids::text AS people_ids, rule_ids::text AS rule_ids
                """ + APPLICATION_FILTER + "ORDER BY id DESC LIMIT ? OFFSET ?",
                tenant, applicant, applicant, keyword, keyword, keyword, keyword, status, limit, offset);
    }

    public long countApplications(long tenant, Long applicant, String keyword, String status) {
        return jdbc.queryForObject("SELECT count(*) " + APPLICATION_FILTER, Long.class,
                tenant, applicant, applicant, keyword, keyword, keyword, keyword, status);
    }

    public List<Map<String, Object>> findIssues(long tenant, String startDate, String endDate) {
        return jdbc.queryForList("""
                SELECT id,form_no,sequence_no,suggested_sequence,project,accounts,path,permissions::text AS permissions,issued_by,
                to_char(issued_at AT TIME ZONE 'Asia/Shanghai','YYYY-MM-DD HH24:MI:SS') AS issued_time,
                CASE WHEN voided_at IS NULL THEN '已确认' ELSE '已作废' END AS status,
                coalesce(void_reason,'') AS reason
                FROM form_secure_application
                WHERE tenant_id=? AND issued_at >= (CAST(? AS date)::timestamp AT TIME ZONE 'Asia/Shanghai')
                AND issued_at < ((CAST(? AS date) + interval '1 day') AT TIME ZONE 'Asia/Shanghai')
                ORDER BY issue_year,sequence_no
                """, tenant, startDate, endDate);
    }

    public List<Map<String, Object>> lockPdfSource(long tenant, long applicant, long id) {
        return jdbc.queryForList("""
                SELECT pdf IS NOT NULL AS has_pdf,project,accounts,instrument,path,permissions
                FROM form_secure_application WHERE tenant_id=? AND applicant_id=? AND id=? AND voided_at IS NULL
                FOR UPDATE
                """, tenant, applicant, id);
    }

    public void savePdf(long tenant, long id, byte[] pdf) {
        jdbc.update("UPDATE form_secure_application SET pdf=? WHERE tenant_id=? AND id=?", pdf, tenant, id);
    }

    public List<Map<String, Object>> findPdf(long tenant, long applicant, long id) {
        return jdbc.queryForList("""
                SELECT form_no,pdf FROM form_secure_application WHERE tenant_id=? AND applicant_id=? AND id=? AND voided_at IS NULL
                """, tenant, applicant, id);
    }
}
