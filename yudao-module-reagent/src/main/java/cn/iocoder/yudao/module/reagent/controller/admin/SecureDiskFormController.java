package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import java.util.Map;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SecureDiskFormModels.*;
import cn.iocoder.yudao.module.reagent.service.securedisk.SecureDiskFormService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Tag(name = "管理后台 - 安全盘申请")
@RequestMapping("/form/secure-disk")
@RequiredArgsConstructor
@Validated
public class SecureDiskFormController {
    private final SecureDiskFormService service;

    @GetMapping("/rules")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<?> rules() {
        return success(service.rules());
    }

    @GetMapping("/settings")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<?> settings() {
        return success(service.settings());
    }

    @PostMapping("/rule")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<Boolean> saveRule(@Valid @RequestBody Rule rule) {
        return success(service.saveRule(rule));
    }

    @DeleteMapping("/rule")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<Boolean> deleteRule(@RequestParam @Positive long id) {
        return success(service.deleteRule(id));
    }

    @GetMapping("/identity")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<?> identity() {
        return success(service.identity());
    }

    @GetMapping("/account-people")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<?> findAccountPeople(@RequestParam(defaultValue = "") @Size(max = 80) String query) {
        return success(service.findAccountPeople(query));
    }

    @PostMapping("/account")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<Boolean> bindAccount(@Valid @RequestBody Account input) {
        return success(service.bindAccount(input));
    }

    @PostMapping("/template")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:manage')")
    public CommonResult<Boolean> upload(@RequestParam("file") MultipartFile file) {
        return success(service.upload(file));
    }

    @PostMapping("/submit")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<IssuedForm> submit(@Valid @RequestBody Application input) throws Exception {
        return success(service.submit(input));
    }

    @GetMapping("/confirmation-preview")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:issue')")
    public CommonResult<ConfirmationPreview> confirmationPreview() {
        return success(service.confirmationPreview());
    }

    @PostMapping("/confirm")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:issue')")
    public CommonResult<IssuedForm> confirm(@Valid @RequestBody ConfirmationRequest input) {
        return success(service.confirm(input));
    }

    @PostMapping("/submit-draft")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<Boolean> submitDraft(@RequestParam @Positive long id) {
        return success(service.submitDraft(id));
    }

    @PostMapping("/generate-pdf")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<Boolean> generatePdf(@RequestParam @Positive long id) throws Exception {
        return success(service.generatePdf(id));
    }

    @PostMapping("/void-own")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<Boolean> voidOwn(@Valid @RequestBody VoidInput input) {
        return success(service.voidOwn(input));
    }

    @PostMapping("/void-issued")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:issue')")
    public CommonResult<Boolean> voidIssued(@Valid @RequestBody VoidInput input) {
        return success(service.voidIssued(input));
    }

    @GetMapping("/mine")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public CommonResult<PageResult<Map<String, Object>>> mine(@Valid ApplicationPageReq request) {
        return success(service.pageApplications(request, true));
    }

    @GetMapping("/issued")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:issue')")
    public CommonResult<PageResult<Map<String, Object>>> issued(@Valid ApplicationPageReq request) {
        return success(service.pageApplications(request, false));
    }

    @GetMapping("/issue-export")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:issue')")
    public void exportIssues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {
        IssueExport export = service.exportIssues(startDate, endDate);
        ExcelUtils.write(response, "安全盘操作日志-" + startDate + "_" + endDate + ".xlsx",
                "操作日志", export.columns(), export.rows());
    }

    @GetMapping("/pdf")
    @PreAuthorize("@ss.hasPermission('form:secure-disk:apply')")
    public ResponseEntity<byte[]> pdf(@RequestParam long id) {
        var file = service.pdf(id);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.filename())
                .body(file.content());
    }
}
