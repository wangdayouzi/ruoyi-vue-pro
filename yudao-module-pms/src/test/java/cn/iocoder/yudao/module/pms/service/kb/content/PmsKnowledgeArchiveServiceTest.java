package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.*;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.*;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.*;
import java.util.List;
import java.util.zip.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PmsKnowledgeArchiveServiceTest {
    private final PmsKnowledgeArchiveService service = new PmsKnowledgeArchiveService();
    private final PmsKnowledgeDocumentMapper documents = mock(PmsKnowledgeDocumentMapper.class);
    private final PmsKnowledgeDocumentVersionMapper versions = mock(PmsKnowledgeDocumentVersionMapper.class);
    private final PmsKnowledgeFolderMapper folders = mock(PmsKnowledgeFolderMapper.class);
    private final FileApi files = mock(FileApi.class);
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    PmsKnowledgeArchiveServiceTest() {
        ReflectionTestUtils.setField(service, "libraryMemberService", mock(PmsKnowledgeLibraryMemberService.class));
        ReflectionTestUtils.setField(service, "documentMapper", documents);
        ReflectionTestUtils.setField(service, "versionMapper", versions);
        ReflectionTestUtils.setField(service, "folderMapper", folders);
        ReflectionTestUtils.setField(service, "fileApi", files);
        ReflectionTestUtils.setField(service, "objectMapper", json);
    }

    @Test
    void exportContainsHistoricalFileBytesAndVersionMetadata() throws Exception {
        when(folders.selectListByLibraryIdAndStatus(1L, 1)).thenReturn(List.of());
        when(documents.selectListByLibraryId(1L)).thenReturn(List.of(new PmsKnowledgeDocumentDO()
                .setId(10L).setFolderId(0L).setParentId(0L).setStatus(1)));
        var version = new PmsKnowledgeDocumentVersionDO().setVersionNo(2L).setTitle("历史文件")
                .setType(4).setFileType("txt").setContent("file-url").setEditorUserId(7L);
        version.setCreateTime(java.time.LocalDateTime.of(2026, 10, 20, 12, 0));
        // Use the application's enum instead of assuming its persisted value.
        version.setType(cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum.FILE.getType());
        when(versions.selectListByDocumentId(10L)).thenReturn(List.of(version));
        when(files.getFileContentByUrl("file-url")).thenReturn("original bytes".getBytes());
        byte[] archive = service.exportLibrary(1L, List.of(10L), 7L);
        try (var zip = new ZipInputStream(new ByteArrayInputStream(archive))) {
            assertEquals("files/10/v2.bin", zip.getNextEntry().getName());
            assertEquals("original bytes", new String(zip.readAllBytes()));
            assertEquals("manifest.json", zip.getNextEntry().getName());
            var manifest = json.readValue(zip.readAllBytes(), PmsKnowledgeArchiveService.ArchiveManifest.class);
            var saved = manifest.documents().get(0).versions().get(0);
            assertEquals(2L, saved.versionNo());
            assertEquals(version.getCreateTime(), saved.createTime());
            assertEquals(7L, saved.editorUserId());
        }
    }

    @Test
    void invalidArchivePathIsRejectedBeforeAnyFileWrite() throws Exception {
        var bytes = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry("../outside.txt"));
            zip.write(1);
        }
        assertThrows(ServiceException.class, () -> service.importLibrary(1L, bytes.toByteArray(), 7L));
        verifyNoInteractions(files);
    }
}
