package cn.iocoder.yudao.module.pms.controller.admin.kb.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRecordDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRuleDO;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeReviewService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/pms/kb/review")
public class PmsKnowledgeReviewController {
    @Resource private PmsKnowledgeReviewService service;

    @GetMapping("/rules")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<PmsKnowledgeReviewRuleDO>> rules(@RequestParam Long libraryId) {
        return success(service.listRules(libraryId, getLoginUserId()));
    }

    @PostMapping("/rule")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Long> saveRule(@RequestBody PmsKnowledgeReviewRuleDO rule) {
        return success(service.saveRule(rule, getLoginUserId()));
    }

    @GetMapping("/due")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<Map<String, Object>>> due(@RequestParam Long libraryId) {
        return success(service.dueList(libraryId, getLoginUserId()));
    }

    @GetMapping("/workbench")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<Map<String, Object>>> workbench(@RequestParam(required = false) Long libraryId) {
        return success(service.workbench(libraryId, getLoginUserId()));
    }

    @GetMapping("/history")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<List<PmsKnowledgeReviewRecordDO>> history(@RequestParam Long documentId) {
        return success(service.history(documentId, getLoginUserId()));
    }

    @GetMapping("/summary")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:query')")
    public CommonResult<Map<String, Object>> summary(@RequestParam Long documentId) {
        return success(service.summary(documentId, getLoginUserId()));
    }

    @PostMapping("/submit")
    @PreAuthorize("@ss.hasPermission('pms:kb:library:update')")
    public CommonResult<Long> submit(@RequestBody SubmitRequest request) {
        return success(service.submit(request.documentId(), request.result(), request.remark(), getLoginUserId()));
    }

    public record SubmitRequest(Long documentId, String result, String remark) {}
}
