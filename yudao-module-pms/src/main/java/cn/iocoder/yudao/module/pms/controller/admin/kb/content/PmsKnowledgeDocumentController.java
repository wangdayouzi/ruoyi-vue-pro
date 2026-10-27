package cn.iocoder.yudao.module.pms.controller.admin.kb.content;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentCreateReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentDraftSaveReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentMoveReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentRespVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentSearchPageReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentUpdateReqVO;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentVersionRespVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.interaction.PmsKnowledgeDocumentLikeDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.library.PmsKnowledgeLibraryDO;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeContentLevelEnum;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeContentPermissionService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeDocumentService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeAttributeService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeOfficePreviewService;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeDocumentLikeService;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeFavoriteService;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeViewRecordService;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - PMS 知识库文档")
@RestController
@RequestMapping("/pms/kb/document")
@Validated
public class PmsKnowledgeDocumentController {

    @Resource
    private PmsKnowledgeDocumentService documentService;
    @Resource
    private PmsKnowledgeAttributeService attributeService;
    @Resource
    private PmsKnowledgeOfficePreviewService officePreviewService;
    @Resource
    private PmsKnowledgeContentPermissionService contentPermissionService;
    @Resource
    private PmsKnowledgeFavoriteService favoriteService;
    @Resource
    private PmsKnowledgeDocumentLikeService documentLikeService;
    @Resource
    private PmsKnowledgeViewRecordService viewRecordService;
    @Resource
    private PmsKnowledgeLibraryService libraryService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建文档")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Long> createDocument(@Valid @RequestBody PmsKnowledgeDocumentCreateReqVO createReqVO) {
        return success(documentService.createDocument(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档内容")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> updateDocument(@Valid @RequestBody PmsKnowledgeDocumentUpdateReqVO updateReqVO) {
        documentService.updateDocument(updateReqVO, getLoginUserId());
        return success(true);
    }

    @GetMapping("/draft-get")
    @Operation(summary = "获取当前用户的文档草稿")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDraftDO> getDraft(
            @RequestParam("documentId") Long documentId) {
        return success(documentService.getDraft(documentId, getLoginUserId()));
    }

    @PutMapping("/draft-save")
    @Operation(summary = "自动保存文档草稿，不影响已发布版本")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> saveDraft(@Valid @RequestBody PmsKnowledgeDocumentDraftSaveReqVO reqVO) {
        documentService.saveDraft(reqVO, getLoginUserId());
        return success(true);
    }

    @PutMapping("/draft-publish")
    @Operation(summary = "发布当前用户的文档草稿")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> publishDraft(@RequestParam("documentId") Long documentId) {
        documentService.publishDraft(documentId, getLoginUserId());
        return success(true);
    }

    @GetMapping("/version-list")
    @Operation(summary = "获得文档历史版本")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<PmsKnowledgeDocumentVersionRespVO>> getDocumentVersionList(@RequestParam("documentId") Long documentId) {
        List<PmsKnowledgeDocumentVersionDO> versions = documentService.getDocumentVersionList(documentId, getLoginUserId());
        return success(convertList(versions, version -> BeanUtils.toBean(version, PmsKnowledgeDocumentVersionRespVO.class)
                .setContent(null)));
    }

    @GetMapping("/version-get")
    @Operation(summary = "获得文档历史版本详情")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<PmsKnowledgeDocumentVersionRespVO> getDocumentVersion(
            @RequestParam("documentId") Long documentId, @RequestParam("versionNo") Long versionNo) {
        Long userId = getLoginUserId();
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, userId);
        PmsKnowledgeDocumentVersionDO version = documentService.getDocumentVersion(documentId, versionNo, userId);
        PmsKnowledgeDocumentVersionRespVO result = BeanUtils.toBean(version, PmsKnowledgeDocumentVersionRespVO.class);
        // 文件版本地址仅对有下载权限的用户返回；预览由单独的受控接口提供。
        if (PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(version.getType())
                && !PmsKnowledgeContentLevelEnum.canDownload(contentPermissionService.getCurrentUserContentPermissionLevel(
                document.getPermissionId(), document.getLibraryId(), userId))) {
            result.setContent(null);
        }
        return success(result);
    }

