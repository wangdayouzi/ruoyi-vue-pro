package cn.iocoder.yudao.module.reagent.service.securedisk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SecureDiskFormModels.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import cn.iocoder.yudao.module.reagent.dal.pg.SecureDiskFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Service
@RequiredArgsConstructor
public class SecureDiskFormService {
    private final SecureDiskFormRepository repository;
    private final SecureDiskPdfRenderer pdfRenderer;

    public List<Map<String, Object>> rules() {
        return withPermissions(repository.findEnabledRules(tenant()));
    }

    public Settings settings() {
        return new Settings(withPermissions(repository.findRules(tenant())), repository.findCounters(tenant()));
    }

    public boolean saveRule(Rule rule) {
        if (!validPermissions(rule.permissions())) {
            throw invalid("请选择有效且不重复的目录权限");
        }
        if (rule.enabled() && (!rule.path().startsWith("\\\\") || rule.path().contains("......"))) {
            throw invalid("启用规则前请填写完整的 UNC 路径");
        }
        String permissions = JsonUtils.toJsonString(rule.permissions().stream().sorted().toList());
        try {
            if (rule.id() == null) {
                repository.insertRule(tenant(), rule.category(), rule.instrument(), rule.path(), permissions, rule.enabled());
            } else if (repository.updateRule(rule.category(), rule.instrument(), rule.path(), permissions, rule.enabled(), tenant(), rule.id()) != 1) {
                throw invalid("规则已删除或不存在");
            }
        } catch (DuplicateKeyException exception) {
            throw invalid("该分类、仪器和目录下已有一条规则，请编辑已有规则");
        }
        return true;
    }

    public boolean deleteRule(long id) {
        if (repository.deleteRule(tenant(), id) != 1) {
            throw invalid("规则已删除或不存在");
        }
        return true;
    }

    private static boolean validPermissions(List<String> permissions) {
        return !permissions.isEmpty()
                && new HashSet<>(permissions).size() == permissions.size()
                && permissions.stream().allMatch(code -> code != null && code.matches("R[1-5]|W[1-6]"));
    }

    private static List<Map<String, Object>> withPermissions(List<Map<String, Object>> rows) {
        rows.forEach(row -> row.put("permissions", readPermissions(row.get("permissions"))));
        return rows;
    }

    private static List<String> readPermissions(Object json) {
        return JsonUtils.parseArray(json.toString(), String.class);
    }

    public Map<String, Object> identity() {
        var applicant = currentApplicant();
        applicant.put("effective_nas_account", effectiveNasAccount(applicant));
        return applicant;
    }

    private Map<String, Object> currentApplicant() {
        return people(getLoginUserId(), "", false).stream().findFirst()
                .orElseThrow(() -> invalid("当前登录账号不可用，请重新登录"));
    }

    private static String effectiveNasAccount(Map<String, Object> applicant) {
        String boundAccount = Objects.toString(applicant.get("nas_account"), "").strip();
        return boundAccount.isEmpty() ? Objects.toString(applicant.get("username"), "") : boundAccount;
    }

    public List<Map<String, Object>> findAccountPeople(String query) {
        return people(null, query.strip(), false);
    }

    private List<Map<String, Object>> people(Long userId, String query, boolean boundOnly) {
        return repository.findPeople(tenant(), TenantContextHolder.getTenantId(), userId, boundOnly, query);
    }

    public boolean bindAccount(Account input) {
        if (people(input.userId(), "", false).isEmpty()) {
            throw invalid("人员不存在或已禁用");
        }
        repository.saveAccount(tenant(), input.userId(), input.nasAccount(), input.domainAccount());
        return true;
    }

