package cn.iocoder.yudao.module.amf.service;

import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileVersionDO;
import cn.iocoder.yudao.module.amf.dal.mysql.AmfFileVersionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * OnlyOffice 在线编辑服务
 *
 * @author yudao
 */
@Service
@Slf4j
public class AmfOnlyOfficeService {

    @Resource
    private AmfFileVersionMapper amfFileVersionMapper;

    @Resource
    private AmfFileStorageService amfFileStorageService;

    @Value("${onlyoffice.doc-server-url:http://localhost:8088}")
    private String docServerUrl;

    @Value("${onlyoffice.callback-url:http://localhost:48080/admin-api/amf/onlyoffice/callback}")
    private String callbackUrl;

    @Value("${onlyoffice.jwt-secret:}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成 OnlyOffice 编辑器配置
     */
    public Map<String, Object> buildEditorConfig(Long versionId, String userId, String userName) {
        AmfFileVersionDO version = amfFileVersionMapper.selectById(versionId);
        if (version == null) {
            throw new RuntimeException("文件版本记录不存在");
        }

        String filePath = amfFileStorageService.getAbsolutePath(version.getFileUrl());
        String fileName = version.getFileName();
        String fileExt = version.getFileType();

        Map<String, Object> config = new LinkedHashMap<>();

        // docServerUrl 供前端加载 OnlyOffice JS API
        config.put("docServerUrl", docServerUrl);

        // 文档信息
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("fileType", fileExt);
        document.put("key", versionId.toString() + "_v" + version.getVersionNo());
        document.put("title", fileName);
        document.put("url", callbackUrl.replace("/callback", "/download?versionId=" + versionId));
        config.put("document", document);

        // 编辑器配置
        Map<String, Object> editorConfig = new LinkedHashMap<>();
        editorConfig.put("callbackUrl", callbackUrl + "?versionId=" + versionId);
        editorConfig.put("lang", "zh-CN");
        editorConfig.put("mode", "view");

        // 用户信息
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", userId);
        user.put("name", userName);
        editorConfig.put("user", user);

        // 自定义
        Map<String, Object> customization = new LinkedHashMap<>();
        customization.put("forcesave", true);
        customization.put("compactHeader", false);
        customization.put("plugins", false);  // 隐藏插件（AI 等）
        customization.put("about", false);    // 隐藏"关于"
        customization.put("feedback", false); // 隐藏"反馈"

        // 隐藏左上角 Logo
        Map<String, Object> logo = new LinkedHashMap<>();
        logo.put("image", "");
        logo.put("imageEmbedded", "");
        customization.put("logo", logo);
        editorConfig.put("customization", customization);

        // 权限配置（chat 从 customization 移到 permissions）
        Map<String, Object> permissions = new LinkedHashMap<>();
        permissions.put("chat", false);
        editorConfig.put("permissions", permissions);

        config.put("editorConfig", editorConfig);
        config.put("type", getDocumentType(fileExt));

        // 高度和宽度
        config.put("height", "100%");
        config.put("width", "100%");

        // JWT 签名
        if (jwtSecret != null && !jwtSecret.isEmpty()) {
            try {
                String configJson = objectMapper.writeValueAsString(config);
                config.put("token", signJwt(configJson));
            } catch (Exception e) {
                log.error("OnlyOffice JWT 签名失败", e);
            }
        }

        return config;
    }

    /**
     * 处理 OnlyOffice 保存回调
     */
    public void handleSaveCallback(Long versionId, String downloadUrl) {
        log.info("OnlyOffice 保存回调: versionId={}, downloadUrl={}", versionId, downloadUrl);

        AmfFileVersionDO version = amfFileVersionMapper.selectById(versionId);
        if (version == null) {
            log.error("文件版本记录不存在: versionId={}", versionId);
            return;
        }

        // 从 OnlyOffice Document Server 下载修改后的文件，覆盖原文件
        try {
            String filePath = amfFileStorageService.getAbsolutePath(version.getFileUrl());
            byte[] fileBytes = downloadFromUrl(downloadUrl);
            Files.write(Paths.get(filePath), fileBytes);

            // 更新版本记录的文件大小
            version.setFileSize((long) fileBytes.length);
            amfFileVersionMapper.updateById(version);

            log.info("OnlyOffice 保存成功: versionId={}, filePath={}, size={}",
                    versionId, filePath, fileBytes.length);
        } catch (Exception e) {
            log.error("OnlyOffice 保存失败: versionId={}, downloadUrl={}", versionId, downloadUrl, e);
        }
    }

    /**
     * 获取下载文件的字节数据
     */
    public byte[] getFileBytes(Long versionId) throws Exception {
        AmfFileVersionDO version = amfFileVersionMapper.selectById(versionId);
        if (version == null) {
            throw new RuntimeException("文件版本记录不存在");
        }
        String filePath = amfFileStorageService.getAbsolutePath(version.getFileUrl());
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * 获取文件名
     */
    public String getFileName(Long versionId) {
        AmfFileVersionDO version = amfFileVersionMapper.selectById(versionId);
        if (version == null) {
            throw new RuntimeException("文件版本记录不存在");
        }
        return version.getFileName();
    }

    /**
     * JWT 签名（HMAC-SHA256）
     */
    public String signJwt(String payload) {
        try {
            String header = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String body = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            String signingInput = header + "." + body;

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

            return signingInput + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("JWT 签名失败", e);
        }
    }

    /**
     * JWT 验证，返回 payload
     */
    public String verifyJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new RuntimeException("JWT 格式错误");

            String expectedSig = signJwtBody(parts[0] + "." + parts[1]);
            if (!expectedSig.equals(parts[2])) throw new RuntimeException("JWT 签名不匹配");

            return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("JWT 验证失败", e);
            return null;
        }
    }

    private String signJwtBody(String headerDotBody) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(headerDotBody.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("JWT 签名失败", e);
        }
    }

    /**
     * 从 URL 下载文件字节
     */
    private byte[] downloadFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        try (InputStream in = url.openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    /**
     * 根据文件扩展名确定 OnlyOffice 文档类型
     */
    private String getDocumentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "doc", "docx", "odt", "rtf", "txt", "html", "mht", "epub" -> "text";
            case "xls", "xlsx", "ods", "csv" -> "spreadsheet";
            case "ppt", "pptx", "odp" -> "presentation";
            case "pdf", "djvu", "xps" -> "pdf";
            default -> "text";
        };
    }

}
