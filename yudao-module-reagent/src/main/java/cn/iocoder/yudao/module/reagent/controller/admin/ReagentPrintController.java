package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.module.reagent.service.ReagentPrintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 试剂打印 Controller
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂打印")
@RestController
@RequestMapping("/reagent/print")
@Validated
public class ReagentPrintController {

    @Resource
    private ReagentPrintService reagentPrintService;

    @GetMapping("/shipment")
    @Operation(summary = "打印单个发货单")
    @Parameter(name = "id", description = "发货单ID", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:shipment:query')")
    public void printShipment(@RequestParam("id") Long id,
                              HttpServletResponse response) {
        reagentPrintService.printShipment(id, response);
    }

    @PostMapping("/shipments")
    @Operation(summary = "批量打印发货单（多选 → 合并为一张交接单，汇总试剂明细）")
    @Parameter(name = "ids", description = "发货单ID集合（需同属一个申请单）", required = true)
    @PreAuthorize("@ss.hasPermission('reagent:shipment:query')")
    public void printShipments(@RequestBody Set<Long> ids,
                               HttpServletResponse response) {
        reagentPrintService.printShipmentsMerged(ids, response);
    }
}
