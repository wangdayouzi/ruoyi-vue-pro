package cn.iocoder.yudao.module.infra.controller.admin.number;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberRuleDO;
import cn.iocoder.yudao.module.infra.service.number.NumberRuleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/infra/number-rule")
public class NumberRuleController {
    @Resource private NumberRuleService service;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('infra:number-rule:query')")
    public CommonResult<List<NumberRuleDO>> list() {
        return success(service.list());
    }

    @GetMapping("/enabled-list")
    @PreAuthorize("@ss.hasAnyPermissions('infra:number-rule:query', 'pms:kb:library:update', 'pms:kb:library:create')")
    public CommonResult<List<NumberRuleDO>> enabledList() {
        return success(service.list().stream().filter(rule -> Integer.valueOf(0).equals(rule.getStatus())).toList());
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('infra:number-rule:query')")
    public CommonResult<NumberRuleDO> get(@RequestParam("id") Long id) {
        return success(service.get(id));
    }

    @PostMapping("/save")
    @PreAuthorize("@ss.hasPermission('infra:number-rule:update')")
    public CommonResult<Long> save(@RequestBody NumberRuleDO rule) {
        return success(service.save(rule));
    }

}
