package cn.iocoder.yudao.module.pms.controller.admin.kb.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRecordDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeReviewRuleDO;
import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeReviewService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
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
    @Resource private AdminUserApi adminUserApi;

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
    public CommonResult<List<ReviewHistoryItem>> history(@RequestParam Long documentId) {
        List<PmsKnowledgeReviewRecordDO> records = service.history(documentId, getLoginUserId());
        Map<Long, AdminUserRespDTO> users = adminUserApi.getUserMap(records.stream()
                .map(PmsKnowledgeReviewRecordDO::getReviewerId).filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet()));
        return success(records.stream().map(record -> {
            AdminUserRespDTO user = users.get(record.getReviewerId());
            return new ReviewHistoryItem(record.getId(), record.getDocumentId(), record.getResult(),
                    record.getReviewDate() == null ? "" : record.getReviewDate().toString(),
                    record.getRemark(), record.getReviewerId(),
                    user == null ? "未知用户" : user.getNickname());
        }).toList());
    }

    public record ReviewHistoryItem(Long id, Long documentId, String result, String reviewDate,
                                    String remark, Long reviewerId, String reviewerName) {}

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
