package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentItemDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentShipmentItemMapper;
import cn.iocoder.yudao.module.reagent.service.ReagentShipmentService;
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
 * 发货单 Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 发货单")
@RestController
@RequestMapping("/reagent/shipment")
@Validated
public class ReagentShipmentController {

    @Resource
    private ReagentShipmentService reagentShipmentService;

    @Resource
    private ReagentShipmentItemMapper reagentShipmentItemMapper;

    @GetMapping("/page")
    @Operation(summary = "获得发货单分页")
    @PreAuthorize("@ss.hasPermission('reagent:shipment:query')")
    public CommonResult<PageResult<ReagentShipmentRespVO>> getShipmentPage(@Valid ReagentShipmentPageReqVO pageReqVO) {
        PageResult<ReagentShipmentDO> pageResult = reagentShipmentService.getShipmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReagentShipmentRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得发货单详情（含明细）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:shipment:query')")
    public CommonResult<ReagentShipmentRespVO> getShipment(@RequestParam("id") Long id) {
        ReagentShipmentDO shipment = reagentShipmentService.getShipment(id);
        ReagentShipmentRespVO respVO = BeanUtils.toBean(shipment, ReagentShipmentRespVO.class);
        List<ReagentShipmentItemDO> items = reagentShipmentItemMapper.selectListByShipmentId(id);
        respVO.setItems(BeanUtils.toBean(items, ReagentShipmentItemVO.class));
        return success(respVO);
    }

    @GetMapping("/list-by-apply-id")
    @Operation(summary = "根据申请单ID获取发货单列表")
    @Parameter(name = "applyId", description = "申请单ID", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:apply:query')")
    public CommonResult<List<ReagentShipmentRespVO>> getShipmentListByApplyId(@RequestParam("applyId") Long applyId) {
        List<ReagentShipmentDO> list = reagentShipmentService.getShipmentListByApplyId(applyId);
        return success(BeanUtils.toBean(list, ReagentShipmentRespVO.class));
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认发货（样品组操作）")
    @PreAuthorize("@ss.hasPermission('reagent:shipment:create')")
    public CommonResult<Long> confirmShipment(@Valid @RequestBody ReagentShipmentSaveReqVO reqVO) {
        Long id = reagentShipmentService.confirmShipment(reqVO);
        return success(id);
    }

    @PostMapping("/revoke")
    @Operation(summary = "撤回发货")
    @Parameter(name = "id", description = "发货单ID", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:shipment:create')")
    public CommonResult<Boolean> revokeShipment(@RequestParam("id") Long id) {
        reagentShipmentService.revokeShipment(id);
        return success(true);
    }

}
