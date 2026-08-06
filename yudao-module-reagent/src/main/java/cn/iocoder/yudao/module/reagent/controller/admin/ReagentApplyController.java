package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyItemDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentApplyItemMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseLotMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseMapper;
import cn.iocoder.yudao.module.reagent.service.ReagentApplyService;
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
 * 试剂申请单 Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂申请单")
@RestController
@RequestMapping("/reagent/apply")
@Validated
public class ReagentApplyController {

    @Resource
    private ReagentApplyService reagentApplyService;

    @Resource
    private ReagentApplyItemMapper reagentApplyItemMapper;

    @Resource
    private ReagentBaseMapper reagentBaseMapper;

    @Resource
    private ReagentBaseLotMapper reagentBaseLotMapper;

    @GetMapping("/page")
    @Operation(summary = "获得申请单分页")
    @PreAuthorize("@ss.hasPermission('reagent:apply:query')")
    public CommonResult<PageResult<ReagentApplyRespVO>> getApplyPage(@Valid ReagentApplyPageReqVO pageReqVO) {
        PageResult<ReagentApplyDO> pageResult = reagentApplyService.getApplyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReagentApplyRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得申请单详情（含明细）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:apply:query')")
    public CommonResult<ReagentApplyRespVO> getApply(@RequestParam("id") Long id) {
        ReagentApplyDO apply = reagentApplyService.getApply(id);
        ReagentApplyRespVO respVO = BeanUtils.toBean(apply, ReagentApplyRespVO.class);
        // 填充明细 & 联查试剂基础数据补 storageTemp、storageLocation、content
        List<ReagentApplyItemDO> items = reagentApplyItemMapper.selectListByApplyId(id);
        List<ReagentApplyItemVO> itemVOs = BeanUtils.toBean(items, ReagentApplyItemVO.class);
        for (ReagentApplyItemVO vo : itemVOs) {
            if (vo.getBasId() != null) {
                var base = reagentBaseMapper.selectByBasId(vo.getBasId());
                if (base != null) {
                    vo.setStorageTemp(base.getStorageTemp());
                    vo.setStorageLocation(base.getStorageLocation());
                    // 联查批号获取 content（复用上面的 base，避免重复查询）
                    if (vo.getLotNo() != null) {
                        var lot = reagentBaseLotMapper.selectByBaseIdAndLotNo(base.getId(), vo.getLotNo());
                        if (lot != null) {
                            vo.setContent(lot.getContent());
                        }
                    }
                }
            }
        }
        respVO.setItems(itemVOs);
        return success(respVO);
    }

    @PostMapping("/create")
    @Operation(summary = "创建申请单（项目组提单）")
    @PreAuthorize("@ss.hasPermission('reagent:apply:create')")
    public CommonResult<Long> createApply(@Valid @RequestBody ReagentApplySaveReqVO createReqVO) {
        Long id = reagentApplyService.createApply(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改申请单（仅草稿/退回状态可编辑）")
    @PreAuthorize("@ss.hasPermission('reagent:apply:update')")
    public CommonResult<Boolean> updateApply(@Valid @RequestBody ReagentApplySaveReqVO updateReqVO) {
        reagentApplyService.updateApply(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:apply:delete')")
    public CommonResult<Boolean> deleteApply(@RequestParam("id") Long id) {
        reagentApplyService.deleteApply(id);
        return success(true);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交申请单（草稿 → 待发货，启动工作流）")
    @Parameter(name = "id", description = "申请单ID", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:apply:update')")
    public CommonResult<Boolean> submitApply(@RequestParam("id") Long id) {
        reagentApplyService.submitApply(id);
        return success(true);
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝/退回申请单（样品组操作）")
    @PreAuthorize("@ss.hasPermission('reagent:apply:update')")
    public CommonResult<Boolean> rejectApply(@Valid @RequestBody ReagentApplyRejectReqVO reqVO) {
        reagentApplyService.rejectApply(reqVO);
        return success(true);
    }

}
