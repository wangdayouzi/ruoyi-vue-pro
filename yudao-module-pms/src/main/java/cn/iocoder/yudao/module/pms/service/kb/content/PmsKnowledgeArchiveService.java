package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentCreateReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.folder.PmsKnowledgeFolderSaveReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeFolderDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentVersionMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeFolderMapper;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentStatusEnum;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_ARCHIVE_INVALID;

/** Portable content archive. Imports into an existing library with fresh permissions and no live share tokens. */
@Service
public class PmsKnowledgeArchiveService {
    private static final int MAX_ARCHIVE_BYTES = 100 * 1024 * 1024;
    private static final int MAX_ITEMS = 1000;
    private static final Pattern FILE_ENTRY = Pattern.compile("files/[0-9]+/v[0-9]+\\.bin");

    @Resource private PmsKnowledgeLibraryMemberService libraryMemberService;
    @Resource private PmsKnowledgeFolderMapper folderMapper;
    @Resource private PmsKnowledgeDocumentMapper documentMapper;
    @Resource private PmsKnowledgeDocumentVersionMapper versionMapper;
    @Resource private PmsKnowledgeFolderService folderService;
    @Resource private PmsKnowledgeDocumentService documentService;
    @Resource private FileApi fileApi;
    @Resource private ObjectMapper objectMapper;