    @PutMapping("/version-restore")
    @Operation(summary = "恢复文档历史版本，生成一个新版本")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> restoreDocumentVersion(@RequestParam("documentId") Long documentId,
            @RequestParam("versionNo") Long versionNo, @RequestParam("expectedVersionNo") Long expectedVersionNo) {
        documentService.restoreDocumentVersion(documentId, versionNo, expectedVersionNo, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "将文档移入回收站")
    @Parameter(name = "id", description = "文档编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:delete')")
    public CommonResult<Boolean> deleteDocument(@RequestParam("id") Long id) {
        documentService.deleteDocument(id, getLoginUserId());
        return success(true);
    }

    @PutMapping("/move")
    @Operation(summary = "移动文档")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> moveDocument(@Valid @RequestBody PmsKnowledgeDocumentMoveReqVO moveReqVO) {
        documentService.moveDocument(moveReqVO, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文档详情")
    @Parameter(name = "id", description = "文档编号", required = true, example = "1024")
    @Parameter(name = "view", description = "是否记录本次浏览", example = "true")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<PmsKnowledgeDocumentRespVO> getDocument(
            @RequestParam("id") Long id,
            @RequestParam(value = "view", defaultValue = "false") Boolean view) {
        // 查询文档
        Long userId = getLoginUserId();
        PmsKnowledgeDocumentDO document = documentService.getDocument(id, userId);
        // 记录浏览
        if (Boolean.TRUE.equals(view)) {
            viewRecordService.createViewRecord(document.getLibraryId(), document.getType(), document.getId(), userId);
        }
        // 构建文档 VO
        return success(buildDocumentRespVO(document, userId));
    }

    @GetMapping("/office-preview-config")
    @Operation(summary = "获得 Office 文件只读预览配置")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<Map<String, Object>> getOfficePreviewConfig(@RequestParam("documentId") Long documentId) {
        return success(officePreviewService.buildPreviewConfig(documentId, getLoginUserId()));
    }

    @GetMapping("/office-history-preview-config")
    @Operation(summary = "获得历史 Office 文件只读预览配置")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<Map<String, Object>> getOfficeHistoryPreviewConfig(
            @RequestParam("documentId") Long documentId, @RequestParam("versionNo") Long versionNo) {
        return success(officePreviewService.buildHistoryPreviewConfig(documentId, versionNo, getLoginUserId()));
    }

    @GetMapping("/history-preview-url")
    @Operation(summary = "获得历史文件短时预览地址")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<String> getHistoryPreviewUrl(
            @RequestParam("documentId") Long documentId, @RequestParam("versionNo") Long versionNo) {
        return success(officePreviewService.buildHistoryPreviewUrl(documentId, versionNo, getLoginUserId()));
    }

    @GetMapping("/preview-source")
    @Operation(summary = "短时授权的文件预览数据")
    @PermitAll
    @TenantIgnore
    public ResponseEntity<byte[]> getPreviewSource(@RequestParam("ticket") String ticket) throws Exception {
        PmsKnowledgeDocumentDO document = officePreviewService.getPreviewSourceDocument(ticket);
        String fileType = document.getFileType() == null ? "bin" : document.getFileType();
        MediaType mediaType;
        try {
            mediaType = fileType.contains("/") ? MediaType.parseMediaType(fileType)
                    : MediaTypeFactory.getMediaType("file." + fileType)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);
        } catch (IllegalArgumentException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok().header("Cache-Control", "no-store, private").contentType(mediaType).body(officePreviewService.getPreviewSource(ticket));
    }

    @GetMapping("/search-page")
    @Operation(summary = "搜索已发布文档标题、正文及附件正文")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<PageResult<PmsKnowledgeDocumentRespVO>> getDocumentSearchPage(
            @Valid PmsKnowledgeDocumentSearchPageReqVO pageReqVO) {
        PageResult<PmsKnowledgeDocumentDO> pageResult = documentService.getDocumentSearchPage(
                pageReqVO, getLoginUserId());
        return success(new PageResult<>(buildDocumentRespVOList(pageResult.getList(), pageReqVO.getKeyword()), pageResult.getTotal()));
    }

    // ==================== 拼接 VO ====================

    private PmsKnowledgeDocumentRespVO buildDocumentRespVO(PmsKnowledgeDocumentDO document, Long userId) {
        // 1.1 拼接当前用户的内容权限和互动状态
        Integer currentUserLevel = contentPermissionService.getCurrentUserContentPermissionLevel(
                document.getPermissionId(), document.getLibraryId(), userId);
        Long creatorUserId = NumberUtils.parseLong(document.getCreator());
        PmsKnowledgeDocumentRespVO documentVO = BeanUtils.toBean(document, PmsKnowledgeDocumentRespVO.class)
                .setCreatorUserId(creatorUserId).setCurrentUserLevel(currentUserLevel)
                .setDownloadStatus(PmsKnowledgeContentLevelEnum.canDownload(currentUserLevel))
                .setPreviewUrl(PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())
                        && officePreviewService.isPreviewAvailable()
                        && officePreviewService.isPreviewSourceRegistered(document)
                        ? officePreviewService.buildPreviewUrlForDocument(document) : null)
                .setFavoriteStatus(favoriteService.isFavorite(document.getType(), document.getId(), userId));
        documentVO.setAttributes(attributeService.values(document.getId(), document.getLibraryId()));
        documentVO.setCarriedAttributes(attributeService.carriedValues(document.getId(), document.getLibraryId()));
        // 1.2 文件不可下载时，隐藏实际文件地址
        if (PmsKnowledgeDocumentTypeEnum.FILE.getType().equals(document.getType())
                && Boolean.FALSE.equals(documentVO.getDownloadStatus())) {
            documentVO.setContent(null);
        }

        // 2. 一次查询并拼接创建人和点赞用户信息
        List<PmsKnowledgeDocumentLikeDO> likes = documentLikeService.getDocumentLikeList(document.getId());
        Set<Long> relatedUserIds = convertSet(likes, PmsKnowledgeDocumentLikeDO::getUserId);
        relatedUserIds.add(creatorUserId);
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(relatedUserIds);
        findAndThen(userMap, creatorUserId, user -> documentVO.setCreatorUserName(user.getNickname()));
        documentVO.setLikeStatus(CollUtil.anyMatch(likes, like -> userId.equals(like.getUserId())))
                .setLikeUsers(convertList(likes, like -> {
                    PmsKnowledgeDocumentRespVO.LikeUser likeUser = new PmsKnowledgeDocumentRespVO.LikeUser()
                            .setId(like.getUserId());
                    findAndThen(userMap, like.getUserId(), user ->
                            likeUser.setNickname(user.getNickname()).setAvatar(user.getAvatar()));
                    return likeUser;
                }));
        return documentVO;
    }

    private List<PmsKnowledgeDocumentRespVO> buildDocumentRespVOList(List<PmsKnowledgeDocumentDO> documents, String keyword) {
        if (CollUtil.isEmpty(documents)) {
            return Collections.emptyList();
        }
        Map<Long, PmsKnowledgeLibraryDO> libraryMap = libraryService.getLibraryMap(
                convertSet(documents, PmsKnowledgeDocumentDO::getLibraryId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertSet(documents,
                document -> NumberUtils.parseLong(document.getCreator())));
        return convertList(documents, document -> {
            PmsKnowledgeDocumentRespVO documentVO = BeanUtils.toBean(document, PmsKnowledgeDocumentRespVO.class)
                    .setContent(null).setContentSummary(buildContentSummary(document.getSearchText(), keyword))
                    .setCreatorUserId(NumberUtils.parseLong(document.getCreator()));
            findAndThen(libraryMap, document.getLibraryId(),
                    library -> documentVO.setLibraryName(library.getName()));
            findAndThen(userMap, documentVO.getCreatorUserId(),
                    user -> documentVO.setCreatorUserName(user.getNickname()));
            return documentVO;
        });
    }

    /**
     * 构造搜索结果摘要，避免直接返回完整正文
     */
    private String buildContentSummary(String content, String keyword) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        String summary = content.replaceAll("\\s+", " ").trim();
        int position = keyword == null || keyword.isBlank() ? -1
                : summary.toLowerCase(java.util.Locale.ROOT).indexOf(keyword.toLowerCase(java.util.Locale.ROOT));
        int start = position < 0 ? 0 : Math.max(0, position - 40);
        int end = Math.min(summary.length(), start + 160);
        return (start > 0 ? "…" : "") + summary.substring(start, end) + (end < summary.length() ? "…" : "");
    }

}
