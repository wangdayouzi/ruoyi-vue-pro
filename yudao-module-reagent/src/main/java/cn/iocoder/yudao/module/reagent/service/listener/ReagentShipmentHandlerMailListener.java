package cn.iocoder.yudao.module.reagent.service.listener;

import cn.iocoder.yudao.module.reagent.service.ReagentMailSendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 发货任务创建时邮件通知发货处理人 — 挂载在"发货" UserTask 的任务监听器上
 *
 * 在 BPMN 设计器中配置：
 *   选中"发货" UserTask → 任务监听器 → 添加
 *   事件：create（任务创建时触发，即提交后进入发货环节）
 *   委托表达式：${reagentShipmentHandlerMailListener}
 *
 * 收件人来源：任务本身的办理人/候选人（流程节点上配置的人），无需单独维护人员名单。
 *   - 候选人策略 = 指定成员：取 {@link DelegateTask#getAssignee()}
 *   - 候选人策略 = 指定角色/部门/岗位：取 {@link DelegateTask#getCandidates()} 展开后的候选人
 *
 * @author yudao
 */
@Component("reagentShipmentHandlerMailListener")
@Slf4j
public class ReagentShipmentHandlerMailListener implements TaskListener {

    /**
     * 邮件模板编号，需在"系统管理 → 邮件管理 → 邮件模板"中配置
     */
    private static final String TEMPLATE_CODE = "reagent-shipment-notify-handler";

    @Resource
    private ReagentMailSendService reagentMailSendService;

    @Override
    public void notify(DelegateTask delegateTask) {
        // 仅"任务创建"事件发信：Flowable 创建任务且已指定办理人时会依次触发 create → assignment，
        // 若设计器把监听器同时挂在 create+assignment（或重复挂载）会导致一封申请发两封邮件。
        // 此处按业务本意"任务创建时通知"守卫，只允许 create 事件发送。
        if (!"create".equals(delegateTask.getEventName())) {
            log.info("[reagent-mail] 跳过非 create 事件（{}），避免重复通知. taskId={}", delegateTask.getEventName(), delegateTask.getId());
            return;
        }
        log.info("[reagent-mail] 发货任务创建，触发邮件通知. taskId={}, processInstanceId={}, taskName={}",
                delegateTask.getId(), delegateTask.getProcessInstanceId(), delegateTask.getName());

        // ========== 1. 收集收件人：办理人 + 候选人 ==========
        Set<Long> userIds = new LinkedHashSet<>();
        // 1.1 单办理人（候选人策略 = 指定成员）
        if (delegateTask.getAssignee() != null) {
            try {
                userIds.add(Long.valueOf(delegateTask.getAssignee()));
            } catch (NumberFormatException ignored) {
            }
        }
        // 1.2 候选人（策略 = 指定角色/部门/岗位时，会展开成候选人）
        for (IdentityLink link : delegateTask.getCandidates()) {
            if (link.getUserId() != null) {
                try {
                    userIds.add(Long.valueOf(link.getUserId()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (userIds.isEmpty()) {
            log.warn("[reagent-mail] 发货任务 {} 未解析到办理人/候选人，跳过邮件发送", delegateTask.getId());
            return;
        }
        log.info("[reagent-mail] 待通知的发货处理人列表 userIds={}", userIds);

        // ========== 2. 组装模板参数（仅业务白名单字段） ==========
        Map<String, Object> params = ReagentMailParamsHelper.buildTemplateParams(delegateTask);

        // ========== 3. 逐个发邮件（userId 自动加载对应 Admin 的邮箱） ==========
        for (Long userId : userIds) {
            try {
                reagentMailSendService.sendSingleMailToAdmin(userId, TEMPLATE_CODE, params);
                log.info("[reagent-mail] 发货通知邮件已发送，收件人: {}", userId);
            } catch (Exception e) {
                log.error("[reagent-mail] 发送失败，userId: {}", userId, e);
            }
        }
        log.info("[reagent-mail] 发货任务通知邮件全部处理完成，收件人列表 userIds={}", userIds);
    }

}
