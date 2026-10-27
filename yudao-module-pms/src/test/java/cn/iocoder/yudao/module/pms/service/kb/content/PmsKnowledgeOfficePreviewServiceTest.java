package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentStatusEnum;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PmsKnowledgeOfficePreviewServiceTest {

    private final PmsKnowledgeOfficePreviewService service = new PmsKnowledgeOfficePreviewService();
    private final PmsKnowledgeDocumentService documentService = mock(PmsKnowledgeDocumentService.class);
    private final FileApi fileApi = mock(FileApi.class);
    private final AdminUserApi adminUserApi = mock(AdminUserApi.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "documentService", documentService);
        ReflectionTestUtils.setField(service, "fileApi", fileApi);
        ReflectionTestUtils.setField(service, "adminUserApi", adminUserApi);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "docServerUrl", "http://localhost:8088");
        ReflectionTestUtils.setField(service, "callbackUrl", "http://localhost:48080/admin-api/amf/onlyoffice/callback");
        ReflectionTestUtils.setField(service, "jwtSecret", "test-office-secret");
        ReflectionTestUtils.setField(service, "previewSecret", "test-preview-secret");
    }

    @SuppressWarnings("unchecked")
    @Test
    void officePreviewIsReadOnlyAndSourceTicketIsVersionBound() throws Exception {
        PmsKnowledgeDocumentDO document = new PmsKnowledgeDocumentDO().setId(42L).setVersionNo(2L)
                .setTitle("说明书").setType(PmsKnowledgeDocumentTypeEnum.FILE.getType())
                .setFileType("docx").setStatus(PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus())
                .setContent("https://files.example.com/guide.docx");
        when(documentService.getDocument(42L, 7L)).thenReturn(document);
        when(documentService.getDocument(42L)).thenReturn(document);
        byte[] bytes = "office file".getBytes();
        when(fileApi.getFileContentByUrl(document.getContent())).thenReturn(bytes);
        when(fileApi.isFileRegistered(document.getContent())).thenReturn(true);

        Map<String, Object> config = service.buildPreviewConfig(42L, 7L);
        Map<String, Object> editorConfig = (Map<String, Object>) config.get("editorConfig");
        Map<String, Object> permissions = (Map<String, Object>) ((Map<String, Object>) config.get("document")).get("permissions");
        Map<String, Object> file = (Map<String, Object>) config.get("document");
        assertEquals("view", editorConfig.get("mode"));
        assertEquals(false, permissions.get("edit"));
        assertEquals(false, permissions.get("download"));
        assertEquals("pms-42-v2", file.get("key"));
        assertNotNull(config.get("token"));
        document.setFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        Map<String, Object> mimeConfig = service.buildPreviewConfig(42L, 7L);
        assertEquals("docx", ((Map<String, Object>) mimeConfig.get("document")).get("fileType"));

        String ticket = service.buildPreviewUrl(42L, 7L).split("ticket=")[1];
        assertArrayEquals(bytes, service.getPreviewSource(ticket));
        document.setVersionNo(3L);
        assertThrows(ServiceException.class, () -> service.getPreviewSourceDocument(ticket));
        assertFalse(service.buildPreviewUrlForDocument(document).contains("guide.docx"));
    }
    @Test
    void closingShareRevokesAlreadyIssuedSourceTicket() {
        var shares = mock(cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeDocumentShareService.class);
        ReflectionTestUtils.setField(service, "shareService", shares);
        var document = new PmsKnowledgeDocumentDO().setId(42L).setVersionNo(1L)
                .setType(PmsKnowledgeDocumentTypeEnum.FILE.getType())
                .setStatus(PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus());
        var share = new cn.iocoder.yudao.module.pms.dal.dataobject.kb.interaction.PmsKnowledgeDocumentShareDO()
                .setId(9L).setDocumentId(42L).setToken("token");
        when(documentService.getDocument(42L)).thenReturn(document);
        when(shares.isShareActive(9L, "token", 42L)).thenReturn(true);
        String ticket = service.buildSharePreviewUrl(document, share).split("ticket=")[1];
        assertNotNull(service.getPreviewSourceDocument(ticket));
        when(shares.isShareActive(9L, "token", 42L)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.getPreviewSourceDocument(ticket));
    }

    @Test
    void historyTicketLoadsRequestedSnapshotAfterCurrentVersionChanges() {
        var mapper = mock(cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentVersionMapper.class);
        ReflectionTestUtils.setField(service, "documentVersionMapper", mapper);
        var current = new PmsKnowledgeDocumentDO().setId(42L).setVersionNo(5L)
                .setType(PmsKnowledgeDocumentTypeEnum.FILE.getType())
                .setStatus(PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus()).setContent("latest-url");
        var old = new cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO()
                .setDocumentId(42L).setVersionNo(2L).setType(PmsKnowledgeDocumentTypeEnum.FILE.getType())
                .setContent("historical-url").setFileType("pdf");
        when(documentService.getDocument(42L, 7L)).thenReturn(current);
        when(documentService.getDocument(42L)).thenReturn(current);
        when(documentService.getDocumentVersion(42L, 2L, 7L)).thenReturn(old);
        when(mapper.selectByDocumentIdAndVersionNo(42L, 2L)).thenReturn(old);
        String ticket = service.buildHistoryPreviewUrl(42L, 2L, 7L).split("ticket=")[1];
        assertEquals("historical-url", service.getPreviewSourceDocument(ticket).getContent());
        assertThrows(ServiceException.class, () -> service.getPreviewSourceDocument(ticket + "x"));
    }

}
