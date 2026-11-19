package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import cn.iocoder.yudao.module.reagent.service.SampleUrgentPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/reagent/sample-urgent/plan")
@Validated
@RequiredArgsConstructor
public class SampleUrgentPlanController {
    private final SampleUrgentPlanService service;
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<PageResult<SampleUrgentPlanDO>> page(@Valid PageParam req) { return success(service.page(req)); }
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:create')")
    public CommonResult<Long> create(@Valid @RequestBody SampleUrgentPlanSaveReqVO req) { return success(service.create(req)); }
    @PutMapping("/action")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:update')")
    public CommonResult<Boolean> action(@Valid @RequestBody SampleUrgentPlanActionReqVO req) {
        service.action(req); return success(true);
    }
    @PutMapping("/schedule")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:update')")
    public CommonResult<Boolean> schedule(@Valid @RequestBody SampleUrgentPlanScheduleReqVO req) {
        service.schedule(req); return success(true);
    }
}
