package cn.iocoder.yudao.module.system.controller.admin.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkQueuePageReqVO;
import cn.iocoder.yudao.module.system.service.dingtalk.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/system/dingtalk-notification")
@RequiredArgsConstructor
@Validated
public class DingTalkNotificationController {
    private final DingTalkNotificationManageService service;
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-notification:query')")
    public CommonResult<PageResult<DingTalkNotificationManageService.Notice>> page(@Valid DingTalkQueuePageReqVO req) { return success(service.page(req)); }
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-notification:query')")
    public CommonResult<DingTalkNotificationManageService.Notice> get(@RequestParam long id) { return success(service.get(id)); }
    public record Operation(@Positive long id,@NotBlank @Size(max=500) String reason,boolean confirmedNotReceived) {}
    @PostMapping("/cancel")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-notification:manage')")
    public CommonResult<Boolean> cancel(@Valid @RequestBody Operation req) { service.cancel(req.id(),getLoginUserId(),req.reason()); return success(true); }
    @PostMapping("/resend")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-notification:manage')")
    public CommonResult<Boolean> resend(@Valid @RequestBody Operation req) { service.resend(req.id(),getLoginUserId(),req.reason(),req.confirmedNotReceived()); return success(true); }
}
