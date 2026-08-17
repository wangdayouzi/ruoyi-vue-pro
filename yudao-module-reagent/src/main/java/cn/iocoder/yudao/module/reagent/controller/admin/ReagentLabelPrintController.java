package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintRespVO;
import cn.iocoder.yudao.module.reagent.service.ReagentLabelPrintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 试剂标签打印 Controller
 *
 * 数据来源：外部只读 SQL Server 数据源（PM 系统），仅按 BASID 查询；打印输出 Excel 键值对表格
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂标签打印")
@RestController
@RequestMapping("/reagent/label-print")
@Validated
public class ReagentLabelPrintController {

    @Resource
    private ReagentLabelPrintService reagentLabelPrintService;

    @GetMapping("/query")
    @Operation(summary = "根据 BASID 查询试剂标签信息（只读 SQL Server）")
    @Parameter(name = "basId", description = "试剂编号 BASID（必填）", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:label-print:query')")
    public CommonResult<List<ReagentLabelPrintRespVO>> getLabelPrint(@RequestParam("basId") @NotBlank(message = "BASID 不能为空") String basId) {
        return success(reagentLabelPrintService.getByBasId(basId));
    }

    @PostMapping("/print")
    @Operation(summary = "生成试剂标签打印 Excel（键值对表格）")
    @PreAuthorize("@ss.hasPermission('reagent:label-print:print')")
    public void printLabel(@Valid @RequestBody ReagentLabelPrintReqVO reqVO,
                           HttpServletResponse response) {
        reagentLabelPrintService.printLabel(reqVO, response);
    }

}
