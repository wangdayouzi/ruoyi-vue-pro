package cn.iocoder.yudao.module.amf.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件存储服务实现（本地存储）
 *
 * @author yudao
 */
@Service
@Slf4j
public class AmfFileStorageServiceImpl implements AmfFileStorageService {

    @Value("${yudao.amf.file-storage-path:D:/uploadfile}")
    private String storageBasePath;

    @Override
    public String storeFile(MultipartFile file, Long businessId) {
        try {
            // 目录结构: {basePath}/{businessId}/{uuid}.{ext}
            String dir = storageBasePath + File.separator + businessId;
            Path dirPath = Paths.get(dir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = getFileExtension(originalName);
            String storedName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);

            Path targetPath = dirPath.resolve(storedName);
            file.transferTo(targetPath.toFile());

            // 返回相对路径: /{businessId}/{storedName}
            return businessId + "/" + storedName;
        } catch (IOException e) {
            log.error("存储文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(storageBasePath, fileUrl);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("删除文件失败: {}", e.getMessage());
        }
    }

    @Override
    public String getAbsolutePath(String fileUrl) {
        return Paths.get(storageBasePath, fileUrl).toAbsolutePath().toString();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

}