    public byte[] exportLibrary(Long libraryId, List<Long> selectedDocumentIds, Long userId) throws Exception {
        libraryMemberService.validateLibraryAdmin(libraryId, userId);
        List<PmsKnowledgeFolderDO> folders = folderMapper.selectListByLibraryIdAndStatus(
                libraryId, PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus());
        List<PmsKnowledgeDocumentDO> documents = documentMapper.selectListByLibraryId(libraryId).stream()
                .filter(d -> PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(d.getStatus())
                        || PmsKnowledgeDocumentStatusEnum.DRAFT.getStatus().equals(d.getStatus()))
                .filter(d -> selectedDocumentIds == null || selectedDocumentIds.isEmpty()
                        || selectedDocumentIds.contains(d.getId()))
                .toList();
        if (selectedDocumentIds != null && !selectedDocumentIds.isEmpty()
                && documents.size() != new HashSet<>(selectedDocumentIds).size()) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        if (folders.size() + documents.size() > MAX_ITEMS) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        List<ArchiveFolder> folderItems = folders.stream()
                .map(f -> new ArchiveFolder(f.getId(), f.getParentId(), f.getTitle())).toList();
        List<ArchiveDocument> documentItems = new ArrayList<>();
        Set<Long> exportedDocumentIds = new HashSet<>();
        documents.forEach(document -> exportedDocumentIds.add(document.getId()));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        long rawSize = 0;
        try (ZipOutputStream zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            for (PmsKnowledgeDocumentDO document : documents) {
                List<PmsKnowledgeDocumentVersionDO> versions = versionMapper.selectListByDocumentId(document.getId());
                if (versions.isEmpty()) {
                    versions = List.of(new PmsKnowledgeDocumentVersionDO()
                            .setVersionNo(document.getVersionNo()).setTitle(document.getTitle())
                            .setContent(document.getContent()).setType(document.getType())
                            .setFileType(document.getFileType()).setFileSize(document.getFileSize()));
                }
                List<ArchiveVersion> versionItems = new ArrayList<>();
                for (PmsKnowledgeDocumentVersionDO version : versions) {
                    String content = version.getContent();
                    if (PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.getType()) && content != null) {
                        byte[] fileBytes = fileApi.getFileContentByUrl(content);
                        rawSize += fileBytes.length;
                        if (rawSize > MAX_ARCHIVE_BYTES) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                        content = "files/" + document.getId() + "/v" + version.getVersionNo() + ".bin";
                        zip.putNextEntry(new ZipEntry(content));
                        zip.write(fileBytes);
                        zip.closeEntry();
                    }
                    versionItems.add(new ArchiveVersion(version.getVersionNo(), version.getTitle(), content,
                            version.getType(), version.getFileType(), version.getFileSize(), version.getCreateTime(), version.getEditorUserId()));
                }
                documentItems.add(new ArchiveDocument(document.getId(), document.getFolderId(),
                        exportedDocumentIds.contains(document.getParentId()) ? document.getParentId() : 0L,
                        document.getStatus(), versionItems));
            }
            byte[] manifest = objectMapper.writeValueAsBytes(new ArchiveManifest(1, folderItems, documentItems));
            if (rawSize + manifest.length > MAX_ARCHIVE_BYTES) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            zip.putNextEntry(new ZipEntry("manifest.json"));
            zip.write(manifest);
            zip.closeEntry();
        }
        return bytes.toByteArray();
    }

    @Transactional(rollbackFor = Exception.class)
    public int importLibrary(Long targetLibraryId, byte[] archiveBytes, Long userId) throws Exception {
        libraryMemberService.validateLibraryAdmin(targetLibraryId, userId);
        if (archiveBytes == null || archiveBytes.length > MAX_ARCHIVE_BYTES) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        Map<String, byte[]> entries = readArchive(archiveBytes);
        ArchiveManifest manifest;
        try { manifest = objectMapper.readValue(entries.get("manifest.json"), ArchiveManifest.class); }
        catch (Exception e) { throw exception(KNOWLEDGE_ARCHIVE_INVALID); }
        if (manifest.formatVersion() != 1 || manifest.folders() == null || manifest.documents() == null
                || manifest.folders().size() + manifest.documents().size() > MAX_ITEMS) {
            throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        validateManifest(manifest, entries);
        Map<Long, Long> folderIds = new HashMap<>();
        folderIds.put(0L, 0L);
        List<ArchiveFolder> pendingFolders = new ArrayList<>(manifest.folders());
        while (!pendingFolders.isEmpty()) {
            int before = pendingFolders.size();
            pendingFolders.removeIf(folder -> {
                Long parent = folderIds.get(folder.parentId());
                if (parent == null) return false;
                Long newId = folderService.createFolder(new PmsKnowledgeFolderSaveReqVO()
                        .setLibraryId(targetLibraryId).setParentId(parent).setTitle(folder.title()), userId);
                folderIds.put(folder.id(), newId);
                return true;
            });
            if (pendingFolders.size() == before) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        Map<Long, Long> documentIds = new HashMap<>();
        documentIds.put(0L, 0L);
        List<ArchiveDocument> pendingDocuments = new ArrayList<>(manifest.documents());
        while (!pendingDocuments.isEmpty()) {
            int before = pendingDocuments.size();
            pendingDocuments.removeIf(item -> {
                Long folderId = folderIds.get(item.folderId());
                Long parentId = documentIds.get(item.parentId());
                if (folderId == null || parentId == null) return false;
                List<ArchiveVersion> revisions = item.versions().stream()
                        .sorted(Comparator.comparing(ArchiveVersion::versionNo)).toList();
                try {
                    Map<Long, String> restoredContent = new LinkedHashMap<>();
                    for (ArchiveVersion revision : revisions) {
                        restoredContent.put(revision.versionNo(), restoreContent(revision, entries, item.id()));
                    }
                    ArchiveVersion current = revisions.get(revisions.size() - 1);
                    Long newId = documentService.createDocument(new PmsKnowledgeDocumentCreateReqVO()
                            .setLibraryId(targetLibraryId).setFolderId(folderId).setParentId(parentId)
                            .setTitle(current.title()).setContent(restoredContent.get(current.versionNo()))
                            .setType(current.type()).setFileType(current.fileType()).setFileSize(current.fileSize())
                            .setStatus(item.status()), userId);
                    if (PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(item.status())) {
                        PmsKnowledgeDocumentVersionDO initial = versionMapper.selectByDocumentIdAndVersionNo(newId, 1L);
                        boolean first = true;
                        for (ArchiveVersion revision : revisions) {
                            PmsKnowledgeDocumentVersionDO saved = new PmsKnowledgeDocumentVersionDO()
                                    .setDocumentId(newId).setVersionNo(revision.versionNo())
                                    .setTitle(revision.title()).setContent(restoredContent.get(revision.versionNo()))
                                    .setType(revision.type()).setFileType(revision.fileType())
                                    .setFileSize(revision.fileSize()).setEditorUserId(revision.editorUserId() == null ? userId : revision.editorUserId());
                            saved.setCreateTime(revision.createTime());
                            if (first) {
                                versionMapper.update(saved.setContent(null).setFileType(null).setFileSize(null), new LambdaUpdateWrapper<PmsKnowledgeDocumentVersionDO>()
                                        .eq(PmsKnowledgeDocumentVersionDO::getId, initial.getId())
                                        .set(PmsKnowledgeDocumentVersionDO::getContent, restoredContent.get(revision.versionNo()))
                                        .set(PmsKnowledgeDocumentVersionDO::getFileType, revision.fileType())
                                        .set(PmsKnowledgeDocumentVersionDO::getFileSize, revision.fileSize()));
                                first = false;
                            } else {
                                versionMapper.insert(saved);
                            }
                        }
                    }
                    documentMapper.updateById(new PmsKnowledgeDocumentDO().setId(newId)
                            .setVersionNo(current.versionNo()));
                    documentIds.put(item.id(), newId);
                    return true;
                } catch (Exception e) {
                    throw new IllegalStateException("知识库内容恢复失败", e);
                }
            });
            if (pendingDocuments.size() == before) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        return manifest.documents().size();
    }

    private Map<String, byte[]> readArchive(byte[] archiveBytes) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        long rawSize = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(archiveBytes), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory() || !("manifest.json".equals(name) || FILE_ENTRY.matcher(name).matches())
                        || entries.containsKey(name) || entries.size() > MAX_ITEMS * 20) {
                    throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                }
                byte[] content = zip.readNBytes(MAX_ARCHIVE_BYTES + 1);
                rawSize += content.length;
                if (rawSize > MAX_ARCHIVE_BYTES) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                entries.put(name, content);
                zip.closeEntry();
            }
        }
        return entries;
    }

    private void validateManifest(ArchiveManifest manifest, Map<String, byte[]> entries) {
        Set<Long> folderIds = new HashSet<>();
        for (ArchiveFolder folder : manifest.folders()) {
            if (folder.id() == null || folder.id() <= 0 || folder.parentId() == null
                    || folder.title() == null || folder.title().isBlank() || folder.title().length() > 255
                    || !folderIds.add(folder.id())) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
        }
        Set<Long> docIds = new HashSet<>();
        int revisionCount = 0;
        for (ArchiveDocument doc : manifest.documents()) {
            if (doc.id() == null || doc.id() <= 0 || doc.folderId() == null || doc.parentId() == null
                    || !docIds.add(doc.id()) || doc.versions() == null || doc.versions().isEmpty()
                    || doc.versions().size() > MAX_ITEMS
                    || !(PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(doc.status())
                    || PmsKnowledgeDocumentStatusEnum.DRAFT.getStatus().equals(doc.status()))) {
                throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            }
            Set<Long> versionNos = new HashSet<>();
            for (ArchiveVersion version : doc.versions()) {
                if (++revisionCount > MAX_ITEMS * 5) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                if (version.versionNo() == null || version.versionNo() < 1 || !versionNos.add(version.versionNo())
                        || version.title() == null || version.title().isBlank() || version.title().length() > 255
                        || !PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.type())
                        && !PmsKnowledgeDocumentTypeEnum.RICH_TEXT.getType().equals(version.type())) {
                    throw exception(KNOWLEDGE_ARCHIVE_INVALID);
                }
                if (PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.type())
                        && version.content() != null && (!FILE_ENTRY.matcher(version.content()).matches()
                        || !entries.containsKey(version.content()))) throw exception(KNOWLEDGE_ARCHIVE_INVALID);
            }
        }
    }

    private String restoreContent(ArchiveVersion version, Map<String, byte[]> entries, Long oldDocumentId) {
        if (!PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.type()) || version.content() == null) {
            return version.content();
        }
        String ext = version.fileType() == null ? "bin" : version.fileType().toLowerCase();
        if (!ext.matches("[a-z0-9]{1,10}")) ext = "bin";
        return fileApi.createFile(entries.get(version.content()),
                "knowledge-" + oldDocumentId + "-v" + version.versionNo() + "." + ext);
    }

    public record ArchiveManifest(int formatVersion, List<ArchiveFolder> folders, List<ArchiveDocument> documents) {}
    public record ArchiveFolder(Long id, Long parentId, String title) {}
    public record ArchiveDocument(Long id, Long folderId, Long parentId, Integer status,
                                  List<ArchiveVersion> versions) {}
    public record ArchiveVersion(Long versionNo, String title, String content, Integer type,
                                 String fileType, Long fileSize, java.time.LocalDateTime createTime, Long editorUserId) {}
}
