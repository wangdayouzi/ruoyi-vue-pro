package cn.iocoder.yudao.module.reagent.service.listener;

import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.bpm.framework.flowable.core.enums.BpmnVariableConstants;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentApplyMapper;
import cn.iocoder.yudao.module.reagent.service.ReagentMailSendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

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
    private ReagentMailSendService reagentMailSendService;

    @Resource
    private ReagentApplyMapper reagentApplyMapper;

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("[reagent-mail] 发货节点完成，触发邮件通知. taskId={}, processInstanceId={}",
                delegateTask.getId(), delegateTask.getProcessInstanceId());

        // 仅当任务为"审批通过"时才发"发货完成"邮件；拒绝/退回完成时跳过，避免误发
        Integer taskStatus = (Integer) delegateTask.getVariable(BpmnVariableConstants.TASK_VARIABLE_STATUS);
        if (BpmTaskStatusEnum.isRejectStatus(taskStatus)) {
            log.info("[reagent-mail] 任务 {} 完成状态为 {}，为拒绝/退回，跳过发货完成邮件", delegateTask.getId(), taskStatus);
            return;
        }

        // 通过流程实例 ID 查申请单，取创建人（=申请人/发起人）作为收件人，不依赖 initiator 流程变量
        ReagentApplyDO apply = reagentApplyMapper.selectByProcessInstanceId(delegateTask.getProcessInstanceId());
        if (apply == null) {
            log.warn("[reagent-mail] 根据流程实例 {} 未找到申请单，跳过邮件发送", delegateTask.getProcessInstanceId());
            return;
        }
        Long userId = NumberUtil.parseLong(apply.getCreator(), null);
        if (userId == null) {
            log.warn("[reagent-mail] 申请单 {} 创建人无效 creator={}，跳过邮件发送", apply.getApplyNo(), apply.getCreator());
            return;
        }
        log.info("[reagent-mail] 待通知的收件人列表 userIds={}", userId);

        Map<String, Object> params = ReagentMailParamsHelper.buildTemplateParams(delegateTask);

        try {
            reagentMailSendService.sendSingleMailToAdmin(userId, TEMPLATE_CODE, params);
            log.info("[reagent-mail] 发货通知已发送，收件人: {}", userId);
        } catch (Exception e) {
            log.error("[reagent-mail] 发送失败，userId: {}", userId, e);
        }
    }

}
