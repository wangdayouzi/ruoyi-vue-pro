package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/** 部门按发送时成员动态展开，和显式人员合并去重。 */
@Service
@RequiredArgsConstructor
public class DingTalkRecipientResolver {
    private final DeptMapper depts;
    private final AdminUserMapper users;
    private final cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi recipients;
    private final DingTalkUserIdentityService identities;

    public List<DeptDO> departmentOptions() {
        return depts.selectList(new LambdaQueryWrapperX<DeptDO>().eq(DeptDO::getStatus,0).orderByAsc(DeptDO::getSort,DeptDO::getId));
    }
    public void validateDepartments(List<Long> ids) {
        for(Long id:ids) {
            var dept=depts.selectById(id);
            if(dept==null || !Integer.valueOf(0).equals(dept.getStatus())) throw invalidParamException("接收部门 {} 不存在或已停用",id);
        }
    }
    public List<Long> resolve(DingTalkMessageTemplateDO template) {
        var ids=recipients.resolve(template.getDefaultUserIds(),template.getDefaultDeptIds(),Boolean.TRUE.equals(template.getIncludeChildDepts()));
        if(ids.isEmpty()) return List.of();
        Map<Long,AdminUserDO> candidates=new LinkedHashMap<>();
        users.selectByIds(ids).forEach(u->candidates.put(u.getId(),u));
        var bound = identities.boundUserIds(candidates.values());
        return candidates.values().stream().filter(u -> Integer.valueOf(0).equals(u.getStatus()))
                .filter(u -> bound.contains(u.getId()))
                .map(AdminUserDO::getId).toList();
    }
}
