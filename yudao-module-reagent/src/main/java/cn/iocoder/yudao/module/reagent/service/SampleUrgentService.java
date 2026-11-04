package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
public class SampleUrgentService {
    @Resource private SampleUrgentMapper mapper;
    @Resource private SampleUrgentLogMapper logs;
    @Resource private AdminUserApi users;
    @Resource private PermissionApi permissions;
    @Resource private SampleUrgentPeopleService people;
    @Resource private org.springframework.context.ApplicationEventPublisher events;
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.beans.factory.ObjectProvider<cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue> notificationQueues;

    @Transactional(rollbackFor=Exception.class)
    public void delete(Long id,Integer revision) {
        if(!permissions.hasAnyPermissions(userId(),"reagent:sample-urgent:update")) throw invalidParamException("需要修改预约权限");
        var row=lock(id,revision);
        owner(row);
        notificationQueues.getObject().cancel("sample-urgent:"+id);
        var entry=new SampleUrgentLogDO(); entry.setUrgentId(id); entry.setAction("删除预约");
        entry.setOperatorId(userId()); entry.setOperatorName(userName());
        entry.setContent("删除预约，保留原始数据及处理记录，取消待发送通知"); logs.insert(entry);
        if(mapper.deleteById(id)!=1) throw invalidParamException("预约已变化，请刷新后重试");
    }

