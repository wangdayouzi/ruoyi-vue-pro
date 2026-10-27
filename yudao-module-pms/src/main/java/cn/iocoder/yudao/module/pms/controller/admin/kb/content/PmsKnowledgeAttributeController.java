package cn.iocoder.yudao.module.pms.controller.admin.kb.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeAttributeService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeContentPermissionService;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeDocumentService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/pms/kb/attribute")
public class PmsKnowledgeAttributeController {
    @Resource private PmsKnowledgeAttributeService service;
    @Resource private PmsKnowledgeDocumentService documentService;
    @Resource private PmsKnowledgeContentPermissionService permissionService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<PmsKnowledgeAttributeDO>> list(@RequestParam("libraryId") Long libraryId) {
        return success(service.list(libraryId, getLoginUserId()));
    }

    @PostMapping("/save-definition")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Long> saveDefinition(@RequestBody PmsKnowledgeAttributeDO definition) {
        return success(service.saveDefinition(definition, getLoginUserId()));
    }

    @PutMapping("/save-values")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Boolean> saveValues(@RequestParam("documentId") Long documentId,
                                            @RequestBody Map<String, String> values) {
        PmsKnowledgeDocumentDO document = documentService.getDocument(documentId, getLoginUserId());
        permissionService.validateContentPermissionWritable(document.getPermissionId(), document.getLibraryId(), getLoginUserId());
        service.saveValues(documentId, document.getLibraryId(), values);
        return success(true);
    }
}
