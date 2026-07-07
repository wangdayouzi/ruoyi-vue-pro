package cn.iocoder.yudao.module.amf.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.amf.controller.admin.vo.AmfBusinessPageReqVO;
import cn.iocoder.yudao.module.amf.controller.admin.vo.AmfBusinessSaveReqVO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfBusinessDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileVersionDO;
import cn.iocoder.yudao.module.amf.dal.mysql.AmfBusinessMapper;
import cn.iocoder.yudao.module.amf.dal.mysql.AmfFileMapper;
import cn.iocoder.yudao.module.amf.dal.mysql.AmfFileVersionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.amf.enums.ErrorCodeConstants.*;

/**
 * 分析方法文件 - 业务单据 Service 实现类
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class AmfBusinessServiceImpl implements AmfBusinessService {

    @Resource
    private AmfBusinessMapper amfBusinessMapper;

    @Resource
    private AmfFileVersionMapper amfFileVersionMapper;

    @Resource
    private AmfFileMapper amfFileMapper;

    @Resource
    private AmfFileStorageService amfFileStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBusiness(AmfBusinessSaveReqVO createReqVO) {
        // 校验BAS编号唯一性
        validateBasNoUnique(null, createReqVO.getBasNo());

        // 插入业务单据
        AmfBusinessDO business = BeanUtils.toBean(createReqVO, AmfBusinessDO.class);
        amfBusinessMapper.insert(business);
        return business.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBusiness(AmfBusinessSaveReqVO updateReqVO) {
        // 校验存在
        validateBusinessExists(updateReqVO.getId());
        // 校验BAS编号唯一性
        validateBasNoUnique(updateReqVO.getId(), updateReqVO.getBasNo());

        // 更新
        AmfBusinessDO updateObj = BeanUtils.toBean(updateReqVO, AmfBusinessDO.class);
        amfBusinessMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusiness(Long id) {
        validateBusinessExists(id);
        // 删除关联的文件及版本
        List<AmfFileDO> files = amfFileMapper.selectListByBusinessId(id);
        for (AmfFileDO file : files) {
            List<AmfFileVersionDO> versions = amfFileVersionMapper.selectListByFileId(file.getId());
            for (AmfFileVersionDO version : versions) {
                amfFileStorageService.deleteFile(version.getFileUrl());
                amfFileVersionMapper.deleteById(version.getId());
            }
            amfFileMapper.deleteById(file.getId());
        }
        amfBusinessMapper.deleteById(id);
    }

    @Override
    public AmfBusinessDO getBusiness(Long id) {
        return validateBusinessExists(id);
    }

    @Override
    public PageResult<AmfBusinessDO> getBusinessPage(AmfBusinessPageReqVO pageReqVO) {
        return amfBusinessMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AmfFileVersionDO uploadFile(Long businessId, MultipartFile file, String changeDescription) {
        validateBusinessExists(businessId);

        // 存储文件
        String fileUrl = amfFileStorageService.storeFile(file, businessId);

        // 查找或创建文件记录（同文件名视为同一文件，追加版本）
        String originalName = file.getOriginalFilename();
        List<AmfFileDO> existingFiles = amfFileMapper.selectListByBusinessId(businessId);
        AmfFileDO fileDO = existingFiles.stream()
                .filter(f -> f.getFileName().equals(originalName))
                .findFirst().orElse(null);

        if (fileDO == null) {
            fileDO = new AmfFileDO();
            fileDO.setBusinessId(businessId);
            fileDO.setFileName(originalName);
            fileDO.setFileVersion(0);
            fileDO.setCreateTime(LocalDateTime.now());
            amfFileMapper.insert(fileDO);
        }

        // 版本号递增
        int newVersion = fileDO.getFileVersion() + 1;

        // 创建版本记录
        AmfFileVersionDO versionDO = new AmfFileVersionDO();
        versionDO.setFileId(fileDO.getId());
        versionDO.setBusinessId(businessId);
        versionDO.setVersionNo(newVersion);
        versionDO.setFileName(originalName);
        versionDO.setFileUrl(fileUrl);
        versionDO.setFileSize(file.getSize());
        versionDO.setFileType(getFileExtension(originalName));
        versionDO.setChangeDescription(changeDescription);
        versionDO.setCreateTime(LocalDateTime.now());
        amfFileVersionMapper.insert(versionDO);

        // 更新文件记录的当前版本
        fileDO.setFileUrl(fileUrl);
        fileDO.setFileVersion(newVersion);
        amfFileMapper.updateById(fileDO);

        return versionDO;
    }

    @Override
    public List<AmfFileDO> getFileList(Long businessId) {
        return amfFileMapper.selectListByBusinessId(businessId);
    }

    @Override
    public List<AmfFileVersionDO> getFileVersionList(Long fileId) {
        return amfFileVersionMapper.selectListByFileId(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        List<AmfFileVersionDO> versions = amfFileVersionMapper.selectListByFileId(fileId);
        for (AmfFileVersionDO version : versions) {
            amfFileStorageService.deleteFile(version.getFileUrl());
            amfFileVersionMapper.deleteById(version.getId());
        }
        amfFileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileVersion(Long versionId) {
        AmfFileVersionDO version = amfFileVersionMapper.selectById(versionId);
        if (version == null) {
            throw exception(AMF_FILE_VERSION_NOT_EXISTS);
        }
        // 删除文件
        amfFileStorageService.deleteFile(version.getFileUrl());
        // 删除版本记录
        amfFileVersionMapper.deleteById(versionId);
    }

    // ========== 校验方法 ==========

    private AmfBusinessDO validateBusinessExists(Long id) {
        AmfBusinessDO business = amfBusinessMapper.selectById(id);
        if (business == null) {
            throw exception(AMF_BUSINESS_NOT_EXISTS);
        }
        return business;
    }

    private void validateBasNoUnique(Long id, String basNo) {
        AmfBusinessDO existing = amfBusinessMapper.selectByBasNo(basNo);
        if (existing != null && !Objects.equals(existing.getId(), id)) {
            throw exception(AMF_BUSINESS_BAS_NO_DUPLICATE);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

}
