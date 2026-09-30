package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import cn.iocoder.yudao.module.reagent.service.SampleLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 领用台账")
@RestController
@RequestMapping("/reagent/sample-loan")
@Validated
public class SampleLoanController {

    @Resource
    private SampleLoanService sampleLoanService;

    @PostMapping("/create")
    @Operation(summary = "登记领用")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Long> createSampleLoan(@Valid @RequestBody SampleLoanCreateReqVO createReqVO) {
        return success(sampleLoanService.createSampleLoan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改领用记录")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:update')")
    public CommonResult<Boolean> updateSampleLoan(@Valid @RequestBody SampleLoanUpdateReqVO updateReqVO) {
        sampleLoanService.updateSampleLoan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领用记录")
    @Parameter(name = "id", description = "领用记录 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:delete')")
    public CommonResult<Boolean> deleteSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.deleteSampleLoan(id);
        return success(true);
    }

    @GetMapping("/check-duplicate")
    @Operation(summary = "检查领用记录是否重复")
    @PreAuthorize("@ss.hasAnyPermissions('reagent:sample-loan:create', 'reagent:sample-loan:update')")
    public CommonResult<Boolean> checkDuplicate(
            @RequestParam("basNo") String basNo,
            @RequestParam("location") String location,
            @RequestParam("requesterId") Long requesterId,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        return success(sampleLoanService.hasBorrowingSampleLoan(basNo, location, requesterId, excludeId));
    }

    @PutMapping("/return")
    @Operation(summary = "归还领用")
    @Parameter(name = "id", description = "台账 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:return')")
    public CommonResult<Boolean> returnSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.returnSampleLoan(id);
        return success(true);
    }

    @PutMapping("/no-return")
    @Operation(summary = "登记无需归还")
    @Parameter(name = "id", description = "台账 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasAnyPermissions('reagent:sample-loan:no-return', 'reagent:sample-loan:return')")
    public CommonResult<Boolean> noReturnSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.noReturnSampleLoan(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得领用台账分页")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:query')")
    public CommonResult<PageResult<SampleLoanRespVO>> getSampleLoanPage(@Valid SampleLoanPageReqVO pageReqVO) {
        PageResult<SampleLoanDO> pageResult = sampleLoanService.getSampleLoanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SampleLoanRespVO.class));
    }

    @GetMapping("/screen-list")
    @Operation(summary = "获得大屏中的领用中样品")
    @PermitAll
    public CommonResult<List<SampleLoanRespVO>> getBorrowingSampleLoanList() {
        return success(BeanUtils.toBean(sampleLoanService.getBorrowingSampleLoanList(), SampleLoanRespVO.class));
    }
}
