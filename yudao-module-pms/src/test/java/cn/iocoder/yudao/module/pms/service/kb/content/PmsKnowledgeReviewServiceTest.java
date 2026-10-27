package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRecordDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRuleDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeFolderDO;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeReviewRecordMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeReviewRuleMapper;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmsKnowledgeReviewServiceTest {
    @InjectMocks private PmsKnowledgeReviewService service;
    @Mock private PmsKnowledgeReviewRuleMapper ruleMapper;
    @Mock private PmsKnowledgeReviewRecordMapper recordMapper;
    @Mock private PmsKnowledgeDocumentService documentService;
    @Mock private PmsKnowledgeLibraryMemberService memberService;
    @Mock private PmsKnowledgeFolderService folderService;
    @Mock private PmsKnowledgeContentPermissionService permissionService;

    @Test
    void nestedDocumentInheritsContainingRootFolderReviewRule() {
        PmsKnowledgeDocumentDO child = new PmsKnowledgeDocumentDO().setId(9L).setLibraryId(10L).setFolderId(0L).setParentId(8L);
        when(documentService.getDocument(8L, 7L)).thenReturn(new PmsKnowledgeDocumentDO()
                .setId(8L).setLibraryId(10L).setFolderId(99L).setParentId(0L));
        when(folderService.getFolder(99L)).thenReturn(new PmsKnowledgeFolderDO().setId(99L).setLibraryId(10L).setParentId(0L));
        when(ruleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                new PmsKnowledgeReviewRuleDO().setId(1L).setFolderId(0L).setEnabled(true).setIntervalMonths(12),
                new PmsKnowledgeReviewRuleDO().setId(2L).setFolderId(99L).setEnabled(true).setIntervalMonths(6)));
        assertEquals(2L, service.effectiveRule(child, 7L).getId());
    }

    @Test
    void missingConclusionReturnsBusinessErrorInsteadOfServerError() {
        when(documentService.getDocument(9L, 7L)).thenReturn(new PmsKnowledgeDocumentDO().setId(9L).setLibraryId(10L).setPermissionId(3L));
        ServiceException error = assertThrows(ServiceException.class, () -> service.submit(9L, null, null, 7L));
        assertEquals(400, error.getCode());
        assertEquals("审阅结论无效", error.getMessage());
    }

    @Test
    void pendingConclusionRemainsInDueListAndDoesNotResetValidReviewCycle() {
        PmsKnowledgeDocumentDO document = new PmsKnowledgeDocumentDO().setId(9L).setLibraryId(10L)
                .setFolderId(0L).setTitle("待核查文件").setDocumentCode("Q200-0009").setStatus(1);
        document.setCreateTime(LocalDateTime.now().minusYears(2));
        when(documentService.getDocumentList(10L, 7L)).thenReturn(List.of(document));
        when(documentService.getDocument(9L, 7L)).thenReturn(document);
        when(ruleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                new PmsKnowledgeReviewRuleDO().setLibraryId(10L).setFolderId(0L)
                        .setIntervalMonths(12).setRemindDays(30).setEnabled(true)));
        when(recordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(
                new PmsKnowledgeReviewRecordDO().setResult("pending").setReviewDate(LocalDate.now()),
                new PmsKnowledgeReviewRecordDO().setResult("valid").setReviewDate(LocalDate.now().minusMonths(11))));

        List<Map<String, Object>> due = service.dueList(10L, 7L);
        assertEquals(1, due.size());
        assertEquals("pending", due.get(0).get("result"));
        assertFalse((Boolean) due.get(0).get("overdue"));
        assertEquals(LocalDate.now().plusMonths(1).toString(), due.get(0).get("dueDate"));
    }
}
