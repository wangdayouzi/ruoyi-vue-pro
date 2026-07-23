package cn.iocoder.yudao.module.amf.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 *
 * @author yudao
 */
public interface AmfFileStorageService {

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @param businessId 业务单据ID
     * @return 文件访问 URL
     */
    String storeFile(MultipartFile file, Long businessId);

    /**
     * 删除文件
     *
     * @param fileUrl 文件 URL
     */
    void deleteFile(String fileUrl);

    /**
     * 获取文件的绝对存储路径（仅本地缓存，供 OnlyOffice 使用）
     *
     * @param fileUrl 文件 URL
     * @return 绝对路径
     */
    String getAbsolutePath(String fileUrl);

    /**
     * 刷新文件本地缓存
     *
     * @param fileUrl 文件 URL
     */
    void refreshCache(String fileUrl);

    /**
     * 从 Infra 存储下载文件内容
     *
     * @param fileUrl 文件 URL
     * @return 文件字节数组
     */
    byte[] downloadFromInfra(String fileUrl);

    /**
     * 重新上传文件到 Infra 存储（替换旧文件记录）
     *
     * @param fileUrl  旧文件 URL
     * @param content  新文件内容
     * @param fileName 文件名
     * @return 新文件 URL
     */
    String reuploadFile(String fileUrl, byte[] content, String fileName);

}
