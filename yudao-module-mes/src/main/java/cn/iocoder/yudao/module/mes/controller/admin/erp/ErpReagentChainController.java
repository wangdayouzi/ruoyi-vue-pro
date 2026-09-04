package cn.iocoder.yudao.module.mes.controller.admin.erp;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.mes.job.ErpReagentChainJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 试剂全链路同步（手动触发）
 *
 * <p>一条接口跑完整链路：erpSync（老库→staging）→ erpPush（staging→master）→ 试剂扁平同步（master→reagent_base_flat）。
 * 2 分钟内限一次（Redis），失败不占冷却。参数格式与链路任务一致：<code>[full:][窗口]关键词</code>，默认 <code>1:试剂</code>。
 *
 * @author yudao
 */
@Tag(name = "管理后台 - 试剂链路同步")
@RestController
@RequestMapping("/mes/erp-reagent")
@Validated
public class ErpReagentChainController {

    /** 同步冷却：2 分钟内最多一次 */
    private static final String SYNC_COOLDOWN_KEY = "reagent:flat:sync:cooldown";
    private static final long SYNC_COOLDOWN_SECONDS = 120L;
    /** 默认参数：回溯 1 天 + 试剂关键词（用户可自行改） */
    private static final String SYNC_DEFAULT_PARAM = "1:试剂";

    @Resource
    private ErpReagentChainJob erpReagentChainJob;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/sync")
    @Operation(summary = "手动触发试剂全链路同步（erpSync + erpPush + 扁平；2 分钟内限一次）")
    @PreAuthorize("@ss.hasPermission('reagent:base:update')")
    public CommonResult<String> sync(@RequestParam(value = "param", required = false) String param) throws Exception {
        // 限流：2 分钟内最多一次（防止重复/并发触发）
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(SYNC_COOLDOWN_KEY, "1", Duration.ofSeconds(SYNC_COOLDOWN_SECONDS));
        if (!Boolean.TRUE.equals(locked)) {
            return success("失败: 同步过于频繁，请 2 分钟后再试");
        }
        String p = StrUtil.isBlank(param) ? SYNC_DEFAULT_PARAM : param;
        try {
            return success(erpReagentChainJob.execute(p));
        } catch (Exception e) {
            // 失败不占冷却：删 key，允许立刻重试
            stringRedisTemplate.delete(SYNC_COOLDOWN_KEY);
            throw e;
        }
    }

}
