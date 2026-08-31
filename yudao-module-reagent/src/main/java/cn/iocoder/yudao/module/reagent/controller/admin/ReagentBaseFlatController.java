package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSaveReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSimplePageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentReceiptReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;
import cn.iocoder.yudao.module.reagent.service.ReagentBaseFlatService;
import cn.iocoder.yudao.module.reagent.service.ReagentReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 老ERP同步 试剂基础数据(扁平) Controller
 *
 * <p>数据来自老ERP采购入库单同步（一行=一批），展示 + 可维护字段编辑。
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂基础数据(老ERP同步)")
@RestController
@RequestMapping("/reagent/base-flat")
@Validated
public class ReagentBaseFlatController {

    @Resource
    private ReagentBaseFlatService reagentBaseFlatService;
    @Resource
    private ReagentReceiptService reagentReceiptService;

    @GetMapping("/page")
    @Operation(summary = "获得试剂基础数据(扁平)分页")
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<PageResult<ReagentBaseFlatRespVO>> getReagentBaseFlatPage(@Valid ReagentBaseFlatPageReqVO pageReqVO) {
        PageResult<ReagentBaseFlatDO> pageResult = reagentBaseFlatService.getReagentBaseFlatPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReagentBaseFlatRespVO.class));
    }

    @PutMapping("/update")
    @Operation(summary = "修改试剂基础数据(扁平)可维护字段")
    @PreAuthorize("@ss.hasPermission('reagent:base:update')")
    public CommonResult<Boolean> updateReagentBaseFlat(@Valid @RequestBody ReagentBaseFlatSaveReqVO updateReqVO) {
        reagentBaseFlatService.updateReagentBaseFlat(updateReqVO);
        return success(true);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "试剂基础数据(扁平)简易分页（申请单选批号用，关键词搜索）")
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public CommonResult<PageResult<ReagentBaseFlatRespVO>> getReagentBaseFlatSimpleList(
            @Valid ReagentBaseFlatSimplePageReqVO reqVO) {
        PageResult<ReagentBaseFlatDO> pageResult = reagentBaseFlatService.getReagentBaseFlatSimplePage(reqVO);
        return success(BeanUtils.toBean(pageResult, ReagentBaseFlatRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试剂基础数据(扁平)")
    @PreAuthorize("@ss.hasPermission('reagent:base:update')")
    public CommonResult<Boolean> deleteReagentBaseFlat(@RequestParam("id") Long id) {
        reagentBaseFlatService.deleteReagentBaseFlat(id);
        return success(true);
    }

    @PostMapping("/generate-receipt")
    @Operation(summary = "生成生物试剂接收单（填充试剂信息，下载 docx）")
    @PreAuthorize("@ss.hasPermission('reagent:base:query')")
    public void generateReceipt(@Valid @RequestBody ReagentReceiptReqVO reqVO, HttpServletResponse response) throws Exception {
        reagentReceiptService.generateReceipt(reqVO, response);
    }

}
