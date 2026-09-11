package cn.iocoder.yudao.module.reagent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrintJobDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrinterDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelTemplateDO;
import cn.iocoder.yudao.module.reagent.service.ReagentLabelPrintJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/** 用户提交打印任务及 Windows 代理领取任务的接口。 */
@Tag(name = "管理后台 - 试剂标签打印任务")
@RestController
@RequestMapping("/reagent/label-print")
@Validated
public class ReagentLabelPrintJobController {

    private static final String AGENT_TOKEN_HEADER = "X-Label-Agent-Token";

    @Resource
    private ReagentLabelPrintJobService jobService;

    @GetMapping("/printers")
    @Operation(summary = "获得可选试剂标签打印机")
    @PreAuthorize("@ss.hasPermission('reagent:label-print:query')")
    public CommonResult<List<ReagentLabelPrinterSimpleRespVO>> getPrinters() {
        List<ReagentLabelPrinterDO> printers = jobService.getEnabledPrinters();
        return success(BeanUtils.toBean(printers, ReagentLabelPrinterSimpleRespVO.class));
    }

    @GetMapping("/templates")
    @Operation(summary = "获得可选试剂标签模板")
    @PreAuthorize("@ss.hasPermission('reagent:label-print:query')")
    public CommonResult<List<ReagentLabelTemplateSimpleRespVO>> getTemplates() {
        List<ReagentLabelTemplateDO> templates = jobService.getEnabledTemplates();
        return success(BeanUtils.toBean(templates, ReagentLabelTemplateSimpleRespVO.class));
    }

    @PostMapping("/jobs")
    @Operation(summary = "创建试剂标签打印任务")
    @PreAuthorize("@ss.hasPermission('reagent:label-print:print')")
    public CommonResult<Long> createJob(@Valid @RequestBody ReagentLabelPrintJobCreateReqVO reqVO) {
        return success(jobService.createJob(reqVO));
    }

    @GetMapping("/jobs/get")
    @Operation(summary = "获得试剂标签打印任务")
    @PreAuthorize("@ss.hasPermission('reagent:label-print:query')")
    public CommonResult<ReagentLabelPrintJobRespVO> getJob(@RequestParam("id") Long id) {
        ReagentLabelPrintJobDO job = jobService.getJob(id);
        return success(BeanUtils.toBean(job, ReagentLabelPrintJobRespVO.class));
    }

    @GetMapping("/jobs/recent")
    @Operation(summary = "获得最近试剂标签打印历史")
    public CommonResult<List<ReagentLabelPrintJobRespVO>> getRecentJobs() {
        return success(BeanUtils.toBean(jobService.getRecentJobs(), ReagentLabelPrintJobRespVO.class));
    }

    /**
     * 代理请求使用机器凭证自行鉴权；该接口 PermitAll 仅绕过用户登录，绝不代表匿名可执行。
     */
    @PermitAll
    @PostMapping("/agent/heartbeat")
    @Operation(summary = "标签代理心跳")
    @ApiAccessLog(enable = false) // 高频探活，不写入 API 访问日志
    public CommonResult<Boolean> heartbeat(@RequestParam String agentCode,
                                           @RequestHeader(AGENT_TOKEN_HEADER) String agentToken) {
        jobService.heartbeat(agentCode, agentToken);
        return success(true);
    }

    @PermitAll
    @PostMapping("/agent/recover")
    @Operation(summary = "标签代理启动后的未完成任务恢复")
    public CommonResult<Boolean> recover(@RequestParam String agentCode,
                                         @RequestHeader(AGENT_TOKEN_HEADER) String agentToken) {
        jobService.recoverAfterRestart(agentCode, agentToken);
        return success(true);
    }

    @PermitAll
    @PostMapping("/agent/claim")
    @Operation(summary = "标签代理领取下一条打印任务")
    @ApiAccessLog(enable = false) // 高频轮询领取任务，不写入 API 访问日志
    public CommonResult<ReagentLabelAgentJobRespVO> claim(@RequestParam String agentCode,
                                                           @RequestHeader(AGENT_TOKEN_HEADER) String agentToken) {
        return success(jobService.claimNext(agentCode, agentToken));
    }

    @PermitAll
    @PostMapping("/agent/result")
    @Operation(summary = "标签代理回传打印结果")
    public CommonResult<Boolean> result(@RequestParam String agentCode,
                                        @RequestHeader(AGENT_TOKEN_HEADER) String agentToken,
                                        @Valid @RequestBody ReagentLabelAgentResultReqVO reqVO) {
        jobService.reportResult(agentCode, agentToken, reqVO);
        return success(true);
    }
}
