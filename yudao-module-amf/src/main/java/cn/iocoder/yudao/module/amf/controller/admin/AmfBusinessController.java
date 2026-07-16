package cn.iocoder.yudao.module.amf.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.amf.controller.admin.vo.*;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfBusinessDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileDO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileVersionDO;
import cn.iocoder.yudao.module.amf.service.AmfBusinessService;
import cn.iocoder.yudao.module.amf.service.AmfOnlyOfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 分析方法文件管理 - Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 分析方法文件管理")
@RestController
@RequestMapping("/amf/business")
@Validated
public class AmfBusinessController {

    @Resource
    private AmfBusinessService amfBusinessService;

    @Resource
    private AmfOnlyOfficeService amfOnlyOfficeService;

    // =================== 业务单据 CRUD ===================

    @PostMapping("/create")
    @Operation(summary = "创建分析方法文件")
    @PreAuthorize("@ss.hasPermission('amf:business:create')")
    public CommonResult<Long> createBusiness(@Valid @RequestBody AmfBusinessSaveReqVO createReqVO) {
        Long id = amfBusinessService.createBusiness(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新分析方法文件")
    @PreAuthorize("@ss.hasPermission('amf:business:update')")
    public CommonResult<Boolean> updateBusiness(@Valid @RequestBody AmfBusinessSaveReqVO updateReqVO) {
        amfBusinessService.updateBusiness(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分析方法文件")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('amf:business:delete')")
    public CommonResult<Boolean> deleteBusiness(@RequestParam("id") Long id) {
        amfBusinessService.deleteBusiness(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得分析方法文件")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('amf:business:query')")
    public CommonResult<AmfBusinessRespVO> getBusiness(@RequestParam("id") Long id) {
        AmfBusinessDO business = amfBusinessService.getBusiness(id);
        return success(BeanUtils.toBean(business, AmfBusinessRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得分析方法文件分页")
    @PreAuthorize("@ss.hasPermission('amf:business:query')")
    public CommonResult<PageResult<AmfBusinessRespVO>> getBusinessPage(@Valid AmfBusinessPageReqVO pageReqVO) {
        PageResult<AmfBusinessDO> pageResult = amfBusinessService.getBusinessPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AmfBusinessRespVO.class));
    }

    // =================== 文件版本管理 ===================

    @PostMapping("/upload")
    @Operation(summary = "上传文件（新版本）")
    @PreAuthorize("@ss.hasPermission('amf:business:upload')")
    public CommonResult<AmfFileVersionRespVO> uploadFile(
            @RequestParam("businessId") Long businessId,
            @RequestParam(value = "fileId", required = false) Long fileId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "changeDescription", required = false) String changeDescription) {
        AmfFileVersionDO version = amfBusinessService.uploadFile(businessId, fileId, file, changeDescription);
        return success(BeanUtils.toBean(version, AmfFileVersionRespVO.class));
    }

    // =================== 文件管理 ===================

    @GetMapping("/file-list")
    @Operation(summary = "获得文件列表")
    @Parameter(name = "businessId", description = "业务单据ID", required = true)
    @PreAuthorize("@ss.hasPermission('amf:business:query')")
    public CommonResult<List<AmfFileRespVO>> getFileList(@RequestParam("businessId") Long businessId) {
        List<AmfFileDO> list = amfBusinessService.getFileList(businessId);
        return success(BeanUtils.toBean(list, AmfFileRespVO.class));
    }

    @DeleteMapping("/file-delete")
    @Operation(summary = "删除文件及其所有版本")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @PreAuthorize("@ss.hasPermission('amf:business:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("fileId") Long fileId) {
        amfBusinessService.deleteFile(fileId);
        return success(true);
    }

    // =================== 版本管理 ===================

    @GetMapping("/version-list")
    @Operation(summary = "获得文件版本记录列表")
    @Parameter(name = "fileId", description = "文件ID", required = true)
    @PreAuthorize("@ss.hasPermission('amf:business:query')")
    public CommonResult<List<AmfFileVersionRespVO>> getFileVersionList(@RequestParam("fileId") Long fileId) {
        List<AmfFileVersionDO> list = amfBusinessService.getFileVersionList(fileId);
        return success(BeanUtils.toBean(list, AmfFileVersionRespVO.class));
    }

    @DeleteMapping("/version-delete")
    @Operation(summary = "删除文件版本记录")
    @Parameter(name = "versionId", description = "版本记录ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('amf:business:delete')")
    public CommonResult<Boolean> deleteFileVersion(@RequestParam("versionId") Long versionId) {
        amfBusinessService.deleteFileVersion(versionId);
        return success(true);
    }

    // =================== OnlyOffice 在线编辑/预览 ===================

    @GetMapping("/editor-config")
    @Operation(summary = "获取 OnlyOffice 编辑器配置（用于在线编辑/预览）")
    @Parameter(name = "versionId", description = "版本记录ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('amf:business:query')")
    public CommonResult<Map<String, Object>> getEditorConfig(
            @RequestParam("versionId") Long versionId,
            HttpServletRequest request) {
        // 从当前登录用户获取 userId 和 userName（简化处理，实际应从安全上下文获取）
        String userId = "1";
        String userName = "admin";
        Map<String, Object> config = amfOnlyOfficeService.buildEditorConfig(versionId, userId, userName);
        return success(config);
    }

}
