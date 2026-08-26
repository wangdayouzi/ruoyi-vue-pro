package cn.iocoder.yudao.module.mes.controller.admin.pm.po;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoRespVO;
import cn.iocoder.yudao.module.mes.service.pm.po.MesPmPoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 老ERP采购订单数据")
@RestController
@RequestMapping("/mes/pm-po")
@Validated
public class MesPmPoController {

    @Resource
    private MesPmPoService pmPoService;

    @GetMapping("/page")
    @Operation(summary = "获得老ERP采购订单数据分页")
    @PreAuthorize("@ss.hasPermission('mes:pm-po:query')")
    public CommonResult<PageResult<MesPmPoRespVO>> getPmPoPage(@Valid MesPmPoPageReqVO pageReqVO) {
        return success(pmPoService.getPoPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出老ERP采购订单数据 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pm-po:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPmPoExcel(@Valid MesPmPoPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        ExcelUtils.write(response, "采购订单数据.xls", "数据", MesPmPoRespVO.class,
                pmPoService.getPoPage(pageReqVO).getList());
    }

}
