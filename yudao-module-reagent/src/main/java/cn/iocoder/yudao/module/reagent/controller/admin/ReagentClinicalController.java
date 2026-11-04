package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;
import cn.iocoder.yudao.module.reagent.service.ReagentClinicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 临床方案（老样本系统同步）Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 临床方案")
@RestController
@RequestMapping("/reagent/clinical")
@Validated
public class ReagentClinicalController {

    @Resource
    private ReagentClinicalService reagentClinicalService;

    @GetMapping("/page")
    @Operation(summary = "获得临床方案分页")
    @PreAuthorize("@ss.hasPermission('reagent:clinical:query')")
    public CommonResult<PageResult<ReagentClinicalRespVO>> getClinicalPage(
            @Valid ReagentClinicalPageReqVO reqVO) {
        PageResult<ReagentClinicalDO> pageResult = reagentClinicalService.getClinicalPage(reqVO);
        return success(BeanUtils.toBean(pageResult, ReagentClinicalRespVO.class));
    }

    @PutMapping("/update")
    @Operation(summary = "修改临床方案本地字段（是否自动发送寄送通知邮件）")
    @PreAuthorize("@ss.hasPermission('reagent:clinical:update')")
    public CommonResult<Boolean> updateClinicalLocalField(@Valid @RequestBody ReagentClinicalUpdateReqVO reqVO) {
        reagentClinicalService.updateClinicalLocalField(reqVO);
        return success(true);
    }

}
