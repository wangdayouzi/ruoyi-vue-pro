package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemRespVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;
import cn.iocoder.yudao.module.reagent.service.ReagentItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 项目号（老样本系统同步）Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 项目号")
@RestController
@RequestMapping("/reagent/item")
@Validated
public class ReagentItemController {

    @Resource
    private ReagentItemService reagentItemService;

    @GetMapping("/page")
    @Operation(summary = "获得项目号分页")
    @PreAuthorize("@ss.hasPermission('reagent:item:query')")
    public CommonResult<PageResult<ReagentItemRespVO>> getItemPage(@Valid ReagentItemPageReqVO reqVO) {
        PageResult<ReagentItemDO> pageResult = reagentItemService.getItemPage(reqVO);
        return success(BeanUtils.toBean(pageResult, ReagentItemRespVO.class));
    }

}
