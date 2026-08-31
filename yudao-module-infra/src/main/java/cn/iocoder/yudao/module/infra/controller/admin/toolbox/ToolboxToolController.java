package cn.iocoder.yudao.module.infra.controller.admin.toolbox;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolSaveReqVO;
import cn.iocoder.yudao.module.infra.convert.toolbox.ToolboxToolConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.toolbox.ToolboxToolDO;
import cn.iocoder.yudao.module.infra.service.toolbox.ToolboxToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IT 工具箱")
@RestController
@RequestMapping("/infra/toolbox-tool")
@Validated
public class ToolboxToolController {

    @Resource
    private ToolboxToolService toolboxToolService;

    @PostMapping("/create")
    @Operation(summary = "创建工具")
    @PreAuthorize("@ss.hasPermission('infra:toolbox-tool:create')")
    public CommonResult<Long> createToolboxTool(@Valid @RequestBody ToolboxToolSaveReqVO createReqVO) {
        return success(toolboxToolService.createToolboxTool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工具")
    @PreAuthorize("@ss.hasPermission('infra:toolbox-tool:update')")
    public CommonResult<Boolean> updateToolboxTool(@Valid @RequestBody ToolboxToolSaveReqVO updateReqVO) {
        toolboxToolService.updateToolboxTool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工具")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:toolbox-tool:delete')")
    public CommonResult<Boolean> deleteToolboxTool(@RequestParam("id") Long id) {
        toolboxToolService.deleteToolboxTool(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工具")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('infra:toolbox-tool:query')")
    public CommonResult<ToolboxToolRespVO> getToolboxTool(@RequestParam("id") Long id) {
        return success(ToolboxToolConvert.INSTANCE.convert(toolboxToolService.getToolboxTool(id)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工具分页")
    @PreAuthorize("@ss.hasPermission('infra:toolbox-tool:query')")
    public CommonResult<PageResult<ToolboxToolRespVO>> getToolboxToolPage(@Valid ToolboxToolPageReqVO pageReqVO) {
        return success(ToolboxToolConvert.INSTANCE.convertPage(toolboxToolService.getToolboxToolPage(pageReqVO)));
    }

    @GetMapping("/list-enabled")
    @Operation(summary = "获得启用中的工具列表（首页展示用）")
    public CommonResult<List<ToolboxToolRespVO>> getEnabledToolboxToolList() {
        return success(ToolboxToolConvert.INSTANCE.convertList(toolboxToolService.getEnabledToolboxToolList()));
    }

    @PostMapping("/record-download")
    @Operation(summary = "记录下载次数 +1")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<Boolean> recordToolboxToolDownload(@RequestParam("id") Long id) {
        toolboxToolService.recordToolboxToolDownload(id);
        return success(true);
    }

}
