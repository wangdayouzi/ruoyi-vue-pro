package cn.iocoder.yudao.module.system.controller.admin.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkMessageSendApi;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import cn.iocoder.yudao.module.system.enums.dingtalk.DingTalkMessageType;
import cn.iocoder.yudao.module.system.service.dingtalk.DingTalkMessageTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 钉钉消息模板")
@RestController
@RequestMapping("/system/dingtalk-template")
@RequiredArgsConstructor
public class DingTalkMessageTemplateController {
    private final DingTalkMessageTemplateService service;
    private final DingTalkMessageSendApi sender;
    private final cn.iocoder.yudao.module.system.service.dingtalk.DingTalkTemplateParameterService parameters;
    @GetMapping("/parameters")
    @PreAuthorize("@ss.hasAnyPermissions('system:dingtalk-template:query', 'system:dingtalk-template:create', 'system:dingtalk-template:update', 'system:dingtalk-template:send')")
    public CommonResult<List<cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider.Parameter>> parameters(@RequestParam String code) {
        return success(parameters.parameters(code));
    }
    @GetMapping("/department-options")
    @PreAuthorize("@ss.hasAnyPermissions('system:dingtalk-template:create', 'system:dingtalk-template:update', 'system:dingtalk-template:send')")
    public CommonResult<List<DingTalkMessageTemplateService.DepartmentOption>> departmentOptions() { return success(service.departmentOptions()); }
    @GetMapping("/receiver-options")
    @PreAuthorize("@ss.hasAnyPermissions('system:dingtalk-template:create', 'system:dingtalk-template:update', 'system:dingtalk-template:send')")
    public CommonResult<List<DingTalkMessageTemplateService.ReceiverOption>> receiverOptions() { return success(service.receiverOptions()); }
    @GetMapping("/types")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:query')")
    public CommonResult<List<DingTalkMessageType.Definition>> types() {
        return success(Arrays.stream(DingTalkMessageType.values()).map(DingTalkMessageType::definition).toList());
    }
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:query')")
    public CommonResult<PageResult<DingTalkMessageTemplateDO>> page(@Valid DingTalkTemplatePageReqVO req) { return success(service.page(req)); }
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:query')")
    public CommonResult<DingTalkMessageTemplateDO> get(@RequestParam Long id) { return success(service.require(id)); }
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:create')")
    public CommonResult<Long> create(@Valid @RequestBody DingTalkTemplateSaveReqVO req) { return success(service.save(req, false)); }
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody DingTalkTemplateSaveReqVO req) { service.save(req, true); return success(true); }
    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:delete')")
    public CommonResult<Boolean> delete(@RequestParam Long id) { service.delete(id); return success(true); }
    @PostMapping("/preview")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:query')")
    public CommonResult<Map<String, String>> preview(@Valid @RequestBody DingTalkTemplatePreviewReqVO req) { return success(service.preview(req)); }
    @PostMapping("/send")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-template:send')")
    public CommonResult<List<DingTalkMessageSendResultDTO>> send(@Valid @RequestBody DingTalkMessageSendReqDTO req) { return success(sender.send(req)); }
}
