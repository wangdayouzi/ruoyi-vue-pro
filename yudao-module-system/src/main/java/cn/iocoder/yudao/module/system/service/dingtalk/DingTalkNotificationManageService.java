package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationGuard;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkQueuePageReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
@RequiredArgsConstructor
public class DingTalkNotificationManageService {
    private final JdbcTemplate jdbc;
    private final List<DingTalkNotificationGuard> guards;
    @Value("${yudao.tenant.enable:true}")
    private boolean tenantEnabled = true;
    private long tenant() { return Objects.requireNonNullElse(TenantContextHolder.getTenantId(),1L); }
    public record Notice(long id,String requestId,String businessId,long userId,String userName,
                         String templateCode,int status,LocalDateTime sendAfter,LocalDateTime expiresAt,
                         String errorMessage,LocalDateTime createTime,Long retryOf,int retryNo,
                         Long operatorId,String operationReason,Map<String,String> params) {}
    private Notice read(java.sql.ResultSet rs,int n) throws java.sql.SQLException {
        var req=JsonUtils.parseObject(rs.getString("payload"),DingTalkMessageSendReqDTO.class);
        return new Notice(rs.getLong("id"),rs.getString("request_id"),rs.getString("business_id"),
                rs.getLong("user_id"),rs.getString("user_name"),req.getTemplateCode(),rs.getInt("status"),
                rs.getObject("send_after",LocalDateTime.class),rs.getObject("expires_at",LocalDateTime.class),
                rs.getString("error_message"),rs.getObject("create_time",LocalDateTime.class),
                rs.getObject("retry_of",Long.class),rs.getInt("retry_no"),rs.getObject("operator_id",Long.class),
                rs.getString("operation_reason"),req.getParams());
    }
    private String from() {
        return " FROM system_dingtalk_notification_queue q LEFT JOIN system_users u ON u.id=q.user_id AND u.deleted=0 "
                + (tenantEnabled ? "AND u.tenant_id=q.tenant_id " : "");
    }
    public PageResult<Notice> page(DingTalkQueuePageReqVO req) {
        String where=" WHERE q.tenant_id=?";
        List<Object> args=new ArrayList<>(); args.add(tenant());
        if(req.getStatus()!=null) { where+=" AND q.status=?"; args.add(req.getStatus()); }
        if(req.getBusinessId()!=null && !req.getBusinessId().isBlank()) { where+=" AND q.business_id=?"; args.add(req.getBusinessId().trim()); }
        if(req.getTemplateCode()!=null && !req.getTemplateCode().isBlank()) { where+=" AND (q.payload::jsonb ->> 'templateCode')=?"; args.add(req.getTemplateCode().trim()); }
        Long total=jdbc.queryForObject("SELECT count(*)"+from()+where,Long.class,args.toArray());
        args.add(req.getPageSize()); args.add((long)(req.getPageNo()-1)*req.getPageSize());
        var list=jdbc.query("SELECT q.*,COALESCE(u.nickname,q.user_id::text) AS user_name"+from()+where+" ORDER BY q.id DESC LIMIT ? OFFSET ?",this::read,args.toArray());
        return new PageResult<>(list,total);
    }
    public Notice get(long id) {
        var rows=jdbc.query("SELECT q.*,COALESCE(u.nickname,q.user_id::text) AS user_name"+from()+" WHERE q.id=? AND q.tenant_id=?",this::read,id,tenant());
        if(rows.isEmpty()) throw invalidParamException("通知不存在");
        return rows.get(0);
    }
    public void cancel(long id,long operator,String reason) {
        if(jdbc.update("UPDATE system_dingtalk_notification_queue SET status=5,operator_id=?,operation_reason=?,update_time=now() WHERE id=? AND tenant_id=? AND status=0",operator,reason,id,tenant())!=1)
            throw invalidParamException("仅待发送通知可以取消，请刷新后重试");
    }
    /** 锁住原始通知串行分配重发次数；只重发已明确失败或人工核实的未知结果。 */
    @Transactional(rollbackFor=Exception.class)
    public void resend(long id,long operator,String reason,boolean confirmed) {
        var current=get(id);
        long root=current.retryOf()==null?id:current.retryOf();
        jdbc.queryForObject("SELECT id FROM system_dingtalk_notification_queue WHERE id=? AND tenant_id=? FOR UPDATE",Long.class,root,tenant());
        current=get(id);
        if(current.status()!=4 && current.status()!=3) throw invalidParamException("仅失败或结果未知的通知可以重发");
        if(current.status()==3 && !confirmed) throw invalidParamException("请先核实接收人尚未收到消息");
        // 不允许从历史失败行反复发起，必须针对最新一次发送结果处理。
        Long latest=jdbc.queryForObject("SELECT max(id) FROM system_dingtalk_notification_queue WHERE tenant_id=? AND (id=? OR retry_of=?)",Long.class,tenant(),root,root);
        if(!Objects.equals(latest,id)) throw invalidParamException("此通知已有重发记录，请查看最新记录");
        Integer count=jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue WHERE tenant_id=? AND retry_of=?",Integer.class,tenant(),root);
        if(count>=3) throw invalidParamException("已达到三次人工重发上限，请排查配置或联系管理员");
        if(current.expiresAt()!=null && !current.expiresAt().isAfter(LocalDateTime.now())) throw invalidParamException("通知已过期，不能重发");
        var row=jdbc.queryForMap("SELECT * FROM system_dingtalk_notification_queue WHERE id=? AND tenant_id=?",id,tenant());
        String guard=(String)row.get("guard_type");
        if(guard!=null && !guard.isBlank()) {
            var checker=guards.stream().filter(g->g.businessType().equals(guard)).findFirst().orElseThrow(()->invalidParamException("业务检查器不存在"));
            if(!checker.applicable(current.businessId(),(String)row.get("business_version"))) throw invalidParamException("业务状态已变化，不再需要提醒");
        }
        var req=JsonUtils.parseObject((String)row.get("payload"),DingTalkMessageSendReqDTO.class);
        req.setRequestId("queue-retry-"+root+"-"+(count+1));
        jdbc.update("INSERT INTO system_dingtalk_notification_queue(tenant_id,request_id,user_id,business_id,guard_type,business_version,payload,send_after,expires_at,retry_of,retry_no,operator_id,operation_reason) VALUES(?,?,?,?,?,?,?,now(),?,?,?,?,?)",
                tenant(),req.getRequestId(),current.userId(),current.businessId(),guard,row.get("business_version"),JsonUtils.toJsonString(req),row.get("expires_at"),root,count+1,operator,reason);
    }
}
