package cn.iocoder.yudao.module.amf.service;

import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储服务实现
 *
 * <p>文件统一通过 {@link FileService} 存入系统文件配置（后台 → 基础设施 → 文件配置），
 * OnlyOffice 需要本地文件时，会临时缓存到系统临时目录。
 *
 * @author yudao
 */
@Service
@Slf4j
public class AmfFileStorageServiceImpl implements AmfFileStorageService {

    @Resource
    private FileService fileService;

    @Resource
    private FileMapper fileMapper;

    @Override
    public String storeFile(MultipartFile file, Long businessId) {
        try {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] content = file.getBytes();
            return fileService.createFile(content, originalName, "amf", contentType);
        } catch (IOException e) {
            log.error("存储文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            FileDO fileDO = fileMapper.selectOne(new LambdaQueryWrapperX<FileDO>()
                    .eq(FileDO::getUrl, fileUrl));
            if (fileDO != null) {
                fileService.deleteFile(fileDO.getId());
                Path cachePath = getCachePath(fileDO.getPath());
                if (cachePath != null) {
                    Files.deleteIfExists(cachePath);
                }
            }
        } catch (Exception e) {
            log.warn("删除文件失败: fileUrl={}, error={}", fileUrl, e.getMessage());
        }
    }

    @Override
    public String getAbsolutePath(String fileUrl) {
        FileDO fileDO = fileMapper.selectOne(new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getUrl, fileUrl));
        if (fileDO == null) {
            throw new RuntimeException("文件记录不存在: " + fileUrl);
        }
        try {
            Path cachePath = getCachePath(fileDO.getPath());
            if (cachePath != null && !Files.exists(cachePath)) {
                Files.createDirectories(cachePath.getParent());
                byte[] content = fileService.getFileContent(fileDO.getConfigId(), fileDO.getPath());
                if (content != null) {
                    Files.write(cachePath, content);
                }
            }
            return cachePath != null ? cachePath.toAbsolutePath().toString() : fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("获取文件路径失败: " + fileUrl, e);
        }
    }

    @Override
    public void refreshCache(String fileUrl) {
        try {
            FileDO fileDO = fileMapper.selectOne(new LambdaQueryWrapperX<FileDO>()
                    .eq(FileDO::getUrl, fileUrl));
            if (fileDO != null) {
                Path cachePath = getCachePath(fileDO.getPath());
                if (cachePath != null) {
                    Files.deleteIfExists(cachePath);
                }
            }
        } catch (Exception e) {
            log.warn("刷新文件缓存失败: fileUrl={}", fileUrl, e);
        }
    }

    @Override
    public byte[] downloadFromInfra(String fileUrl) {
        FileDO fileDO = fileMapper.selectOne(new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getUrl, fileUrl));
        if (fileDO == null) {
            throw new RuntimeException("文件记录不存在: " + fileUrl);
        }
        try {
            return fileService.getFileContent(fileDO.getConfigId(), fileDO.getPath());
        } catch (Exception e) {
            throw new RuntimeException("从系统存储下载文件失败: " + fileUrl, e);
        }
    }

    @Override
    public String reuploadFile(String fileUrl, byte[] content, String fileName) {
        deleteFile(fileUrl);
        return fileService.createFile(content, fileName, "amf", null);
    }

    private static Path getCachePath(String path) {
        if (path == null) {
            return null;
        }
        return Paths.get(System.getProperty("java.io.tmpdir"), "amf-cache", path);
    }

}