    public PageResult<SampleUrgentDO> page(SampleUrgentPageReqVO req) { return mapper.page(req,userId()); }
    public SampleUrgentDO get(Long id) {
        var row=mapper.selectById(id);
        if(row==null) throw invalidParamException("加急需求不存在");
        return row;
    }
    public List<SampleUrgentLogDO> logs(Long id) {
        get(id);
        return logs.selectList(new LambdaQueryWrapperX<SampleUrgentLogDO>().eq(SampleUrgentLogDO::getUrgentId,id).orderByDesc(SampleUrgentLogDO::getId));
    }
    @Transactional(rollbackFor=Exception.class)
    public Long save(SampleUrgentSaveReqVO req) {
        SampleUrgentRules.validateSave(req);
        req.setProjectNo(req.getProjectNo().trim());
        people.prepare(req);
        req.setSampleScope(req.getSampleScope().trim());
        req.setReason(Objects.toString(req.getReason(),""));
        req.setBasNos(SampleUrgentRules.normalizeBas(req.getBasNos()));
        if(req.getId()==null) {
            var row=BeanUtils.toBean(req,SampleUrgentDO.class);
            row.setRequesterId(userId()); row.setRequesterName(userName());
            row.setNoticeNames(people.noticeNames(req.getNoticeUserIds(),req.getNoticeDeptIds()));
            row.setStatus(0); row.setExceptionType("NONE"); row.setRevision(0);
            mapper.insert(row);
            log(row.getId(),"登记需求",describe(req));
            return row.getId();
        }
        var row=lock(req.getId(),req.getRevision());
        owner(row); active(row);
        if(SampleUrgentRules.blank(req.getChangeReason())) throw invalidParamException("请填写修改原因，便于样品组了解变更");
        var before=BeanUtils.toBean(row,SampleUrgentSaveReqVO.class);
        var updated=BeanUtils.toBean(req,SampleUrgentDO.class);
        updated.setNoticeNames(people.noticeNames(req.getNoticeUserIds(),req.getNoticeDeptIds()));
        // 显式赋值避免可选日期清空时被 MyBatis 的 NOT_NULL 更新策略忽略。
        mapper.update(null,new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<SampleUrgentDO>()
                .eq(SampleUrgentDO::getId,row.getId())
                .set(SampleUrgentDO::getProjectNo,updated.getProjectNo()).set(SampleUrgentDO::getNewProject,updated.getNewProject())
                .set(SampleUrgentDO::getClinicalId,updated.getClinicalId())
                .set(SampleUrgentDO::getBasNos,updated.getBasNos()).set(SampleUrgentDO::getSampleScope,updated.getSampleScope())
                .set(SampleUrgentDO::getDeadline,updated.getDeadline()).set(SampleUrgentDO::getExpectedArrival,updated.getExpectedArrival())
                .set(SampleUrgentDO::getPlannedTestTime,updated.getPlannedTestTime()).set(SampleUrgentDO::getContacts,updated.getContacts())
                .set(SampleUrgentDO::getAppointmentUserIds,cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString(updated.getAppointmentUserIds()))
                .set(SampleUrgentDO::getNoticeUserIds,cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString(updated.getNoticeUserIds()))
                .set(SampleUrgentDO::getNoticeDeptIds,cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString(updated.getNoticeDeptIds()))
                .set(SampleUrgentDO::getNoticeNames,updated.getNoticeNames())
                .set(SampleUrgentDO::getReason,updated.getReason()).set(SampleUrgentDO::getLogisticsNo,updated.getLogisticsNo())
                .set(SampleUrgentDO::getRemark,updated.getRemark()).set(SampleUrgentDO::getRevision,row.getRevision()+1)
                .set(SampleUrgentDO::getUpdateTime,java.time.LocalDateTime.now()).set(SampleUrgentDO::getUpdater,userId().toString()));
        log(row.getId(),"修改需求",req.getChangeReason()+"\n修改前：\n"+describe(before)+"\n修改后：\n"+describe(req));
        return row.getId();
    }
    @Transactional(rollbackFor=Exception.class)
    public void action(SampleUrgentActionReqVO req) {
        var row=lock(req.getId(),req.getRevision());
        switch(req.getAction()) {
            case "COMMENT" -> {
                if(SampleUrgentRules.blank(req.getContent())) throw invalidParamException("请填写补充说明");
                if(!SampleUrgentRules.isParty(row,userId()) && !canHandle()) throw invalidParamException("仅提交人、预约人或样品组可补充说明");
                log(row.getId(),"补充说明",req.getContent());
                return;
            }
            case "CANCEL" -> {
                if(!permissions.hasAnyPermissions(userId(),"reagent:sample-urgent:update")) throw invalidParamException("需要修改本人需求权限");
                owner(row); active(row);
                if(SampleUrgentRules.blank(req.getContent())) throw invalidParamException("请填写取消原因");
                row.setStatus(4);
                log(row.getId(),"取消需求",req.getContent());
            }
            case "CLAIM" -> {
                handlePermission(); active(row);
                if(row.getHandlerId()!=null) throw invalidParamException("已有处理人，请刷新列表");
                row.setHandlerId(userId()); row.setHandlerName(userName()); row.setStatus(1);
                log(row.getId(),"接手处理","已接手此需求");
            }
            case "PROGRESS" -> {
                handlePermission();
                if (row.getStatus() == 4) throw invalidParamException("已取消的需求不能更新进度");
                if(row.getHandlerId()!=null && !Objects.equals(row.getHandlerId(),userId())) throw invalidParamException("请由当前处理人更新进度");
                SampleUrgentRules.validateProgress(req,row);
                row.setHandlerId(userId()); row.setHandlerName(userName());
                row.setStatus(req.getStatus()); row.setExceptionType(req.getExceptionType());
                row.setExceptionNote("NONE".equals(req.getExceptionType())?"":req.getExceptionNote());
                row.setCompletedScope(Objects.toString(req.getCompletedScope(),""));
                row.setRemainingScope(Objects.toString(req.getRemainingScope(),""));
                row.setCompletedTime(req.getStatus() == 3 ? req.getCompletedTime() : null);
                log(row.getId(),"更新进度",List.of("待处理","处理中","部分入库","已完成","已取消").get(row.getStatus())
                        +"\n已完成范围："+row.getCompletedScope()+"\n剩余范围："+row.getRemainingScope()
                        +"\n异常："+row.getExceptionType()+" "+Objects.toString(row.getExceptionNote(),"")
                        +"\n实际入库时间："+Objects.toString(row.getCompletedTime(),"未完成")
                        +"\n说明："+Objects.toString(req.getContent(),""));
            }
            default -> throw invalidParamException("不支持的操作");
        }
        row.setRevision(row.getRevision()+1);
        mapper.updateById(row);
    }
    private SampleUrgentDO lock(Long id,Integer revision) {
        var row=mapper.lock(id);
        if(row==null) throw invalidParamException("加急需求不存在");
        if(!Objects.equals(row.getRevision(),revision)) throw invalidParamException("需求已被更新，请刷新后重试");
        return row;
    }
    private void owner(SampleUrgentDO row) {
        if(!SampleUrgentRules.isParty(row,userId())) throw invalidParamException("仅提交人或预约人可修改或取消此需求");
    }
    private void active(SampleUrgentDO row) {
        if(row.getStatus()>=3) throw invalidParamException("已完成或已取消的需求不能变更，请另行登记新需求");
    }
    private boolean canHandle() { return permissions.hasAnyPermissions(userId(),"reagent:sample-urgent:handle"); }
    private void handlePermission() { if(!canHandle()) throw invalidParamException("需要样品组处理权限"); }
    private Long userId() { return Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()); }
    private String userName() { return users.getUser(userId()).getNickname(); }
    private void log(Long id,String action,String content) {
        var log=new SampleUrgentLogDO(); log.setUrgentId(id); log.setAction(action); log.setContent(content);
        log.setOperatorId(userId()); log.setOperatorName(userName()); logs.insert(log);
        events.publishEvent(new SampleUrgentNoticeEvent(
                cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),
                id,log.getId(),userId(),action,content));
    }
    private String describe(SampleUrgentSaveReqVO r) {
        return "临床方案编号："+r.getProjectNo()
                +"\n新临床方案编号："+r.getNewProject()+"\nBAS号："+Objects.toString(r.getBasNos(),"")
                +"\n检测项："+r.getSampleScope()+"\n预约入库时间："+r.getDeadline()+"\n预计到样："+Objects.toString(r.getExpectedArrival(),"")
                +"\n计划检测："+Objects.toString(r.getPlannedTestTime(),"")+"\n预约人："+r.getContacts()+"\n加急原因："+r.getReason()
                +"\n通知样品管理员："+people.noticeNames(r.getNoticeUserIds(),r.getNoticeDeptIds())+"\n物流单号："+Objects.toString(r.getLogisticsNo(),"")+"\n备注："+Objects.toString(r.getRemark(),"");
    }
}
