package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseLotDO;
import cn.iocoder.yudao.module.reagent.service.ReagentBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 试剂基础数据 Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂基础数据")
@RestController
@RequestMapping("/reagent/base")
@Validated
public class ReagentBaseController {

    @Resource
    private ReagentBaseService reagentBaseService;

    // ==================== 试剂主表 ====================

    @GetMapping("/page")
    @Operation(summary = "获得试剂分页")
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<PageResult<ReagentBaseRespVO>> getReagentBasePage(@Valid ReagentBasePageReqVO pageReqVO) {
        PageResult<ReagentBaseDO> pageResult = reagentBaseService.getReagentBasePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReagentBaseRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得试剂简易列表（下拉选择用）")
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<List<ReagentBaseRespVO>> getReagentBaseSimpleList() {
        List<ReagentBaseRespVO> list = reagentBaseService.getReagentBaseSimpleList();
        return success(list);
    }

    @GetMapping("/get")
    @Operation(summary = "获得试剂详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<ReagentBaseRespVO> getReagentBase(@RequestParam("id") Long id) {
        ReagentBaseDO reagent = reagentBaseService.getReagentBase(id);
        return success(BeanUtils.toBean(reagent, ReagentBaseRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建试剂")
    @PreAuthorize("@ss.hasPermission('reagent:base:create')")
    public CommonResult<Long> createReagentBase(@Valid @RequestBody ReagentBaseSaveReqVO createReqVO) {
        Long id = reagentBaseService.createReagentBase(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改试剂")
    @PreAuthorize("@ss.hasPermission('reagent:base:update')")
    public CommonResult<Boolean> updateReagentBase(@Valid @RequestBody ReagentBaseSaveReqVO updateReqVO) {
        reagentBaseService.updateReagentBase(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试剂")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:base:delete')")
    public CommonResult<Boolean> deleteReagentBase(@RequestParam("id") Long id) {
        reagentBaseService.deleteReagentBase(id);
        return success(true);
    }

    // ==================== 试剂批号 ====================

    @GetMapping("/lot/list-by-base-id")
    @Operation(summary = "根据试剂ID获取批号列表")
    @Parameter(name = "baseId", description = "试剂主键", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<List<ReagentBaseLotRespVO>> getLotListByBaseId(@RequestParam("baseId") Long baseId) {
        List<ReagentBaseLotDO> list = reagentBaseService.getLotListByBaseId(baseId);
        return success(BeanUtils.toBean(list, ReagentBaseLotRespVO.class));
    }

    @GetMapping("/lot/list-by-bas-id")
    @Operation(summary = "根据试剂编号获取批号列表（申请时选择批号用）")
    @Parameter(name = "basId", description = "试剂编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<List<ReagentBaseLotRespVO>> getLotListByBasId(@RequestParam("basId") String basId) {
        List<ReagentBaseLotDO> list = reagentBaseService.getLotListByBasId(basId);
        return success(BeanUtils.toBean(list, ReagentBaseLotRespVO.class));
    }

    @PostMapping("/lot/create")
    @Operation(summary = "创建试剂批号")
    @PreAuthorize("@ss.hasPermission('reagent:base:create')")
    public CommonResult<Long> createReagentBaseLot(@Valid @RequestBody ReagentBaseLotSaveReqVO createReqVO) {
        Long id = reagentBaseService.createReagentBaseLot(createReqVO);
        return success(id);
    }

    @PutMapping("/lot/update")
    @Operation(summary = "修改试剂批号")
    @PreAuthorize("@ss.hasPermission('reagent:base:update')")
    public CommonResult<Boolean> updateReagentBaseLot(@Valid @RequestBody ReagentBaseLotSaveReqVO updateReqVO) {
        reagentBaseService.updateReagentBaseLot(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lot/delete")
    @Operation(summary = "删除试剂批号")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:base:delete')")
    public CommonResult<Boolean> deleteReagentBaseLot(@RequestParam("id") Long id) {
        reagentBaseService.deleteReagentBaseLot(id);
        return success(true);
    }

}
