package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.*;

@Service @Validated @RequiredArgsConstructor
public class DingTalkNotificationRecipientService implements DingTalkNotificationRecipientApi {
    private final AdminUserApi users;
    private final DeptApi departments;
    @Override public List<Long> resolve(List<Long> userIds,List<Long> deptIds,boolean includeChildDepts) {
        Set<Long> candidates=new LinkedHashSet<>(userIds==null?List.of():userIds);
        Set<Long> selected=new LinkedHashSet<>(deptIds==null?List.of():deptIds);
        if(!selected.isEmpty()) {
            // 先排除停用/不存在的部门，避免其子部门被意外纳入通知范围。
            selected.retainAll(departments.getDeptList(selected).stream().filter(d->Integer.valueOf(0).equals(d.getStatus())).map(d->d.getId()).toList());
            if(includeChildDepts && !selected.isEmpty()) {
                var children=departments.getChildDeptList(selected);
                boolean changed;
                do {
                    changed=false;
                    for(var child:children) if(Integer.valueOf(0).equals(child.getStatus()) && selected.contains(child.getParentId())) changed|=selected.add(child.getId());
                } while(changed);
            }
            if(!selected.isEmpty()) users.getUserListByDeptIds(selected).forEach(u->candidates.add(u.getId()));
        }
        if(candidates.isEmpty()) return List.of();
        var active=new HashSet<Long>();
        users.getUserList(candidates).stream().filter(u->Integer.valueOf(0).equals(u.getStatus())).forEach(u->active.add(u.getId()));
        candidates.retainAll(active);
        return List.copyOf(candidates);
    }
}
