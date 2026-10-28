package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanOptionRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfUpdateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import cn.iocoder.yudao.module.reagent.service.SampleLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @GetMapping("/sites")
    @PermitAll
    @Operation(summary = "获取可领用站点")
    public CommonResult<List<SampleLoanOptionRespVO>> getSites() {
        return success(sampleLoanService.getSiteOptions());
    }

    @GetMapping("/locations")
    @PermitAll
    @Operation(summary = "获取站点可用楼层")
    public CommonResult<List<SampleLoanOptionRespVO>> getLocations(@RequestParam("site") String site) {
        return success(sampleLoanService.getLocationOptions(site));
    }

    @PostMapping("/create")
    @Operation(summary = "登记领用")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Long> createSampleLoan(@Valid @RequestBody SampleLoanCreateReqVO createReqVO) {
        return success(sampleLoanService.createSampleLoan(createReqVO));
    }

    @PostMapping("/create-self")
    @Operation(summary = "移动端登记本人领用")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<SampleLoanRespVO> createSelfSampleLoan(@Valid @RequestBody SampleLoanSelfCreateReqVO createReqVO) {
        return success(BeanUtils.toBean(sampleLoanService.createSelfSampleLoan(createReqVO), SampleLoanRespVO.class));
    }

    @GetMapping("/page-self")
    @Operation(summary = "移动端获取本人领用记录")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<PageResult<SampleLoanRespVO>> getSelfSampleLoanPage(@Valid SampleLoanPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(sampleLoanService.getSelfSampleLoanPage(pageReqVO), SampleLoanRespVO.class));
    }

    @GetMapping("/page-mobile")
    @Operation(summary = "移动端获得领用记录分页")
    @PreAuthorize("@ss.hasAnyPermissions('reagent:sample-loan:create', 'reagent:sample-loan:query')")
    public CommonResult<PageResult<SampleLoanRespVO>> getMobileSampleLoanPage(@Valid SampleLoanPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(sampleLoanService.getSampleLoanPage(pageReqVO), SampleLoanRespVO.class));
    }

    @PutMapping("/update-self")
    @Operation(summary = "移动端修改本人领用记录")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Boolean> updateSelfSampleLoan(@Valid @RequestBody SampleLoanSelfUpdateReqVO updateReqVO) {
        sampleLoanService.updateSelfSampleLoan(updateReqVO);
        return success(true);
    }

    @PutMapping("/return-self")
    @Operation(summary = "移动端归还本人领用记录")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Boolean> returnSelfSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.returnSelfSampleLoan(id);
        return success(true);
    }

    @PutMapping("/no-return-self")
    @Operation(summary = "移动端确认本人领用记录无需归还")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Boolean> noReturnSelfSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.noReturnSelfSampleLoan(id);
        return success(true);
    }

    @DeleteMapping("/delete-self")
    @Operation(summary = "移动端删除本人领用记录")
    @PreAuthorize("@ss.hasPermission('reagent:sample-loan:create')")
    public CommonResult<Boolean> deleteSelfSampleLoan(@RequestParam("id") Long id) {
        sampleLoanService.deleteSelfSampleLoan(id);
        return success(true);
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
            @RequestParam(value = "site", required = false) String site,
            @RequestParam("location") String location,
            @RequestParam("requesterId") Long requesterId,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        return success(sampleLoanService.hasBorrowingSampleLoan(basNo, site, location, requesterId, excludeId));
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
    @Operation(summary = "获取宁波大屏可领用记录")
    @PermitAll
    public CommonResult<List<SampleLoanRespVO>> getBorrowingSampleLoanList(
            @RequestParam("location") @NotBlank String location) {
        return success(BeanUtils.toBean(sampleLoanService.getBorrowingSampleLoanList(location), SampleLoanRespVO.class));
    }
}
