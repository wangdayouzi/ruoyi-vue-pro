package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.interaction.PmsKnowledgeDocumentShareDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentVersionMapper;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentStatusEnum;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeDocumentShareService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_ACCESS_DENIED;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_DOCUMENT_NOT_EXISTS;

/** Read-only Office preview. Uploaded files never enter an Office edit/save callback. */
@Service
public class PmsKnowledgeOfficePreviewService {
    private static final Set<String> WORD = Set.of("doc", "docx");
    private static final Set<String> CELL = Set.of("xls", "xlsx");
    private static final Set<String> SLIDE = Set.of("ppt", "pptx");
    private static final long TICKET_SECONDS = 600;

    @Resource
    private PmsKnowledgeDocumentService documentService;
    @Resource
    private FileApi fileApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private PmsKnowledgeDocumentShareService shareService;
    @Resource
    private PmsKnowledgeDocumentVersionMapper documentVersionMapper;
    @Resource
    private ObjectMapper objectMapper;
    @Value("${onlyoffice.doc-server-url:}")
    private String docServerUrl;
    @Value("${onlyoffice.callback-url:}")
    private String callbackUrl;
    @Value("${onlyoffice.jwt-secret:}")
    private String jwtSecret;
    @Value("${pms.kb.preview-secret:${onlyoffice.jwt-secret:}}")
    private String previewSecret;

