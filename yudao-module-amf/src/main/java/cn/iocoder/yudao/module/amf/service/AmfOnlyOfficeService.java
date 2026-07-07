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
        editorConfig.put("mode", "edit");

        // 用户信息
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", userId);
        user.put("name", userName);
        editorConfig.put("user", user);

        // 自定义
        Map<String, Object> customization = new LinkedHashMap<>();
        customization.put("goback", new LinkedHashMap<String, Object>() {{
            put("blank", true);
            put("text", "返回");
            put("url", "#");
        }});
        customization.put("forcesave", false);
        customization.put("compactHeader", false);
        editorConfig.put("customization", customization);

        config.put("editorConfig", editorConfig);
        config.put("type", getDocumentType(fileExt));

        // 高度和宽度
        config.put("height", "100%");
        config.put("width", "100%");

        return config;
    }

    /**
     * 处理 OnlyOffice 保存回调
     */
    public void handleSaveCallback(Long versionId, String downloadUrl) {
        log.info("OnlyOffice 保存回调: versionId={}, downloadUrl={}", versionId, downloadUrl);
        // 注意：OnlyOffice 回调保存时，版本不变，只是更新内容。
        // 如需自动创建新版本，可在此扩展。
        // 当前实现：回调保存时直接覆盖原文件内容，不更新版本号。
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
