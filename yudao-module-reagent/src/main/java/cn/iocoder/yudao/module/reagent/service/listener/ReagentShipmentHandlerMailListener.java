package cn.iocoder.yudao.module.reagent.service.listener;

import cn.iocoder.yudao.module.reagent.service.ReagentMailSendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发货任务创建时邮件通知发货处理人 — 挂载在"发货" UserTask 的任务监听器上
 *
 * 在 BPMN 设计器中配置：
 *   选中"发货" UserTask → 任务监听器 → 添加
 *   事件：create（任务创建时触发，即提交后进入发货环节）
 *   委托表达式：${reagentShipmentHandlerMailListener}
 *
 * 收件人：按申请单发货方区域（上海/宁波）写死的两个发货处理人邮箱，不再按任务候选人群发。
 *
 * 注意：create 事件在 processInstanceId 落库前触发，绝不能按流程实例 ID 回表查申请单（会查不到而漏发）；
 * 发货区域改从提交申请时写入的流程变量 region 读取。
 *
 * 另：发货节点配置为会签多实例（样品组每个成员一个并行审核任务、任一通过即完成），
 * 每个成员任务 create 都会触发本监听器，故需去重，保证每张申请单只发一封。
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

    /** 上海 / 宁波 发货处理人邮箱（写死） */
    private static final String MAIL_SHANGHAI = "jhsh_sample@accurantbio.com";
//    private static final String MAIL_NINGBO = "xnnb_sample@accurantbio.com";
    private static final String MAIL_NINGBO = "dayou.wang@accurantbio.com";

    /** 发送打标流程变量：同流程实例只发一封（会签/异常重试兜底） */
    private static final String VAR_MAIL_SENT = "reagentShipmentHandlerMailSent";

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

        // 会签多实例去重：样品组每个成员一个并行审核任务、各自触发 create。只让第一个成员
        // （loopCounter=0）发信，其余跳过，保证每张申请单只发一封。
        Integer loopCounter = (Integer) delegateTask.getVariable("loopCounter");
        if (loopCounter != null && loopCounter > 0) {
            log.info("[reagent-mail] 会签成员 loopCounter={}，已由首个成员通知，跳过. taskId={}", loopCounter, delegateTask.getId());
            return;
        }
        // 发送打标兜底（覆盖非多实例/异常重试场景）：同流程实例只发一封
        if (Boolean.TRUE.equals(delegateTask.getVariable(VAR_MAIL_SENT))) {
            log.info("[reagent-mail] 本流程实例已通知过发货处理人，跳过重复. taskId={}", delegateTask.getId());
            return;
        }

        // 收件人由流程变量 region（前端选择的发货区域：上海/宁波）决定。
        // create 事件早于 processInstanceId 落库，不能回表查申请单，故从流程变量读取。
        String region = (String) delegateTask.getVariable("region");
        boolean ningbo = "宁波".equals(region);
        String toMail = ningbo ? MAIL_NINGBO : MAIL_SHANGHAI;
        log.info("[reagent-mail] 申请单 {} 区域={}，通知邮箱={}", delegateTask.getVariable("applyNo"),
                ningbo ? "宁波" : "上海", toMail);

        // 组装模板参数（仅业务白名单字段，从流程变量取）
        Map<String, Object> params = ReagentMailParamsHelper.buildTemplateParams(delegateTask);

        try {
            reagentMailSendService.sendSingleMailToAddress(toMail, TEMPLATE_CODE, params);
            // 发送成功后打标，供会签后续成员/异常重试场景跳过重复发送
            delegateTask.setVariable(VAR_MAIL_SENT, Boolean.TRUE);
            log.info("[reagent-mail] 发货通知邮件已发送，收件人: {}", toMail);
        } catch (Exception e) {
            log.error("[reagent-mail] 发送失败，toMail: {}", toMail, e);
        }
    }

}
