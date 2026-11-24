package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.service.MatrixInboundService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Tag(name = "管理后台 - 基质入库登记")
@Validated
@RequestMapping("/reagent/matrix-inbound")
public class MatrixInboundController {
    @Resource private MatrixInboundService service;

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query')")
    public CommonResult<PageResult<MatrixInboundRespVO>> page(@Valid MatrixInboundPageReqVO filter) {
        return success(service.page(filter));
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query')")
    public CommonResult<MatrixInboundRespVO> get(@RequestParam @Positive Long id) {
        return success(service.get(id));
    }

    @GetMapping("/scope")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query')")
    public CommonResult<List<String>> scope() {
        return success(service.managedSites());
    }

    @GetMapping("/logs")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query')")
    public CommonResult<List<MatrixInboundLogRespVO>> logs(@RequestParam @Positive Long id) {
        return success(service.logs(id));
    }

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:create')")
    public CommonResult<MatrixInboundRespVO> create(@Valid @RequestBody MatrixInboundCreateReqVO input) {
        return success(service.create(input));
    }

    @PutMapping("/register")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:register')")
    public CommonResult<Boolean> register(@Valid @RequestBody MatrixInboundRegisterReqVO input) {
        service.register(input);
        return success(true);
    }

    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MatrixInboundUpdateReqVO input) {
        service.update(input);
        return success(true);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:delete')")
    public CommonResult<Boolean> delete(@RequestParam @Positive Long id, @RequestParam @PositiveOrZero Integer version) {
        service.delete(id, version);
        return success(true);
    }

    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query') and @ss.hasPermission('reagent:matrix-inbound:export')")
    public void export(HttpServletResponse response, @Valid MatrixInboundPageReqVO filter) throws IOException {
        service.export(response, filter);
    }

    @GetMapping("/export-bas")
    @PreAuthorize("@ss.hasPermission('reagent:matrix-inbound:query') and @ss.hasPermission('reagent:matrix-inbound:export')")
    public void exportBas(HttpServletResponse response, @Valid MatrixInboundPageReqVO filter) throws IOException {
        service.exportBas(response, filter);
    }
}
