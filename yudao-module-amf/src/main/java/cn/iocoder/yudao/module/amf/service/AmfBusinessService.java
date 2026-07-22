package cn.iocoder.yudao.module.amf.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.amf.controller.admin.vo.AmfBusinessPageReqVO;
import cn.iocoder.yudao.module.amf.controller.admin.vo.AmfBusinessSaveReqVO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfBusinessDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileVersionDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * 分析方法文件 - 业务单据 Service 接口
 *
 * @author yudao
 */
public interface AmfBusinessService {

    /**
     * 创建分析方法文件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBusiness(AmfBusinessSaveReqVO createReqVO);

    /**
     * 更新分析方法文件
     *
     * @param updateReqVO 更新信息
     */
    void updateBusiness(AmfBusinessSaveReqVO updateReqVO);

    /**
     * 删除分析方法文件
     *
     * @param id 编号
     */
    void deleteBusiness(Long id);

    /**
     * 获得分析方法文件
     *
     * @param id 编号
     * @return 分析方法文件
     */
    AmfBusinessDO getBusiness(Long id);

    /**
     * 获得分析方法文件分页
     *
     * @param pageReqVO 分页查询
     * @return 分页结果
     */
    PageResult<AmfBusinessDO> getBusinessPage(AmfBusinessPageReqVO pageReqVO);

    /**
     * 上传文件（新版本）- 按文件名匹配已有文件记录
     *
     * @param businessId 业务单据ID
     * @param file 上传的文件
     * @param changeDescription 变更说明
     * @return 版本记录
     */
    AmfFileVersionDO uploadFile(Long businessId, MultipartFile file, String changeDescription);

    /**
     * 上传文件（新版本）- 指定文件记录ID，不按文件名匹配
     *
     * @param businessId 业务单据ID
     * @param fileId 已有的文件记录ID（为null时按文件名匹配）
     * @param file 上传的文件
     * @param versionNo 版本号（null时自动递增）
     * @param effectiveDate 签字生效日期
     * @param changeDescription 变更说明
     * @return 版本记录
     */
    AmfFileVersionDO uploadFile(Long businessId, Long fileId, MultipartFile file,
                                String versionNo, String effectiveDate, String changeDescription);

    /**
     * 获得指定业务单据的文件列表
     *
     * @param businessId 业务单据ID
     * @return 文件列表
     */
    List<AmfFileDO> getFileList(Long businessId);

    /**
     * 获得指定文件的版本记录列表
     *
     * @param fileId 文件ID
     * @return 版本记录列表
     */
    List<AmfFileVersionDO> getFileVersionList(Long fileId);

    /**
     * 删除指定文件及其所有版本
     *
     * @param fileId 文件ID
     */
    void deleteFile(Long fileId);

    /**
     * 删除指定版本记录
     *
     * @param versionId 版本记录ID
     */
    void deleteFileVersion(Long versionId);

}
