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
            @RequestParam(value = "documentIds", required = false) List<Long> documentIds,
            @RequestParam(value = "folderId", required = false) Long folderId) throws Exception {
        byte[] archive = archiveService.exportLibrary(libraryId, documentIds, folderId, getLoginUserId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=knowledge-library-" + libraryId + ".zip")
                .contentType(MediaType.parseMediaType("application/zip")).body(archive);
    }

    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Integer> restoreLibrary(@RequestParam("libraryId") Long libraryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", defaultValue = "0") Long folderId) throws Exception {
        if (file.isEmpty() || file.getSize() > 100L * 1024 * 1024) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        return success(archiveService.importLibrary(libraryId, file.getBytes(), folderId, getLoginUserId()));
    }

    @PostMapping(value = "/batch-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public CommonResult<List<Long>> batchImportFiles(@RequestParam("libraryId") Long libraryId,
            @RequestParam("folderId") Long folderId, @RequestParam("parentId") Long parentId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "paths", required = false) List<String> paths) throws Exception {
        if (files.isEmpty() || files.size() > 1000 || files.stream().mapToLong(MultipartFile::getSize).sum() > 100L * 1024 * 1024) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        libraryMemberService.validateLibraryAdmin(libraryId, getLoginUserId());
        if (folderId != 0 && !libraryId.equals(folderService.getFolder(folderId, getLoginUserId()).getLibraryId())) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        if (parentId != 0 && !libraryId.equals(documentService.getDocument(parentId, getLoginUserId()).getLibraryId())) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        if (paths != null && (paths.size() != files.size() || parentId != 0)) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        List<String[]> relativePaths = new ArrayList<>();
        if (paths != null) {
            for (String path : paths) {
                String[] parts = path.replace('\\', '/').split("/", -1);
                if (parts.length > 32) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                for (String part : parts) {
                    if (part.isBlank() || part.equals(".") || part.equals("..") || part.length() > 255
                            || part.contains(":") || part.chars().anyMatch(c -> c < 32)) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                }
                relativePaths.add(parts);
            }
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
        java.util.Map<String, Long> importedFolders = new java.util.HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            PmsKnowledgeDocumentCreateReqVO request = requests.get(i);
            if (paths != null) {
                String[] parts = relativePaths.get(i);
                Long destination = folderId;
                String key = "";
                for (int j = 0; j < parts.length - 1; j++) {
                    key += "/" + parts[j];
                    Long existing = importedFolders.get(key);
                    if (existing == null) {
                        existing = folderService.createFolder(new cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.folder.PmsKnowledgeFolderSaveReqVO()
                                .setLibraryId(libraryId).setParentId(destination).setTitle(parts[j]), getLoginUserId());
                        importedFolders.put(key, existing);
                    }
                    destination = existing;
                }
                request.setFolderId(destination);
            }
            request.setContent(fileApi.createFile(files.get(i).getBytes(), request.getTitle() + "." + request.getFileType()));
            ids.add(documentService.createDocument(request, getLoginUserId()));
        }
        return success(ids);
    }
}
