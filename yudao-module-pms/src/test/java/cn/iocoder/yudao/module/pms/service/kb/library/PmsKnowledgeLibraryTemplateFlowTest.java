package cn.iocoder.yudao.module.pms.service.kb.library;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentCreateReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.library.vo.library.PmsKnowledgeLibrarySaveReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.library.PmsKnowledgeLibraryTemplateDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.library.PmsKnowledgeLibraryMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.library.PmsKnowledgeLibraryTemplateMapper;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeDocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PmsKnowledgeLibraryTemplateFlowTest {
    @InjectMocks private PmsKnowledgeLibraryServiceImpl libraryService;
    @InjectMocks private PmsKnowledgeLibraryTemplateServiceImpl templateService;
    @Mock private PmsKnowledgeLibraryMapper libraryMapper;
    @Mock private PmsKnowledgeLibraryTemplateMapper templateMapper;
    @Mock private PmsKnowledgeDocumentService documentService;

    @Test
    void templateWithoutNumberRuleFailsBeforeCreatingLibrary() {
        PmsKnowledgeLibrarySaveReqVO request = new PmsKnowledgeLibrarySaveReqVO().setTemplateId(1L);
        ServiceException error = assertThrows(ServiceException.class, () -> libraryService.createLibrary(request, 5L));
        assertTrue(error.getMessage().contains("默认编号规则"));
        verifyNoInteractions(libraryMapper);
    }

    @Test
    void templateDocumentsKeepMarkdownAndUseNormalDocumentCreation() {
        PmsKnowledgeLibraryTemplateDO.Document document = new PmsKnowledgeLibraryTemplateDO.Document()
                .setTitle("说明").setContent("# 标题\n\n正文");
        PmsKnowledgeLibraryTemplateDO template = new PmsKnowledgeLibraryTemplateDO()
                .setId(1L).setStatus(0).setDocuments(List.of(document));
        when(templateMapper.selectById(1L)).thenReturn(template);
        templateService.createTemplateDocumentList(1L, 2L, 5L);
        ArgumentCaptor<PmsKnowledgeDocumentCreateReqVO> captor = ArgumentCaptor.forClass(PmsKnowledgeDocumentCreateReqVO.class);
        verify(documentService).createDocument(captor.capture(), eq(5L));
        PmsKnowledgeDocumentCreateReqVO created = captor.getValue();
        assertEquals(2L, created.getLibraryId());
        assertEquals(0L, created.getFolderId());
        assertEquals("# 标题\n\n正文", created.getContent());
        assertNull(created.getStatus()); // default is published by the document service
    }
}
