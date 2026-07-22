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
        // 校验方法编号唯一性
        validateMethodNoUnique(null, createReqVO.getMethodNo());

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
        // 校验方法编号唯一性
        validateMethodNoUnique(updateReqVO.getId(), updateReqVO.getMethodNo());

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
        return uploadFile(businessId, null, file, null, null, changeDescription);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AmfFileVersionDO uploadFile(Long businessId, Long fileId, MultipartFile file,
                                        String versionNo, String effectiveDate, String changeDescription) {
        validateBusinessExists(businessId);

        // 存储文件
        String fileUrl = amfFileStorageService.storeFile(file, businessId);
        String originalName = file.getOriginalFilename();

        AmfFileDO fileDO;
        if (fileId != null) {
            // 指定了文件记录ID：上传新版本
            fileDO = amfFileMapper.selectById(fileId);
            if (fileDO == null || !fileDO.getBusinessId().equals(businessId)) {
                throw exception(AMF_FILE_NOT_EXISTS);
            }
            fileDO.setFileName(originalName);
        } else {
            // 未指定文件记录ID：新建文件记录
            fileDO = new AmfFileDO();
            fileDO.setBusinessId(businessId);
            fileDO.setFileName(originalName);
            fileDO.setFileVersion("0");
            fileDO.setCreateTime(LocalDateTime.now());
            amfFileMapper.insert(fileDO);
        }

        // 新建文件初始版本号为空字符串
        if (fileId == null) {
            fileDO.setFileVersion("");
        }

        // 版本号：手动填写或默认"1"
        // 版本号必须填写
        if (versionNo == null || versionNo.isEmpty()) {
            throw exception(AMF_FILE_VERSION_EMPTY);
        }
        String newVersion = versionNo;

        // 版本号唯一性校验（上传新版本时）
        if (fileId != null) {
            List<AmfFileVersionDO> existingVersions = amfFileVersionMapper.selectListByFileId(fileId);
            boolean duplicate = existingVersions.stream().anyMatch(v -> newVersion.equals(v.getVersionNo()));
            if (duplicate) {
                throw exception(AMF_FILE_VERSION_DUPLICATE);
            }
        }

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

        // 更新文件记录的当前版本和签字生效日期
        fileDO.setFileUrl(fileUrl);
        fileDO.setFileVersion(newVersion);
        if (effectiveDate != null && !effectiveDate.isEmpty()) {
            fileDO.setEffectiveDate(java.time.LocalDate.parse(effectiveDate));
        }
        amfFileMapper.updateById(fileDO);

        // 更新母记录签字生效日期（取所有文件中最新的）
        if (effectiveDate != null && !effectiveDate.isEmpty()) {
            AmfBusinessDO business = amfBusinessMapper.selectById(businessId);
            if (business != null) {
                java.time.LocalDate newDate = java.time.LocalDate.parse(effectiveDate);
                if (business.getEffectiveDate() == null || newDate.isAfter(business.getEffectiveDate())) {
                    business.setEffectiveDate(newDate);
                    amfBusinessMapper.updateById(business);
                }
            }
        }

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

    private void validateMethodNoUnique(Long id, String methodNo) {
        AmfBusinessDO existing = amfBusinessMapper.selectByMethodNo(methodNo);
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
