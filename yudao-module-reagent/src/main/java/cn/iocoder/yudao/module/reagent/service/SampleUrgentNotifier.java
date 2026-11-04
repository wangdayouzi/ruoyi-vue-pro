package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkMessageSendApi;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.event.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class SampleUrgentNotifier {
    @Resource private SampleUrgentMapper mapper;
    @Resource private SampleUrgentLogMapper logs;
    @Resource private DingTalkMessageSendApi sender;
    @Resource private ConfigApi config;
    @Resource private PermissionApi permissions;
    @Resource private SampleUrgentPeopleService people;
    @Resource private cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue notificationQueue;
    private static final String DEADLINE_GUARD="sample-urgent-deadline";

    /** 升级时仅补齐未登记的当前版本，已发送/取消/失败的通知不会被启动过程重新生成。 */
    @Transactional(rollbackFor=Exception.class)
    public void initializeDeadline(Long id) {
        var row=mapper.lock(id);
        if(row==null || notificationQueue.exists("sample-urgent:"+id,DEADLINE_GUARD,String.valueOf(row.getRevision()))) return;
        if(row.getCreateTime()!=null && !row.getCreateTime().isBefore(row.getDeadline().minusMinutes(120))
                && notificationQueue.exists("sample-urgent:"+id,null,String.valueOf(row.getRevision()))) return;
        try { scheduleDeadline(row); }
        catch(IllegalStateException | cn.iocoder.yudao.framework.common.exception.ServiceException ex) {
            // 模板/接收人未配置只记录业务提醒异常，不能阻断整个应用启动；数据库异常仍正常抛出。
            failure(id,"临期提醒未登记："+safeError(ex));
        }
    }

    void scheduleDeadline(SampleUrgentDO row) {
        var now=java.time.LocalDateTime.now();
        if(row.getStatus()>=3 || !row.getDeadline().isAfter(now)
                || "false".equalsIgnoreCase(setting("enabled"))) return;
        var planned=row.getDeadline().minusMinutes(120);
        if(planned.isBefore(now)) planned=now;
        var receivers=sampleTargets(row);
        deliver(row,receivers,"urgent-soon-"+row.getId()+"-"+row.getRevision(),
                "即将逾期","",planned,null);
    }

    @Resource private org.springframework.transaction.PlatformTransactionManager transactionManager;

    @TransactionalEventListener(phase=TransactionPhase.BEFORE_COMMIT)
    public void onEvent(SampleUrgentNoticeEvent event) {
        // 业务最终写入完成后、事务提交前登记通知，待发队列与业务原子提交；真实发送由统一任务执行。
        try {
            var tx = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
            tx.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED);
            // 单租户模式没有请求租户上下文，钉钉配置统一使用主租户 1；不改变业务请求上下文。
            Long noticeTenantId = event.tenantId() == null ? 1L : event.tenantId();
            TenantUtils.execute(noticeTenantId, () -> tx.executeWithoutResult(status -> notifyEvent(event)));
        } catch(Exception ex) { log.error("[sample-urgent-notice] urgentId={}, logId={}",event.urgentId(),event.logId(),ex); }
    }

    void notifyEvent(SampleUrgentNoticeEvent event) {
        var row=mapper.selectById(event.urgentId());
        if(row==null) return;
        boolean refreshDeadline=!"补充说明".equals(event.action());
        if(refreshDeadline) notificationQueue.cancelPending("sample-urgent:"+row.getId(),DEADLINE_GUARD);
        if("false".equalsIgnoreCase(setting("enabled"))) return;
        try {
            Targets receivers=switch(event.action()) {
                case "登记需求", "修改需求", "取消需求" -> sampleTargets(row);
                case "补充说明" -> SampleUrgentRules.isParty(row,event.actorId())?sampleTargets(row):appointmentTargets(row);
                default -> appointmentTargets(row);
            };
            deliver(row,receivers,"urgent-event-"+event.logId(),event.action(),event.content(),java.time.LocalDateTime.now(),event.actorId());
            if(refreshDeadline && !("登记需求".equals(event.action()) &&
                    !row.getDeadline().minusMinutes(120).isAfter(java.time.LocalDateTime.now()))) scheduleDeadline(row);
        } catch(Exception ex) { failure(row.getId(),"提醒未发出："+safeError(ex)); }
    }

    private record Targets(List<Long> users,List<Long> departments,boolean administrators) {}
    private Targets appointmentTargets(SampleUrgentDO row) {
        if(row.getAppointmentUserIds()!=null && !row.getAppointmentUserIds().isEmpty()) return new Targets(row.getAppointmentUserIds(),List.of(),false);
        return new Targets(List.of(row.getRequesterId()),List.of(),false);
    }
    private Targets sampleTargets(SampleUrgentDO row) {
        if(row.getHandlerId()!=null) return new Targets(List.of(row.getHandlerId()),List.of(),true);
        var users=row.getNoticeUserIds()==null?List.<Long>of():row.getNoticeUserIds();
        var depts=row.getNoticeDeptIds()==null?List.<Long>of():row.getNoticeDeptIds();
        if(!users.isEmpty() || !depts.isEmpty()) return new Targets(users,depts,true);
        var defaults=sender.getDefaultUserIds("sample_urgent_notice");
        var configured=defaults==null?List.<Long>of():defaults.stream().filter(people::administratorEligible).toList();
        if(configured==null || configured.isEmpty()) throw new IllegalStateException("没有有效的样品管理员接收通知，请检查历史模板的默认人员/部门配置");
        return new Targets(configured,List.of(),true);
    }
    void deliver(SampleUrgentDO row,Targets targets,String requestId,String action,String content,
                 java.time.LocalDateTime sendAfter,Long actorId) {
        var plan=new DingTalkNotificationPlanReqDTO();
        plan.setRequestId(requestId); plan.setTemplateCode("sample_urgent_notice"); plan.setBusinessId("sample-urgent:"+row.getId());
        plan.setParams(params(row,action,content)); plan.setUserIds(targets.users()); plan.setDeptIds(targets.departments());
        plan.setExcludedUserIds(actorId==null?List.of():List.of(actorId)); plan.setSendAfter(sendAfter);
        boolean deadlineNotice=requestId.startsWith("urgent-soon-");
        plan.setExpiresAt(deadlineNotice?row.getDeadline():null); plan.setGuardType(deadlineNotice?DEADLINE_GUARD:null);
        plan.setBusinessVersion(String.valueOf(row.getRevision()));
        notificationQueue.enqueuePlan(plan,targets.administrators()?people::administratorEligible:
                id->permissions.hasAnyPermissions(id,"reagent:sample-urgent:query"));
    }

    Map<String,String> params(SampleUrgentDO row,String action,String content) {
        Map<String,String> p=new LinkedHashMap<>();
        p.put("action",value(action,50)); p.put("registrationNo",String.valueOf(row.getId()));
        p.put("clinicalProtocolNo",value(row.getProjectNo(),200)); p.put("testItems",value(row.getSampleScope(),400));
        p.put("basNos",value(row.getBasNos(),300)); p.put("reservationTime",time(row.getDeadline()));
        p.put("appointmentNames",row.getAppointmentUserIds()!=null && !row.getAppointmentUserIds().isEmpty()?value(row.getContacts(),300):"待确认");
        p.put("submitterName",value(row.getRequesterName(),100)); p.put("noticeRecipients",value(row.getNoticeNames(),2000));
        p.put("handlerName",row.getHandlerId()==null?"未接手":value(row.getHandlerName(),100));
        p.put("status",List.of("待处理","处理中","部分入库","已入库","已取消").get(row.getStatus()));
        p.put("remark",value(row.getRemark(),1400));
        p.put("completedScope",value(row.getCompletedScope(),4000)); p.put("remainingScope",value(row.getRemainingScope(),4000));
        p.put("exceptionTypeName",Map.of("NONE","无异常","NOT_ARRIVED","未收到样品","WAIT_REPLY","等待回复/补资料","SAMPLE_ISSUE","样品问题","DELAY","预计延期","OTHER","其他异常").getOrDefault(Objects.toString(row.getExceptionType(),"NONE"),"其他异常"));
        p.put("exceptionNote",value(row.getExceptionNote(),4000)); p.put("actualInboundTime",time(row.getCompletedTime()));
        String reason="修改需求".equals(action)?changeReason(content):"";
        p.put("changeReason",value(reason,1000));
        return p;
    }
    private String changeReason(String content) {
        String reason=Objects.toString(content,""); int snapshot=reason.indexOf("\n修改前：");
        return snapshot<0?reason:reason.substring(0,snapshot);
    }
    private static String value(String text,int max) {
        if(text==null || text.isBlank()) return "—";
        text=text.trim(); return text.length()<=max?text:text.substring(0,max-1)+"…";
    }
    private static String time(java.time.LocalDateTime date) {
        return date==null?"—":date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    String key(String suffix) { return "reagent.sample-urgent.dingtalk."+TenantContextHolder.getRequiredTenantId()+"."+suffix; }
    String setting(String suffix) { return config.getConfigValueByKey(key(suffix)); }
    static String clip(String value,int max) {
        if(value==null || value.isBlank()) return "未填写";
        return value.length()<=max ? value : value.substring(0,max)+"…";
    }
    private String safeError(Exception ex) { return clip(ex.getMessage(),800); }
    private void failure(Long id,String content) {
        var entry=new SampleUrgentLogDO(); entry.setUrgentId(id); entry.setOperatorId(0L); entry.setOperatorName("系统");
        entry.setAction("钉钉提醒异常"); entry.setContent(content); logs.insert(entry);
    }
}
