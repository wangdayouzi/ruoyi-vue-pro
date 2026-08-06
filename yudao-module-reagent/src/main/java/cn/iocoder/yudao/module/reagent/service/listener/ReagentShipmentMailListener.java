package cn.iocoder.yudao.module.reagent.service.listener;

import cn.iocoder.yudao.module.system.api.mail.MailSendApi;
import cn.iocoder.yudao.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 发货完成邮件通知 — 挂载在 UserTask 的任务监听器上
 *
 * 在 BPMN 设计器中配置：
 *   选中 UserTask → 任务监听器 → 添加
 *   事件：complete
 *   委托表达式：${reagentShipmentMailListener}
 *
 * @author yudao
 */
@Component("reagentShipmentMailListener")
@Slf4j
public class ReagentShipmentMailListener implements TaskListener {

    private static final String TEMPLATE_CODE = "reagent-shipment-notify";

    @Resource
    private MailSendApi mailSendApi;

    @Override
    public void notify(DelegateTask delegateTask) {
        // 取流程发起人作为收件人
        Object initiator = delegateTask.getVariable("initiator");
        Long userId = null;
        if (initiator instanceof Long l) userId = l;
        else if (initiator instanceof Integer i) userId = i.longValue();
        else if (initiator instanceof String s) {
            try { userId = Long.valueOf(s); } catch (NumberFormatException ignored) {}
        }

        if (userId == null) {
            log.warn("[reagent-mail] 未找到流程发起人 initiator，跳过邮件发送");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        delegateTask.getVariables().forEach(params::put);

        try {
            mailSendApi.sendSingleMailToAdmin(new MailSendSingleToUserReqDTO()
                    .setUserId(userId)
                    .setTemplateCode(TEMPLATE_CODE)
                    .setTemplateParams(params));
            log.info("[reagent-mail] 发货通知已发送，收件人: {}", userId);
        } catch (Exception e) {
            log.error("[reagent-mail] 发送失败", e);
        }
    }

}
