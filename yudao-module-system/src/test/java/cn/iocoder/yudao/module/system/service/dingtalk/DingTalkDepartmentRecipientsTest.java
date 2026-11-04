package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkDepartmentRecipientsTest {
    DeptMapper depts; AdminUserMapper users; DingTalkNotificationRecipientApi recipients;
    DingTalkRecipientResolver resolver; DingTalkMessageTemplateDO template;
    @BeforeEach void setup() {
        depts=mock(DeptMapper.class); users=mock(AdminUserMapper.class); recipients=mock(DingTalkNotificationRecipientApi.class);
        var identities=new DingTalkUserIdentityService(mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper.class),mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper.class),mock(cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkContactClient.class));
        resolver=new DingTalkRecipientResolver(depts,users,recipients,identities);
        template=new DingTalkMessageTemplateDO(); template.setCode("sample_urgent_notice");
        template.setDefaultUserIds(List.of(10L)); template.setDefaultDeptIds(List.of(1L)); template.setIncludeChildDepts(true);
        when(recipients.resolve(any(),any(),anyBoolean())).thenReturn(List.of(10L,11L));
        when(users.selectByIds(anyCollection())).thenReturn(List.of(user(10L),user(11L)));
    }
    @Test void templateUsesCommonDepartmentResolverWithoutBusinessPermissions() {
        assertEquals(List.of(10L,11L),resolver.resolve(template));
        verify(recipients).resolve(List.of(10L),List.of(1L),true);
        verify(users,never()).selectListByDeptIds(anyCollection());
    }
    @Test void childDepartmentChoiceIsPassedToCommonResolver() {
        template.setIncludeChildDepts(false); resolver.resolve(template);
        verify(recipients).resolve(List.of(10L),List.of(1L),false);
    }
    @Test void templateDefaultsStillExcludeUnboundEmployees() {
        var unbound=user(11L); unbound.setDingTalkUserId(""); when(users.selectByIds(anyCollection())).thenReturn(List.of(user(10L),unbound));
        assertEquals(List.of(10L),resolver.resolve(template));
    }
    @Test void departmentSelectionMustReferenceEnabledDepartments() {
        var dept=new DeptDO(); dept.setId(1L); dept.setStatus(0); when(depts.selectById(1L)).thenReturn(dept);
        assertDoesNotThrow(()->resolver.validateDepartments(List.of(1L)));
        dept.setStatus(1); assertThrows(ServiceException.class,()->resolver.validateDepartments(List.of(1L)));
        assertThrows(ServiceException.class,()->resolver.validateDepartments(List.of(99L)));
    }
    private AdminUserDO user(long id) { return AdminUserDO.builder().id(id).nickname("员工"+id).status(0).dingTalkUserId("ding"+id).build(); }
}
