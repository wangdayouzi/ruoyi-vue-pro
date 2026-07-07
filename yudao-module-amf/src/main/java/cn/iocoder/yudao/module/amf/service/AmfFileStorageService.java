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
     * @return 文件存储路径/URL
     */
    String storeFile(MultipartFile file, Long businessId);

    /**
     * 删除文件
     *
     * @param fileUrl 文件路径
     */
    void deleteFile(String fileUrl);

    /**
     * 获取文件的绝对存储路径
     *
     * @param fileUrl 文件URL
     * @return 绝对路径
     */
    String getAbsolutePath(String fileUrl);

}
