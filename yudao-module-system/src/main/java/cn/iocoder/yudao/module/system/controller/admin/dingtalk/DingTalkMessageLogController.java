package cn.iocoder.yudao.module.system.controller.admin.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkLogPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageLogDO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.DingTalkMessageLogMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Tag(name = "管理后台 - 钉钉消息记录")
@RestController
@RequestMapping("/system/dingtalk-log")
@RequiredArgsConstructor
public class DingTalkMessageLogController {
    private final DingTalkMessageLogMapper mapper;
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-log:query')")
    public CommonResult<PageResult<DingTalkMessageLogDO>> page(@Valid DingTalkLogPageReqVO req) { return success(mapper.selectPage(req)); }
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('system:dingtalk-log:query')")
    public CommonResult<DingTalkMessageLogDO> get(@RequestParam Long id) {
        var log = mapper.selectById(id);
        if (log == null) throw invalidParamException("钉钉发送记录不存在");
        return success(log);
    }
}
