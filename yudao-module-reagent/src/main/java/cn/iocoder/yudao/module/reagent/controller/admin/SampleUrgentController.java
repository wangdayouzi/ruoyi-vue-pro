package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.service.SampleUrgentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Tag(name="管理后台 - 样品加急入库")
@RestController
@RequestMapping("/reagent/sample-urgent")
@Validated
public class SampleUrgentController {
    @Resource private SampleUrgentService service;
    @Resource private cn.iocoder.yudao.module.reagent.service.SampleUrgentPeopleService people;
    @GetMapping("/selection-options")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<cn.iocoder.yudao.module.reagent.service.SampleUrgentPeopleService.Options> options() { return success(people.options()); }
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<PageResult<SampleUrgentDO>> page(@Valid SampleUrgentPageReqVO req) { return success(service.page(req)); }
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<SampleUrgentDO> get(@RequestParam Long id) { return success(service.get(id)); }
    @GetMapping("/logs")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<List<SampleUrgentLogDO>> logs(@RequestParam Long id) { return success(service.logs(id)); }
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:create')")
    public CommonResult<Long> create(@Valid @RequestBody SampleUrgentSaveReqVO req) {
        if(req.getId()!=null) throw invalidParamException("新登记需求不能指定 ID");
        return success(service.save(req));
    }
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:update')")
    public CommonResult<Long> update(@Valid @RequestBody SampleUrgentSaveReqVO req) {
        if(req.getId()==null || req.getRevision()==null) throw invalidParamException("缺少需求 ID 或版本号");
        return success(service.save(req));
    }
    @PutMapping("/action")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<Boolean> action(@Valid @RequestBody SampleUrgentActionReqVO req) {
        service.action(req); return success(true);
    }
    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:update')")
    public CommonResult<Boolean> delete(@RequestParam @jakarta.validation.constraints.Positive Long id,
            @RequestParam @jakarta.validation.constraints.Min(0) Integer revision) {
        service.delete(id,revision); return success(true);
    }
}
