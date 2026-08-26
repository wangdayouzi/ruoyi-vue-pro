package cn.iocoder.yudao.module.mes.job;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.mes.service.wm.erp.ErpPushService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 老 ERP → MES 推送定时任务（阶段2）
 *
 * 触发：基础设施-定时任务 新增任务，Handler 名称选 erpPushJob，参数 param 填"每类单据本次处理行数"（默认 1000，0=不限）。
 * 建议：先手动执行；如要自动请放低峰期。先跑 erpSyncJob 拉 staging，再跑本任务推送。
 *
 * @author yudao
 */
@Component
@Slf4j
public class ErpPushJob implements JobHandler {

    @Resource
    private ErpPushService erpPushService;

    @Override
    public String execute(String param) throws Exception {
        int limit = 1000;
        if (StrUtil.isNotBlank(param)) {
            try {
                limit = Integer.parseInt(param.trim());
            } catch (NumberFormatException e) {
                log.warn("[ErpPushJob] param 非法，使用默认 1000: {}", param);
            }
        }
        log.info("[ErpPushJob] 触发，limit={}", limit);
        return erpPushService.push(limit);
    }

}
