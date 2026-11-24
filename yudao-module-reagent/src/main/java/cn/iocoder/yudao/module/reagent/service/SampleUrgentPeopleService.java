package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentSaveReqVO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/** 校验预约人账号状态和通知对象权限。 */
@Service @RequiredArgsConstructor
public class SampleUrgentPeopleService {
    private final AdminUserApi users;
    private final DeptApi departments;
    private final PermissionApi permissions;
    private final DingTalkNotificationRecipientApi recipients;
    public record Person(Long id,String name) {}
    public record Department(Long id,Long parentId,String name) {}
    public record Options(List<Person> appointmentUsers,List<Person> administrators,List<Department> departments) {}
    boolean administratorEligible(Long id) {
        return permissions.hasAnyPermissions(id,"reagent:sample-urgent:query") && permissions.hasAnyPermissions(id,"reagent:sample-urgent:handle");
    }
    public Options options() {
        var enabled=users.getUserListByStatus(0);
        var depts=departments.getEnabledDeptList();
        return new Options(enabled.stream().map(u->new Person(u.getId(),u.getNickname())).toList(),
                enabled.stream().filter(u->administratorEligible(u.getId())).map(u->new Person(u.getId(),u.getNickname())).toList(),
                depts.stream().map(d->new Department(d.getId(),d.getParentId(),d.getName())).toList());
    }
    public void prepare(SampleUrgentSaveReqVO req) {
        req.setAppointmentUserIds(distinct(req.getAppointmentUserIds()));
        req.setNoticeUserIds(distinct(req.getNoticeUserIds())); req.setNoticeDeptIds(distinct(req.getNoticeDeptIds()));
        if(req.getAppointmentUserIds().isEmpty()) throw invalidParamException("请选择预约人");
        req.getAppointmentUserIds().forEach(users::validateUser);
        for(Long id:req.getNoticeUserIds()) {
            users.validateUser(id);
            if(!administratorEligible(id)) throw invalidParamException("所选通知人员不具有样品组处理权限，请重新选择");
        }
        departments.validateDeptList(req.getNoticeDeptIds());
        if ((!req.getNoticeUserIds().isEmpty() || !req.getNoticeDeptIds().isEmpty())
                && administrators(req.getNoticeUserIds(), req.getNoticeDeptIds()).isEmpty()) throw invalidParamException("所选部门或人员中没有有效的样品管理员");
        var names=users.getUserMap(req.getAppointmentUserIds());
        req.setContacts(req.getAppointmentUserIds().stream().map(id->names.get(id).getNickname()).collect(Collectors.joining("、")));
        if(req.getContacts().length()>300) throw invalidParamException("预约人姓名总长度超过300字，请减少选择人数");
    }
    public String noticeNames(List<Long> userIds,List<Long> deptIds) {
        List<String> labels=new ArrayList<>();
        var userMap=users.getUserMap(distinct(userIds));
        for(Long id:distinct(userIds)) if(userMap.containsKey(id)) labels.add(userMap.get(id).getNickname());
        var deptMap=departments.getDeptMap(distinct(deptIds));
        for(Long id:distinct(deptIds)) if(deptMap.containsKey(id)) labels.add(deptMap.get(id).getName()+"（部门）");
        return String.join("、",labels);
    }
    /** 部门展开和有效账号处理由通用通知模块负责，此处只保留样品管理员资格。 */
    public Set<Long> administrators(List<Long> userIds,List<Long> deptIds) {
        return recipients.resolve(userIds,deptIds,true).stream().filter(this::administratorEligible)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    static List<Long> distinct(List<Long> ids) { return ids==null?new ArrayList<>():new ArrayList<>(new LinkedHashSet<>(ids)); }
}
