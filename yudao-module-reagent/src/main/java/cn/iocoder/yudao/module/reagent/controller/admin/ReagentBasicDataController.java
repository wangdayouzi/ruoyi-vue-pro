package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalOptionRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemOptionRespVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;
import cn.iocoder.yudao.module.reagent.service.ReagentBasicDataSyncService;
import cn.iocoder.yudao.module.reagent.service.ReagentClinicalService;
import cn.iocoder.yudao.module.reagent.service.ReagentItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 临床方案 / 项目号 基础数据 Controller
 *
 * <p>提供同步触发，以及加急入库、领用台账等业务页面的下拉选项（支持模糊搜索）。
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂基础数据")
@RestController
@RequestMapping("/reagent/basic-data")
@Validated
public class ReagentBasicDataController {

    @Resource
    private ReagentBasicDataSyncService reagentBasicDataSyncService;

    @Resource
    private ReagentClinicalService reagentClinicalService;

    @Resource
    private ReagentItemService reagentItemService;

    @PostMapping("/sync")
    @Operation(summary = "手动同步临床方案与项目号（全量）")
    @PreAuthorize("@ss.hasPermission('reagent:basic-data:sync')")
    public CommonResult<String> sync() {
        return success(reagentBasicDataSyncService.sync());
    }

    @GetMapping("/clinical-options")
    @Operation(summary = "临床方案下拉选项（名称/编号模糊搜索）")
    @PreAuthorize("@ss.hasAnyPermissions('reagent:basic-data:select','reagent:clinical:query','reagent:sample-urgent:query')")
    public CommonResult<List<ReagentClinicalOptionRespVO>> getClinicalOptions(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<ReagentClinicalDO> list = reagentClinicalService.listOptions(keyword);
        return success(BeanUtils.toBean(list, ReagentClinicalOptionRespVO.class));
    }

    @GetMapping("/item-options")
    @Operation(summary = "项目号/BAS下拉选项（项目号/方案名称/方案编号模糊搜索）")
    @PreAuthorize("@ss.hasAnyPermissions('reagent:basic-data:select','reagent:item:query',"
            + "'reagent:sample-urgent:query','reagent:sample-loan:query')")
    public CommonResult<List<ReagentItemOptionRespVO>> getItemOptions(
            @RequestParam(value = "clinicalId", required = false) Long clinicalId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<ReagentItemDO> list = reagentItemService.listOptions(clinicalId, keyword);
        return success(BeanUtils.toBean(list, ReagentItemOptionRespVO.class));
    }

}
