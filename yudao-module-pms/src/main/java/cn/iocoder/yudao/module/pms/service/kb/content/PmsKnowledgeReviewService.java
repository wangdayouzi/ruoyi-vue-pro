package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeFolderDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRecordDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRuleDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeReviewRecordMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeReviewRuleMapper;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
public class PmsKnowledgeReviewService {
    private static final Set<String> RESULTS = Set.of("valid", "pending", "replaced", "abolished");
    @Resource private PmsKnowledgeReviewRuleMapper ruleMapper;
    @Resource private PmsKnowledgeReviewRecordMapper recordMapper;
    @Resource private PmsKnowledgeDocumentService documentService;
    @Resource private PmsKnowledgeFolderService folderService;
    @Resource private PmsKnowledgeLibraryMemberService memberService;
    @Resource private PmsKnowledgeContentPermissionService permissionService;

    public List<PmsKnowledgeReviewRuleDO> listRules(Long libraryId, Long userId) {
        memberService.validateLibraryReadable(libraryId, userId);
        return ruleMapper.selectList(new LambdaQueryWrapper<PmsKnowledgeReviewRuleDO>()
                .eq(PmsKnowledgeReviewRuleDO::getLibraryId, libraryId).orderByAsc(PmsKnowledgeReviewRuleDO::getFolderId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveRule(PmsKnowledgeReviewRuleDO input, Long userId) {
        if (input.getLibraryId() == null) throw invalidParamException("请选择知识库");
        memberService.validateLibraryAdmin(input.getLibraryId(), userId);
        if (input.getFolderId() == null) input.setFolderId(0L);
        if (input.getFolderId() != 0L) {
            PmsKnowledgeFolderDO folder = folderService.getFolder(input.getFolderId(), userId);
            if (!input.getLibraryId().equals(folder.getLibraryId()) || folder.getParentId() == null || folder.getParentId() != 0L)
                throw invalidParamException("审阅规则只能配置在一级文件夹");
        }
        if (input.getIntervalMonths() == null || input.getIntervalMonths() < 1 || input.getIntervalMonths() > 120)
            throw invalidParamException("审阅周期须为 1 至 120 个月");
        if (input.getRemindDays() == null || input.getRemindDays() < 0 || input.getRemindDays() > 365)
            throw invalidParamException("提前提醒天数须为 0 至 365 天");
        if (input.getEnabled() == null) input.setEnabled(true);
        PmsKnowledgeReviewRuleDO old = ruleMapper.selectOne(new LambdaQueryWrapper<PmsKnowledgeReviewRuleDO>()
                .eq(PmsKnowledgeReviewRuleDO::getLibraryId, input.getLibraryId())
                .eq(PmsKnowledgeReviewRuleDO::getFolderId, input.getFolderId()));
        if (old == null) {
            input.setId(null);
            ruleMapper.insert(input);
        } else {
            input.setId(old.getId());
            ruleMapper.updateById(input);
        }
        return input.getId();
    }

    public PmsKnowledgeReviewRuleDO effectiveRule(PmsKnowledgeDocumentDO document, Long userId) {
        List<PmsKnowledgeReviewRuleDO> rules = listRules(document.getLibraryId(), userId);
        Long folderId = document.getFolderId();
        Long parentId = document.getParentId();
        Set<Long> seenDocuments = new java.util.HashSet<>();
        while ((folderId == null || folderId == 0L) && parentId != null && parentId != 0L && seenDocuments.add(parentId)) {
            PmsKnowledgeDocumentDO parent = documentService.getDocument(parentId, userId);
            if (!document.getLibraryId().equals(parent.getLibraryId())) break;
            folderId = parent.getFolderId();
            parentId = parent.getParentId();
        }
        if (folderId != null && folderId != 0L) {
            Set<Long> seen = new java.util.HashSet<>();
            PmsKnowledgeFolderDO folder = folderService.getFolder(folderId);
            while (folder != null && seen.add(folder.getId()) && folder.getParentId() != null && folder.getParentId() != 0L)
                folder = folderService.getFolder(folder.getParentId());
            if (folder != null && (folder.getParentId() == null || folder.getParentId() == 0L)) {
                Long rootId = folder.getId();
                PmsKnowledgeReviewRuleDO override = rules.stream().filter(r -> rootId.equals(r.getFolderId())).findFirst().orElse(null);
                if (override != null) return Boolean.TRUE.equals(override.getEnabled()) ? override : null;
            }
        }
        PmsKnowledgeReviewRuleDO base = rules.stream().filter(r -> r.getFolderId() == 0L).findFirst().orElse(null);
        return base != null && Boolean.TRUE.equals(base.getEnabled()) ? base : null;
    }

    public List<PmsKnowledgeReviewRecordDO> history(Long documentId, Long userId) {
        documentService.getDocument(documentId, userId);
        return recordMapper.selectList(new LambdaQueryWrapper<PmsKnowledgeReviewRecordDO>()
                .eq(PmsKnowledgeReviewRecordDO::getDocumentId, documentId).orderByDesc(PmsKnowledgeReviewRecordDO::getId));
    }

    public Map<String, Object> summary(Long documentId, Long userId) {
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, userId);
        PmsKnowledgeReviewRuleDO rule = effectiveRule(document, userId);
        List<PmsKnowledgeReviewRecordDO> records = history(documentId, userId);
        PmsKnowledgeReviewRecordDO latest = records.isEmpty() ? null : records.get(0);
        String result = latest == null ? "unreviewed" : latest.getResult();
        if (rule == null || "abolished".equals(result) || "replaced".equals(result))
            return Map.of("result", result, "dueDate", "", "overdue", false);
        LocalDate basis = reviewBasis(document, records);
        LocalDate dueDate = basis.plusMonths(rule.getIntervalMonths());
        return Map.of("result", result, "dueDate", dueDate.toString(), "overdue", dueDate.isBefore(LocalDate.now()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long submit(Long documentId, String result, String remark, Long userId) {
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, userId);
        permissionService.validateContentPermissionWritable(document.getPermissionId(), document.getLibraryId(), userId);
        if (result == null || !RESULTS.contains(result)) throw invalidParamException("审阅结论无效");
        if (remark != null && remark.length() > 2000) throw invalidParamException("审阅备注过长");
        if (("replaced".equals(result) || "abolished".equals(result)) && (remark == null || remark.isBlank()))
            throw invalidParamException("替代或作废需要填写原因");
        PmsKnowledgeReviewRecordDO record = new PmsKnowledgeReviewRecordDO()
                .setDocumentId(documentId).setLibraryId(document.getLibraryId()).setResult(result)
                .setReviewDate(LocalDate.now()).setRemark(remark).setReviewerId(userId);
        recordMapper.insert(record);
        return record.getId();
    }

    public List<Map<String, Object>> dueList(Long libraryId, Long userId) {
        memberService.validateLibraryReadable(libraryId, userId);
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (PmsKnowledgeDocumentDO document : documentService.getDocumentList(libraryId, userId)) {
            if (!PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(document.getStatus())) continue;
            PmsKnowledgeReviewRuleDO rule = effectiveRule(document, userId);
            if (rule == null) continue;
            List<PmsKnowledgeReviewRecordDO> history = history(document.getId(), userId);
            PmsKnowledgeReviewRecordDO latest = history.isEmpty() ? null : history.get(0);
            if (latest != null && ("abolished".equals(latest.getResult()) || "replaced".equals(latest.getResult()))) continue;
            LocalDate basis = reviewBasis(document, history);
            LocalDate dueDate = basis.plusMonths(rule.getIntervalMonths());
            if (!"pending".equals(latest == null ? null : latest.getResult())
                    && dueDate.isAfter(today.plusDays(rule.getRemindDays()))) continue;
            result.add(Map.of("documentId", document.getId(), "title", document.getTitle(),
                    "documentCode", document.getDocumentCode() == null ? "" : document.getDocumentCode(),
                    "dueDate", dueDate.toString(), "overdue", dueDate.isBefore(today),
                    "result", latest == null ? "unreviewed" : latest.getResult()));
        }
        result.sort(Comparator.comparing(row -> (String) row.get("dueDate")));
        return result;
    }

    /** 当前用户可读文档的有效性总览，含尚未进入提醒期的文件。 */
    public List<Map<String, Object>> workbench(Long libraryId, Long userId) {
        List<Long> readableIds = memberService.getReadableLibraryIdList(userId);
        if (libraryId != null && !readableIds.contains(libraryId)) return List.of();
        List<Long> libraryIds = libraryId == null ? readableIds : List.of(libraryId);
        List<Map<String, Object>> rows = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Long id : libraryIds) {
            for (PmsKnowledgeDocumentDO document : documentService.getDocumentList(id, userId)) {
                if (!PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(document.getStatus())) continue;
                PmsKnowledgeReviewRuleDO rule = effectiveRule(document, userId);
                List<PmsKnowledgeReviewRecordDO> records = history(document.getId(), userId);
                String result = records.isEmpty() ? "unreviewed" : records.get(0).getResult();
                String stage = "normal";
                String dueDate = "";
                if ("abolished".equals(result) || "replaced".equals(result)) {
                    stage = "closed";
                } else if (rule == null) {
                    stage = "no_rule";
                } else {
                    LocalDate due = reviewBasis(document, records).plusMonths(rule.getIntervalMonths());
                    dueDate = due.toString();
                    if (due.isBefore(today)) stage = "overdue";
                    else if ("pending".equals(result) || !due.isAfter(today.plusDays(rule.getRemindDays()))) stage = "upcoming";
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("documentId", document.getId());
                row.put("libraryId", document.getLibraryId());
                row.put("title", document.getTitle());
                row.put("documentCode", document.getDocumentCode() == null ? "" : document.getDocumentCode());
                row.put("dueDate", dueDate);
                row.put("result", result);
                row.put("stage", stage);
                row.put("lastReviewDate", records.isEmpty() ? "" : records.get(0).getReviewDate().toString());
                rows.add(row);
            }
        }
        Map<String, Integer> stageOrder = Map.of("overdue", 0, "upcoming", 1, "normal", 2,
                "no_rule", 3, "closed", 4);
        rows.sort(Comparator.comparingInt((Map<String, Object> row) -> stageOrder.get((String) row.get("stage")))
                .thenComparing(row -> (String) row.get("dueDate"))
                .thenComparing(row -> (Long) row.get("documentId")));
        return rows;
    }

    private LocalDate reviewBasis(PmsKnowledgeDocumentDO document, List<PmsKnowledgeReviewRecordDO> records) {
        return records.stream().filter(row -> "valid".equals(row.getResult()))
                .map(PmsKnowledgeReviewRecordDO::getReviewDate).findFirst()
                .orElse(document.getCreateTime().toLocalDate());
    }
}