    public boolean isPreviewSourceRegistered(PmsKnowledgeDocumentDO document) {
        return document != null && PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())
                && fileApi.isFileRegistered(document.getContent());
    }

    public boolean isPreviewAvailable() {
        return previewSecret != null && !previewSecret.isBlank();
    }

    public Map<String, Object> buildPreviewConfig(Long documentId, Long userId) {
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, userId);
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        String viewerName = user != null && user.getNickname() != null && !user.getNickname().isBlank()
                ? user.getNickname() : "用户" + userId;
        return buildPreviewConfig(document, String.valueOf(userId), viewerName,
                buildPreviewUrlForDocument(document));
    }

    public Map<String, Object> buildHistoryPreviewConfig(Long documentId, Long versionNo, Long userId) {
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, userId);
        PmsKnowledgeDocumentVersionDO version = documentService.getDocumentVersion(documentId, versionNo, userId);
        PmsKnowledgeDocumentDO source = new PmsKnowledgeDocumentDO()
                .setId(documentId).setVersionNo(versionNo).setTitle(version.getTitle())
                .setContent(version.getContent()).setType(version.getType())
                .setFileType(version.getFileType()).setFileSize(version.getFileSize());
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        String viewerName = user != null && user.getNickname() != null && !user.getNickname().isBlank()
                ? user.getNickname() : "用户" + userId;
        return buildPreviewConfig(source, String.valueOf(userId), viewerName,
                buildPreviewUrl(source, "h", null));
    }

    /** The share controller validates the token and access code before calling this method. */
    public Map<String, Object> buildSharePreviewConfig(PmsKnowledgeDocumentDO document,
            PmsKnowledgeDocumentShareDO share) {
        return buildPreviewConfig(document, "share-" + share.getId(), "访客",
                buildSharePreviewUrl(document, share));
    }

    private Map<String, Object> buildPreviewConfig(PmsKnowledgeDocumentDO document,
            String viewerId, String viewerName, String previewUrl) {
        String ext = normalizeOfficeExtension(document);
        if (ext == null || docServerUrl == null || docServerUrl.isBlank() || jwtSecret == null || jwtSecret.isBlank()
                || callbackUrl == null || !callbackUrl.contains("/admin-api/")) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        if (!isPreviewSourceRegistered(document)) {
            throw exception(cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_PREVIEW_FILE_NOT_REGISTERED);
        }
        Map<String, Object> file = new LinkedHashMap<>();
        file.put("fileType", ext);
        file.put("key", "pms-" + document.getId() + "-v" + document.getVersionNo());
        file.put("title", document.getTitle() + "." + ext);
        String sourceBase = callbackUrl.substring(0, callbackUrl.indexOf("/admin-api/"));
        file.put("url", sourceBase + previewUrl);

        Map<String, Object> permissions = new LinkedHashMap<>();
        permissions.put("edit", false);
        permissions.put("download", false);
        permissions.put("print", false);
        permissions.put("copy", true);

        Map<String, Object> editor = new LinkedHashMap<>();
        editor.put("mode", "view");
        editor.put("lang", "zh-CN");
        editor.put("user", Map.of("id", viewerId, "name", viewerName));
        editor.put("customization", Map.of("anonymous", Map.of("request", false)));
        file.put("permissions", permissions);
        permissions.put("comment", false);
        permissions.put("review", false);
        permissions.put("fillForms", false);
        permissions.put("chat", false);

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("docServerUrl", docServerUrl);
        config.put("document", file);
        config.put("documentType", WORD.contains(ext) ? "word" : CELL.contains(ext) ? "cell" : "slide");
        config.put("editorConfig", editor);
        config.put("height", "100%");
        config.put("width", "100%");
        try {
            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String body = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    objectMapper.writeValueAsBytes(config));
            String jwtBody = header + "." + body;
            config.put("token", jwtBody + "." + hmac(jwtBody, jwtSecret));
        } catch (Exception e) {
            throw new IllegalStateException("OnlyOffice 预览配置生成失败", e);
        }
        return config;
    }

    public String buildPreviewUrl(Long documentId, Long userId) {
        return buildPreviewUrlForDocument(documentService.getDocument(documentId, userId));
    }

    public String buildHistoryPreviewUrl(Long documentId, Long versionNo, Long userId) {
        documentService.getDocument(documentId, userId);
        PmsKnowledgeDocumentVersionDO version = documentService.getDocumentVersion(documentId, versionNo, userId);
        PmsKnowledgeDocumentDO source = new PmsKnowledgeDocumentDO()
                .setId(documentId).setVersionNo(versionNo).setType(version.getType());
        return buildPreviewUrl(source, "h", null);
    }

    /** Call only after another service has authorized public share access. */
    public String buildPreviewUrlForDocument(PmsKnowledgeDocumentDO document) {
        return buildPreviewUrl(document, "a", null);
    }

    public String buildSharePreviewUrl(PmsKnowledgeDocumentDO document, PmsKnowledgeDocumentShareDO share) {
        if (share == null || !shareService.isShareActive(share.getId(), share.getToken(), document.getId())) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        return buildPreviewUrl(document, "s", share);
    }

    private String buildPreviewUrl(PmsKnowledgeDocumentDO document, String scope,
            PmsKnowledgeDocumentShareDO share) {
        if (!PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())
                || !isPreviewAvailable()) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        long expires = Instant.now().getEpochSecond() + TICKET_SECONDS;
        String payload = document.getId() + "." + document.getVersionNo() + "." + expires + "." + scope;
        if (share != null) payload += "." + share.getId() + "." + share.getToken();
        return "/admin-api/pms/kb/document/preview-source?ticket=" + payload + "." + hmac(payload, previewSecret);
    }

    public PmsKnowledgeDocumentDO getPreviewSourceDocument(String ticket) {
        if (previewSecret == null || previewSecret.isBlank()) throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        String[] parts = ticket == null ? new String[0] : ticket.split("\\.");
        if (parts.length != 5 && parts.length != 7) throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        String payload = String.join(".", java.util.Arrays.copyOf(parts, parts.length - 1));
        if (!MessageDigest.isEqual(parts[parts.length - 1].getBytes(StandardCharsets.US_ASCII),
                hmac(payload, previewSecret).getBytes(StandardCharsets.US_ASCII))) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        Long documentId;
        Long versionNo;
        long expires;
        try {
            documentId = Long.valueOf(parts[0]);
            versionNo = Long.valueOf(parts[1]);
            expires = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        if (Instant.now().getEpochSecond() > expires) throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId);
        if (document == null
                || !PmsKnowledgeDocumentStatusEnum.NORMAL.getStatus().equals(document.getStatus())
                || !PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())) {
            throw exception(KNOWLEDGE_DOCUMENT_NOT_EXISTS);
        }
        String scope = parts[3];
        if ("s".equals(scope)) {
            if (parts.length != 7) throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
            Long shareId;
            try { shareId = Long.valueOf(parts[4]); }
            catch (NumberFormatException e) { throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED); }
            if (!shareService.isShareActive(shareId, parts[5], documentId)) {
                throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
            }
        } else if (!"a".equals(scope) && !"h".equals(scope) || parts.length != 5) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        if ("h".equals(scope)) {
            PmsKnowledgeDocumentVersionDO version = documentVersionMapper.selectByDocumentIdAndVersionNo(documentId, versionNo);
            if (version == null || !PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.getType())) {
                throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
            }
            document.setContent(version.getContent()).setFileType(version.getFileType());
        } else if (!versionNo.equals(document.getVersionNo())) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        return document;
    }

    public byte[] getPreviewSource(String ticket) throws Exception {
        return fileApi.getFileContentByUrl(getPreviewSourceDocument(ticket).getContent());
    }

    private String normalizeOfficeExtension(PmsKnowledgeDocumentDO document) {
        if (!PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())) return null;
        String ext = document.getFileType() == null ? "" : document.getFileType().trim().toLowerCase();
        ext = switch (ext) {
            case "application/msword" -> "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx";
            case "application/vnd.ms-excel" -> "xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
            case "application/vnd.ms-powerpoint" -> "ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx";
            default -> ext;
        };
        return WORD.contains(ext) || CELL.contains(ext) || SLIDE.contains(ext) ? ext : null;
    }

    private String hmac(String input, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("OnlyOffice 签名失败", e);
        }
    }
}
