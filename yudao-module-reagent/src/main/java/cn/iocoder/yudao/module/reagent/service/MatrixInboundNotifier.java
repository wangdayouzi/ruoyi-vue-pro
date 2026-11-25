package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundDO;
import cn.iocoder.yudao.module.reagent.enums.MatrixInboundStatusEnum;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkMessageSendApi;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import static cn.iocoder.yudao.module.reagent.service.MatrixInboundSupport.*;

@Service
public class MatrixInboundNotifier {
    public static final String REMINDER_GUARD = "matrix-inbound-deadline";
    @Resource private DingTalkMessageSendApi templates;
    @Resource private DingTalkNotificationQueue queue;
    @Value("${reagent.matrix-inbound.page-url:}") private String pageUrl;

    public void created(MatrixInboundDO row) {
        sendReservation(row, ":created", "新预约提交");
        schedule(row);
    }

    public void updated(MatrixInboundDO row) {
        cancelPrevious(row.getId(), row.getVersion() - 1);
        sendReservation(row, ":updated:" + row.getVersion(), "预约已修改");
        schedule(row);
    }

    public void registered(MatrixInboundDO row) {
        cancelPrevious(row.getId(), row.getVersion() - 1);
        List<Long> recipients = row.getAppointmentUserIds();
        if (recipients == null || recipients.isEmpty()) {
            recipients = row.getApplicantId() == null ? List.of() : List.of(row.getApplicantId());
        }
        enqueue(row, "matrix_inbound_completed", recipients, "matrix-inbound:" + row.getId(),
                "matrix-inbound:" + row.getId() + ":status:" + row.getVersion(), "入库状态更新", now(), null, null);
        schedule(row);
    }

    public void deleted(MatrixInboundDO row) {
        cancelPrevious(row.getId(), row.getVersion());
        sendReservation(row, ":deleted:" + row.getVersion(), "预约已删除");
    }

    private void cancelPrevious(Long id, Integer version) {
        // 版本变化后，旧提醒既主动取消，也会被发送前的业务校验拒绝。
        queue.cancel("matrix-inbound:" + id);
        queue.cancel(reminderBusiness(id, version));
    }

    private void sendReservation(MatrixInboundDO row, String event, String action) {
        if ("SHANGHAI".equals(row.getSite()) && !Boolean.TRUE.equals(row.getUrgent())) return;
        String template = templateCode(row.getSite());
        enqueue(row, template, audience(template), "matrix-inbound:" + row.getId(),
                "matrix-inbound:" + row.getId() + event, action, now(), null, null);
    }

    void schedule(MatrixInboundDO row) {
        LocalDateTime current = now();
        if (!reminderPending(row, current)) return;
        // 提前入队但延迟发送；若进入两小时窗口后才创建，则立即提醒，过截止时间自动失效。
        LocalDateTime planned = row.getAppointmentDate().minusHours(2);
        String template = templateCode(row.getSite());
        enqueue(row, template, audience(template), reminderBusiness(row.getId(), row.getVersion()),
                "matrix-soon:" + row.getId() + ":" + row.getVersion(), "加急预约将在 2 小时内到期",
                planned.isAfter(current) ? planned : current, row.getAppointmentDate(), REMINDER_GUARD);
    }

    private boolean reminderPending(MatrixInboundDO row, LocalDateTime current) {
        return Boolean.TRUE.equals(row.getUrgent()) && MatrixInboundStatusEnum.INBOUND.getValue() != row.getInboundStatus()
                && row.getAppointmentDate() != null && row.getAppointmentDate().isAfter(current);
    }

    private void enqueue(MatrixInboundDO row, String template, List<Long> targets, String businessId, String requestId,
                         String action, LocalDateTime after, LocalDateTime expires, String guard) {
        var plan = new DingTalkNotificationPlanReqDTO();
        plan.setBusinessId(businessId);
        plan.setRequestId(requestId);
        plan.setTemplateCode(template);
        plan.setUserIds(targets);
        plan.setParams(parameters(row, action));
        plan.setSendAfter(after);
        plan.setExpiresAt(expires);
        plan.setGuardType(guard);
        queue.enqueuePlan(plan, user -> true);
    }

    private List<Long> audience(String template) {
        return Objects.requireNonNullElse(templates.getDefaultUserIds(template), List.of());
    }

    private Map<String, String> parameters(MatrixInboundDO row, String action) {
        var params = new LinkedHashMap<String, String>();
        params.put("action", action);
        params.put("site", siteName(row.getSite()));
        params.put("basNo", displayBas(row));
        params.put("matrixKind", "B".equals(row.getMatrixKind()) ? "个体基质" : "混合基质");
        params.put("applicantName", text(row.getApplicantName()));
        params.put("appointmentDate", formatTime(row.getAppointmentDate()));
        params.put("limsDesigned", row.getLimsDesigned() == null ? "未填写" : yesNo(row.getLimsDesigned()));
        params.put("urgent", yesNo(row.getUrgent()));
        params.put("status", statusName(row.getInboundStatus()));
        params.put("registeredName", row.getRegisteredName() == null ? "未登记" : row.getRegisteredName());
        params.put("registeredTime", row.getRegisteredAt() == null ? "未登记" : formatTime(row.getRegisteredAt()));
        params.put("detailLink", detailLink(row.getId()));
        return params;
    }

    private String detailLink(Long id) {
        if (pageUrl == null || pageUrl.isBlank()) return "请登录系统查看预约";
        try {
            URI uri = URI.create(pageUrl);
            if (!Set.of("https", "http").contains(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null || pageUrl.contains("(") || pageUrl.contains(")")) {
                return "请登录系统查看预约";
            }
            return "\n\n[查看预约](" + pageUrl + "?id=" + id + ")";
        } catch (IllegalArgumentException exception) {
            return "请登录系统查看预约";
        }
    }

    public static String reminderBusiness(Long id, Integer version) {
        return "matrix-deadline:" + id + ":" + version + ":soon";
    }
}
