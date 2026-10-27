package cn.iocoder.yudao.module.pms.controller.admin.kb.interaction;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeOfficePreviewService;
import cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share.PmsKnowledgeDocumentSharePublicRespVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share.PmsKnowledgeDocumentShareRespVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share.PmsKnowledgeDocumentShareSaveReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share.PmsKnowledgeDocumentShareUpdateMemberListReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.interaction.PmsKnowledgeDocumentShareDO;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeDocumentShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - PMS 知识库文档分享")
@RestController
@RequestMapping("/pms/kb/document-share")
@Validated
public class PmsKnowledgeDocumentShareController {

    @Resource
    private PmsKnowledgeDocumentShareService documentShareService;

    @Resource
    private PmsKnowledgeOfficePreviewService officePreviewService;

    @PostMapping("/open")
    @Operation(summary = "开启文档分享")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<PmsKnowledgeDocumentShareRespVO> openShare(
            @Valid @RequestBody PmsKnowledgeDocumentShareSaveReqVO reqVO) {
        PmsKnowledgeDocumentShareDO share = documentShareService.openShare(reqVO.getDocumentId(),
                reqVO.getShareUserIds(), reqVO.getExpireTime(), reqVO.getAccessCode(), getLoginUserId());
        return success(toShareVO(share));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分享成员、有效期和访问码")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<PmsKnowledgeDocumentShareRespVO> updateShare(
            @Valid @RequestBody PmsKnowledgeDocumentShareSaveReqVO reqVO) {
        PmsKnowledgeDocumentShareDO share = documentShareService.updateShare(reqVO.getDocumentId(),
                reqVO.getShareUserIds(), reqVO.getExpireTime(), reqVO.getAccessCode(),
                Boolean.TRUE.equals(reqVO.getRemoveAccessCode()), getLoginUserId());
        return success(toShareVO(share));
    }

    @PutMapping("/update-member-list")
    @Operation(summary = "更新文档内部分享成员")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> updateShareMemberList(
            @Valid @RequestBody PmsKnowledgeDocumentShareUpdateMemberListReqVO reqVO) {
        documentShareService.updateShareMemberList(reqVO.getDocumentId(), reqVO.getShareUserIds(), getLoginUserId());
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "关闭文档分享")
    @Parameter(name = "documentId", description = "文档编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> closeShare(@RequestParam("documentId") Long documentId) {
        documentShareService.closeShare(documentId, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文档分享配置")
    @Parameter(name = "documentId", description = "文档编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<PmsKnowledgeDocumentShareRespVO> getDocumentShare(@RequestParam("documentId") Long documentId) {
        PmsKnowledgeDocumentShareDO share = documentShareService.getActiveDocumentShare(documentId, getLoginUserId());
        return success(share == null ? null : toShareVO(share));
    }

    @GetMapping("/get-by-token")
    @Operation(summary = "通过分享令牌获得文档")
    @Parameter(name = "token", description = "分享令牌", required = true)
    @PermitAll
    @TenantIgnore
    public CommonResult<PmsKnowledgeDocumentSharePublicRespVO> getDocumentByToken(
            @RequestParam("token") @NotBlank(message = "分享令牌不能为空") String token,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-Knowledge-Share-Code", required = false) String accessCode) {
        PmsKnowledgeDocumentDO document = documentShareService.getDocumentByShareToken(token, accessCode);
        PmsKnowledgeDocumentSharePublicRespVO documentVO = BeanUtils.toBean(
                document, PmsKnowledgeDocumentSharePublicRespVO.class);
        if (PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())) {
            documentVO.setContent(null).setPreviewUrl(officePreviewService.isPreviewAvailable()
                    && officePreviewService.isPreviewSourceRegistered(document)
                    ? officePreviewService.buildSharePreviewUrl(document, getValidatedShare(document.getId(), token)) : null);
        }
        return success(documentVO);
    }

    @GetMapping("/office-preview-config-by-token")
    @Operation(summary = "通过分享令牌获得 Office 文件只读预览配置")
    @PermitAll
    @TenantIgnore
    public CommonResult<java.util.Map<String, Object>> getOfficePreviewConfigByToken(
            @RequestParam("token") @NotBlank String token,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-Knowledge-Share-Code", required = false) String accessCode) {
        PmsKnowledgeDocumentDO document = documentShareService.getDocumentByShareToken(token, accessCode);
        return success(officePreviewService.buildSharePreviewConfig(document,
                getValidatedShare(document.getId(), token)));
    }

    private PmsKnowledgeDocumentShareDO getValidatedShare(Long documentId, String token) {
        PmsKnowledgeDocumentShareDO share = documentShareService.getActiveDocumentShare(documentId);
        if (share == null || !token.equals(share.getToken())) {
            throw cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception(
                    cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_DOCUMENT_SHARE_INVALID);
        }
        return share;
    }

    private PmsKnowledgeDocumentShareRespVO toShareVO(PmsKnowledgeDocumentShareDO share) {
        return BeanUtils.toBean(share, PmsKnowledgeDocumentShareRespVO.class)
                .setAccessCodeRequired(share.getAccessCodeHash() != null);
    }

}
