package cn.iocoder.yudao.module.pms.controller.admin.kb.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentCreateReqVO;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeArchiveService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeDocumentService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_ARCHIVE_INVALID;

/** Import/export and content backup for one knowledge library. */
@RestController
@RequestMapping("/pms/kb/archive")
public class PmsKnowledgeArchiveController {
    @Resource private PmsKnowledgeArchiveService archiveService;
    @Resource private PmsKnowledgeDocumentService documentService;
    @Resource private FileApi fileApi;
    @Resource private cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService libraryMemberService;
    @Resource private cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeFolderService folderService;

    @PostMapping("/reindex")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Integer> rebuildSearchIndex(@RequestParam("libraryId") Long libraryId) {
        return success(documentService.rebuildLibrarySearchIndex(libraryId, getLoginUserId()));
    }

    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public ResponseEntity<byte[]> exportLibrary(@RequestParam("libraryId") Long libraryId,
            @RequestParam(value = "documentIds", required = false) List<Long> documentIds) throws Exception {
        byte[] archive = archiveService.exportLibrary(libraryId, documentIds, getLoginUserId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=knowledge-library-" + libraryId + ".zip")
                .contentType(MediaType.parseMediaType("application/zip")).body(archive);
    }

    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Integer> restoreLibrary(@RequestParam("libraryId") Long libraryId,
            @RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getSize() > 100L * 1024 * 1024) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        return success(archiveService.importLibrary(libraryId, file.getBytes(), getLoginUserId()));
    }

    @PostMapping(value = "/batch-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public CommonResult<List<Long>> batchImportFiles(@RequestParam("libraryId") Long libraryId,
            @RequestParam("folderId") Long folderId, @RequestParam("parentId") Long parentId,
            @RequestParam("files") List<MultipartFile> files) throws Exception {
        if (files.isEmpty() || files.size() > 20 || files.stream().mapToLong(MultipartFile::getSize).sum() > 100L * 1024 * 1024) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        libraryMemberService.validateLibraryAdmin(libraryId, getLoginUserId());
        if (folderId != 0 && !libraryId.equals(folderService.getFolder(folderId, getLoginUserId()).getLibraryId())) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        if (parentId != 0 && !libraryId.equals(documentService.getDocument(parentId, getLoginUserId()).getLibraryId())) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        List<PmsKnowledgeDocumentCreateReqVO> requests = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            String name = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename().replace('\\', '/');
            name = name.substring(name.lastIndexOf('/') + 1);
            if (name.isBlank() || name.length() > 255) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "bin";
            if (!ext.matches("[a-z0-9]{1,10}")) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            String title = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
            if (title.isBlank()) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            requests.add(new PmsKnowledgeDocumentCreateReqVO()
                    .setLibraryId(libraryId).setFolderId(folderId).setParentId(parentId)
                    .setTitle(title)
                    .setType(PmsKnowledgeDocumentTypeEnum.FILE.getType())
                    .setFileType(ext).setFileSize(file.getSize()));
        }
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            PmsKnowledgeDocumentCreateReqVO request = requests.get(i);
            request.setContent(fileApi.createFile(files.get(i).getBytes(), request.getTitle() + "." + request.getFileType()));
            ids.add(documentService.createDocument(request, getLoginUserId()));
        }
        return success(ids);
    }
}