    public boolean upload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 2_000_000) {
            throw invalid("请选择不超过 2 MB 的 DOCX 模板");
        }
        try {
            byte[] bytes = file.getBytes();
            pdfRenderer.render(bytes, "SH25BAS0664", "sample", "MSD-002", "sample path", List.of("R1"));
            repository.saveTemplate(tenant(), bytes);
            return true;
        } catch (Exception ex) {
            throw invalid("模板无法生成 PDF，请检查 上海原版 DOCX 及必需的占位符");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public IssuedForm submit(Application input) throws Exception {
        long tenant = tenant();
        repository.lock(
                "secure-disk:" + tenant + ":" + getLoginUserId() + ":" + input.requestId());
        var previous = repository.findSubmission(tenant, getLoginUserId(), input.requestId());
        if (!previous.isEmpty()) {
            return new IssuedForm(((Number) previous.get(0).get("id")).longValue(), (String) previous.get(0).get("form_no"));
        }
        Map<String, Object> original = null;
        if (input.changeId() != null) {
            original = application(input.changeId(), true);
            if (original.get("form_no") != null || original.get("voided_at") != null) {
                throw invalid("只能修改待确认且有效的申请");
            }
        }
        var applicant = currentApplicant();
        var participantIds = new LinkedHashSet<>(input.participantIds());
        List<String> participantNames = new ArrayList<>();
        Set<String> nasAccounts = new LinkedHashSet<>();
        for (Long participantId : participantIds) {
            var matches = people(participantId, "", false);
            if (matches.isEmpty()) {
                throw invalid("所选预约人已停用或不存在，请重新选择");
            }
            var participant = matches.get(0);
            participantNames.add(personName(participant));
            nasAccounts.add(effectiveNasAccount(participant));
        }
        String accounts = String.join(",", nasAccounts);
        if (accounts.length() > 200 || nasAccounts.contains("")) {
            throw invalid("预约人的账号为空或合计长度超过表单限制，请检查人员");
        }
        Set<Long> ids = new LinkedHashSet<>(input.ruleIds());
        if (ids.size() != input.ruleIds().size()) {
            throw invalid("目录不能重复");
        }
        String project = Objects.toString(input.project(), "").strip();
        boolean projectMissing = project.isEmpty() || "NA".equalsIgnoreCase(project);
        List<String> paths = new ArrayList<>();
        Set<String> assignedPermissions = new LinkedHashSet<>();
        String category = null;
        String instrument = null;
        for (long id : ids) {
            var rules = repository.lockEnabledRule(tenant, id);
            if (rules.isEmpty()) {
                throw invalid("规则已停用，请重新选择");
            }
            var rule = rules.get(0);
            if (category == null) {
                category = (String) rule.get("category");
                instrument = (String) rule.get("instrument");
            } else if (!category.equals(rule.get("category")) || !instrument.equals(rule.get("instrument"))) {
                throw invalid("同一申请只可选择同一分类下的同一种仪器或用途");
            }
            List<String> allowed = readPermissions(rule.get("permissions"));
            if (!validPermissions(allowed)) {
                throw invalid("目录权限配置无效，请联系管理员");
            }
            if (paths.isEmpty()) {
                assignedPermissions.addAll(allowed);
            } else if (!assignedPermissions.equals(new HashSet<>(allowed))) {
                throw invalid("所选目录的权限不同，请分开申请");
            }
            String directoryRule = (String) rule.get("path");
            if (directoryRule.contains("{project}") && projectMissing) {
                throw invalid("所选目录需要实际项目号，请填写后再生成");
            }
            paths.add(directoryRule.replace("{project}", project));
        }
        String path = String.join("\n", paths);
        String permissions = JsonUtils.toJsonString(assignedPermissions);
        String formProject = projectMissing ? "NA" : project;
        String peopleIds = JsonUtils.toJsonString(participantIds);
        String ruleIds = JsonUtils.toJsonString(input.ruleIds());
        String names = JsonUtils.toJsonString(participantNames);
        if (original != null && original.get("submitted_at") == null) {
            repository.updateDraft(tenant, input.changeId(), input.requestId(), formProject, accounts,
                    category, instrument, path, permissions, peopleIds, ruleIds, names, input.submit());
            return new IssuedForm(input.changeId(), null);
        }
        boolean submitted = input.submit() || original != null;
        Long id = repository.insertApplication(tenant, getLoginUserId(), input.requestId(), formProject,
                accounts, category, instrument, path, permissions, peopleIds, ruleIds,
                personName(applicant), names, submitted);
        if (original != null) {
            repository.replaceApplication(getLoginUserId(), id, tenant, input.changeId());
        }
        return new IssuedForm(id, null);
    }

    private String personName(Map<String, Object> person) {
        String name = Objects.toString(person.get("nickname"), "").strip();
        return name.isEmpty() ? Objects.toString(person.get("username"), "") : name;
    }

    @Transactional
    public boolean submitDraft(long id) {
        var row = application(id, true);
        if (row.get("voided_at") != null) {
            throw invalid("作废申请不能提交");
        }
        repository.submitDraft(tenant(), id);
        return true;
    }

    private byte[] renderPdf(long tenant, String project, String accounts, String instrument,
                             String path, List<String> permissions) throws Exception {
        var templates = repository.findTemplate(tenant);
        if (!templates.isEmpty()) {
            return pdfRenderer.render(templates.get(0), project, accounts, instrument, path, permissions);
        }
        try (InputStream original = new ClassPathResource("templates/secure-disk-shanghai.docx").getInputStream()) {
            return pdfRenderer.render(original.readAllBytes(), project, accounts, instrument, path, permissions);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean generatePdf(long id) throws Exception {
        long tenant = tenant();
        var rows = repository.lockPdfSource(tenant, getLoginUserId(), id);
        if (rows.isEmpty()) {
            throw invalid("申请不存在或已作废");
        }
        var row = rows.get(0);
        if (!Boolean.TRUE.equals(row.get("has_pdf"))) {
            repository.savePdf(tenant, id, renderPdf(tenant, (String) row.get("project"), (String) row.get("accounts"),
                    (String) row.get("instrument"), (String) row.get("path"), readPermissions(row.get("permissions"))));
        }
        return true;
    }

    public ConfirmationPreview confirmationPreview() {
        int year = issueYear(repository.currentTime());
        return readCounter(year, repository.findCounter(tenant(), year));
    }

    @Transactional
    public IssuedForm confirm(ConfirmationRequest input) {
        long tenant = tenant();
        var row = application(input.id(), false);
        if (row.get("voided_at") != null) {
            throw invalid("作废申请不能确认");
        }
        if (row.get("form_no") != null) {
            return new IssuedForm(input.id(), (String) row.get("form_no"));
        }
        if (row.get("submitted_at") == null) {
            throw invalid("请先提交申请");
        }
        repository.lock("secure-disk-issue:" + tenant);
        OffsetDateTime issuedAt = repository.currentTime();
        int year = issueYear(issuedAt);
        ConfirmationPreview counter = readCounter(year, repository.lockCounter(tenant, year));
        if (input.year() != year || input.expectedSequence() != counter.nextSequence()) {
            throw invalid("当前流水已更新，请重新打开确认窗口");
        }
        if (input.sequence() < counter.nextSequence()) {
            throw invalid("流水号不能小于当前待用号");
        }
        String number = String.format(Locale.ROOT, "SD-A-V%d-%d-%03d", counter.version(), year, input.sequence());
        repository.issueApplication(number, year, input.sequence(), counter.nextSequence(), issuedAt,
                getLoginUserId(), personName(currentApplicant()), tenant, input.id());
        repository.advanceCounter(tenant, year, input.sequence() + 1);
        return new IssuedForm(input.id(), number);
    }

    private int issueYear(OffsetDateTime time) {
        return time.atZoneSameInstant(ZoneId.of("Asia/Shanghai")).getYear();
    }

    private ConfirmationPreview readCounter(int year, List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            throw invalid("本年度流水尚未通过部署 SQL 初始化");
        }
        var row = rows.get(0);
        return new ConfirmationPreview(year, ((Number) row.get("version")).intValue(),
                ((Number) row.get("next_no")).intValue());
    }

    @Transactional
    public boolean voidOwn(VoidInput input) {
        var row = application(input.id(), true);
        if (row.get("form_no") != null) {
            throw invalid("已确认的申请由确认人员作废");
        }
        return voidApplication(input, row);
    }

    @Transactional
    public boolean voidIssued(VoidInput input) {
        return voidApplication(input, application(input.id(), false));
    }

    private boolean voidApplication(VoidInput input, Map<String, Object> row) {
        if (row.get("voided_at") == null) {
            repository.voidApplication(getLoginUserId(), input.reason(), tenant(), input.id());
        }
        return true;
    }

    private Map<String, Object> application(long id, boolean own) {
        var rows = repository.lockApplication(tenant(), id, own, getLoginUserId());
        if (rows.isEmpty()) {
            throw invalid("申请不存在");
        }
        return rows.get(0);
    }

    public PageResult<Map<String, Object>> pageApplications(ApplicationPageReq request, boolean own) {
        Long applicant = own ? getLoginUserId() : null;
        long tenant = tenant();
        String keyword = Objects.toString(request.getKeyword(), "").strip();
        String status = Objects.toString(request.getStatus(), "ALL");
        long total = repository.countApplications(tenant, applicant, keyword, status);
        if (total == 0) {
            return PageResult.empty();
        }
        int limit = request.getPageSize();
        long offset = (request.getPageNo() - 1L) * limit;
        var rows = repository.pageApplications(tenant, applicant, keyword, status, limit, offset);
        return new PageResult<>(rows, total);
    }

    public IssueExport exportIssues(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw invalid("结束日期不能早于开始日期");
        }
        var rows = repository.findIssues(tenant(), startDate.toString(), endDate.toString());
        List<List<String>> head = List.of("申请单ID", "表单号", "建议流水", "项目号", "确认时间", "确认人ID",
                "路径", "权限", "NAS账号", "状态", "作废原因").stream().map(List::of).toList();
        List<List<Object>> data = rows.stream().map(row -> List.of(row.get("id"), row.get("form_no"),
                Objects.toString(row.get("suggested_sequence"), ""), row.get("project"),
                row.get("issued_time"), Objects.toString(row.get("issued_by"), ""), row.get("path"),
                permissionDisplay(readPermissions(row.get("permissions"))), row.get("accounts"),
                row.get("status"), row.get("reason"))).toList();
        return new IssueExport(head, data);
    }

    private static String permissionDisplay(List<String> codes) {
        return String.join("、", codes.stream().map(code ->
                (code.startsWith("R") ? "读取" : "写入") + code.substring(1)).toList());
    }

    public PdfFile pdf(long id) {
        var rows = repository.findPdf(tenant(), getLoginUserId(), id);
        if (rows.isEmpty()) {
            throw invalid("申请不存在");
        }
        var row = rows.get(0);
        if (row.get("pdf") == null) {
            throw invalid("该申请尚未生成 PDF");
        }
        String filename = Objects.toString(row.get("form_no"), "secure-disk-" + id) + ".pdf";
        return new PdfFile(filename, (byte[]) row.get("pdf"));
    }

    private static long tenant() {
        return Objects.requireNonNullElse(TenantContextHolder.getTenantId(), 1L);
    }

    private static ServiceException invalid(String message) {
        return new ServiceException(400, message);
    }

}
