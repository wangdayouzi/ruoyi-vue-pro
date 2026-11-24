package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.service.SampleUrgentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.service.SampleUrgentPeopleService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Tag(name="管理后台 - 样品加急入库")
@RestController
@RequestMapping("/reagent/sample-urgent")
@Validated
public class SampleUrgentController {
    @Resource private SampleUrgentService service;
    @Resource private SampleUrgentPeopleService people;
    @GetMapping("/selection-options")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<SampleUrgentPeopleService.Options> options() { return success(people.options()); }
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<PageResult<SampleUrgentRespVO>> page(@Valid SampleUrgentPageReqVO req) {
        return success(BeanUtils.toBean(service.page(req), SampleUrgentRespVO.class));
    }
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<SampleUrgentRespVO> get(@RequestParam Long id) {
        return success(BeanUtils.toBean(service.get(id), SampleUrgentRespVO.class));
    }
    @GetMapping("/logs")
    @PreAuthorize("@ss.hasPermission('reagent:sample-urgent:query')")
    public CommonResult<List<SampleUrgentLogRespVO>> logs(@RequestParam Long id) {
        return success(BeanUtils.toBean(service.logs(id), SampleUrgentLogRespVO.class));
    }
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
    public CommonResult<Boolean> delete(@RequestParam @Positive Long id,
            @RequestParam @Min(0) Integer revision) {
        service.delete(id,revision); return success(true);
    }
}
