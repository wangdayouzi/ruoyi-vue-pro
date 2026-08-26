package cn.iocoder.yudao.module.mes.controller.admin.pm.inbound;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundRespVO;
import cn.iocoder.yudao.module.mes.service.pm.inbound.MesPmInboundService;
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

@Tag(name = "管理后台 - 老ERP采购入库单明细")
@RestController
@RequestMapping("/mes/pm-inbound")
@Validated
public class MesPmInboundController {

    @Resource
    private MesPmInboundService pmInboundService;

    @GetMapping("/page")
    @Operation(summary = "获得老ERP采购入库单明细分页")
    @PreAuthorize("@ss.hasPermission('mes:pm-inbound:query')")
    public CommonResult<PageResult<MesPmInboundRespVO>> getPmInboundPage(@Valid MesPmInboundPageReqVO pageReqVO) {
        return success(pmInboundService.getDetailPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出老ERP采购入库单明细 Excel")
    @PreAuthorize("@ss.hasPermission('mes:pm-inbound:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPmInboundExcel(@Valid MesPmInboundPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        ExcelUtils.write(response, "采购入库单明细.xls", "数据", MesPmInboundRespVO.class,
                pmInboundService.getDetailPage(pageReqVO).getList());
    }

}
