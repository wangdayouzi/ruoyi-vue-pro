package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.api.dingtalk.*;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import java.util.*;

/** 持久待发队列。处理中超时及不确定结果均不自动重发，避免重复通知。 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class DingTalkNotificationQueueService implements DingTalkNotificationQueue {
    private final JdbcTemplate jdbc;
    private final DingTalkMessageSendApi sender;
    private final List<DingTalkNotificationGuard> guards;
    private final PlatformTransactionManager transactionManager;
    @jakarta.annotation.Resource private DingTalkNotificationRecipientApi recipients;
    private long tenant() { return Objects.requireNonNullElse(TenantContextHolder.getTenantId(),1L); }
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor=Exception.class)
    public void enqueuePlan(DingTalkNotificationPlanReqDTO plan,java.util.function.Predicate<Long> eligible) {
        Objects.requireNonNull(plan.getSendAfter(),"发送时间不能为空");
        if(plan.getExpiresAt()!=null && !plan.getExpiresAt().isAfter(plan.getSendAfter())) throw new IllegalArgumentException("失效时间必须晚于发送时间");
        var excluded=new HashSet<>(plan.getExcludedUserIds());
        for(Long id:recipients.resolve(plan.getUserIds(),plan.getDeptIds(),plan.isIncludeChildDepts())) {
            if(excluded.contains(id) || !eligible.test(id)) continue;
            var req=new DingTalkMessageSendReqDTO(); req.setRequestId(plan.getRequestId()); req.setTemplateCode(plan.getTemplateCode());
            req.setBusinessId(plan.getBusinessId()); req.setParams(plan.getParams()); req.setUserIds(List.of(id));
            // 与旧的明确人员入队复用同一持久化逻辑；不把部门或业务回调放入发送任务。
            enqueue(req,plan.getSendAfter(),plan.getExpiresAt(),plan.getGuardType(),plan.getBusinessVersion());
        }
    }
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor=Exception.class)
    public void enqueue(DingTalkMessageSendReqDTO request, LocalDateTime after, LocalDateTime expires,
                        String guardType,String version) {
        Objects.requireNonNull(after,"发送时间不能为空");
        if(expires!=null && !expires.isAfter(after)) throw new IllegalArgumentException("失效时间必须晚于发送时间");
        for(Long userId:new LinkedHashSet<>(request.getUserIds())) {
            var single=JsonUtils.parseObject(JsonUtils.toJsonString(request),DingTalkMessageSendReqDTO.class);
            single.setUserIds(List.of(userId));
            String payload=JsonUtils.toJsonString(single);
            int inserted=jdbc.update("INSERT INTO system_dingtalk_notification_queue(tenant_id,request_id,user_id,business_id,guard_type,business_version,payload,send_after,expires_at) VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT(tenant_id,request_id,user_id) DO NOTHING",
                    tenant(),request.getRequestId(),userId,request.getBusinessId(),guardType,version,payload,after,expires);
            if(inserted==0) {
                String previous=jdbc.queryForObject("SELECT payload FROM system_dingtalk_notification_queue WHERE tenant_id=? AND request_id=? AND user_id=?",String.class,tenant(),request.getRequestId(),userId);
                if(!JsonUtils.parseObject(previous,DingTalkMessageSendReqDTO.class).equals(single)) throw new IllegalArgumentException("同一通知请求编号不能用于不同消息");
            }
        }
    }
    @Override public int cancel(String businessId) {
        if(businessId==null || businessId.isBlank()) throw new IllegalArgumentException("业务编号不能为空");
        return jdbc.update("UPDATE system_dingtalk_notification_queue SET status=5,update_time=now() WHERE tenant_id=? AND business_id=? AND status=0",tenant(),businessId);
    }
    record Task(long id,long tenant,String payload,String guard,String business,String version) {}
    @Override public int cancelPending(String businessId,String guardType) {
        return jdbc.update("UPDATE system_dingtalk_notification_queue SET status=5,error_message='业务已调整',update_time=now() WHERE tenant_id=? AND business_id=? AND guard_type=? AND status=0",tenant(),businessId,guardType);
    }
    @Override public boolean exists(String businessId,String guardType,String version) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM system_dingtalk_notification_queue WHERE tenant_id=? AND business_id=? AND guard_type IS NOT DISTINCT FROM ? AND business_version=?)",Boolean.class,tenant(),businessId,guardType,version));
    }
    private Task claim() {
        var tx=new TransactionTemplate(transactionManager);
        return tx.execute(status -> {
            var rows=jdbc.query("SELECT id,tenant_id,payload,guard_type,business_id,business_version FROM system_dingtalk_notification_queue WHERE status=0 AND send_after<=now() ORDER BY send_after,id LIMIT 1 FOR UPDATE SKIP LOCKED",
                    (rs,n)->new Task(rs.getLong(1),rs.getLong(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)));
            if(rows.isEmpty()) return null;
            Task task=rows.get(0);
            jdbc.update("UPDATE system_dingtalk_notification_queue SET status=1,update_time=now() WHERE id=?",task.id());
            return task;
        });
    }
    public String dispatch() {
            int handled=0;
            jdbc.update("UPDATE system_dingtalk_notification_queue SET status=6,error_message='通知已过有效期',update_time=now() WHERE status=0 AND expires_at<=now()");
            jdbc.update("UPDATE system_dingtalk_notification_queue SET status=3,error_message='发送处理中超时，请核实收件情况',update_time=now() WHERE status=1 AND update_time<now()-interval '10 minutes'");
            for(int i=0;i<100;i++) {
                Task task=claim(); if(task==null) break;
                TenantUtils.execute(task.tenant(),()->send(task));
                handled++;
            }
            // 数据库级错误交给现有任务框架记录为失败，不能吞掉后显示执行成功。
            return "钉钉通知处理完成，本次处理 "+handled+" 条；具体发送结果请查看通知管理";
    }
    private void send(Task task) {
        try {
            // 认领后再次检查有效期，避免排队期间过期仍发送。
            Boolean expired=jdbc.queryForObject("SELECT expires_at IS NOT NULL AND expires_at<=now() FROM system_dingtalk_notification_queue WHERE id=?",Boolean.class,task.id());
            if(Boolean.TRUE.equals(expired)) { finish(task,6,"通知已过有效期"); return; }
            if(task.guard()!=null && !task.guard().isBlank()) {
                var guard=guards.stream().filter(g->g.businessType().equals(task.guard())).findFirst().orElseThrow(()->new IllegalStateException("未注册业务有效性检查器"));
                if(!guard.applicable(task.business(),task.version())) { finish(task,5,"业务状态已变化，不再提醒"); return; }
            }
            var result=sender.send(JsonUtils.parseObject(task.payload(),DingTalkMessageSendReqDTO.class));
            if(result==null || result.size()!=1) { finish(task,3,"发送结果不完整，请核实收件情况"); return; }
            var outcome=result.get(0);
            int state=Integer.valueOf(1).equals(outcome.status())?2:Integer.valueOf(2).equals(outcome.status())?4:3;
            finish(task,state,outcome.errorMessage());
        } catch(cn.iocoder.yudao.framework.common.exception.ServiceException ex) {
            // 发送服务的业务校验异常发生在网络调用前，可明确判定未发送。
            finish(task,4,ex.getMessage());
        } catch(Exception ex) { finish(task,3,"发送结果未知，请核实收件情况后再重发"); log.error("[dingtalk-notification-send] queueId={}",task.id(),ex); }
    }
    private void finish(Task task,int status,String error) {
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=?,error_message=?,update_time=now() WHERE id=? AND status=1",status,error==null?null:error.substring(0,Math.min(1000,error.length())),task.id());
    }
}
