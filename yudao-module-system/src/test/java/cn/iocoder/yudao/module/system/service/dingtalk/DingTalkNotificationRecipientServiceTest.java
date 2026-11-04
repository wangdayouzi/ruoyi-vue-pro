package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkNotificationRecipientServiceTest {
    AdminUserApi users; DeptApi depts; DingTalkNotificationRecipientService service;
    List<DeptRespDTO> departments;
    @BeforeEach void setup() {
        users=mock(AdminUserApi.class); depts=mock(DeptApi.class); service=new DingTalkNotificationRecipientService(users,depts);
        departments=List.of(dept(1,0,0),dept(2,1,0),dept(3,2,0),dept(4,1,1),dept(5,4,0));
        when(depts.getDeptList(anyCollection())).thenAnswer(i->{Collection<Long> ids=i.getArgument(0);return departments.stream().filter(d->ids.contains(d.getId())).toList();});
        when(depts.getChildDeptList(anyCollection())).thenReturn(departments.subList(1,5));
        when(users.getUserListByDeptIds(anyCollection())).thenAnswer(i->{
            Collection<Long> ids=i.getArgument(0);List<AdminUserRespDTO> members=new ArrayList<>();
            if(ids.contains(1L)) members.addAll(List.of(user(10,0),user(11,0)));
            if(ids.contains(2L)) members.addAll(List.of(user(11,0),user(12,0)));
            if(ids.contains(3L)) members.add(user(13,0));
            if(ids.contains(5L)) members.add(user(14,0)); return members;
        });
        when(users.getUserList(anyCollection())).thenAnswer(i->{Collection<Long> ids=i.getArgument(0);return ids.stream().filter(id->id!=99L).map(id->user(id,id==15L?1:0)).toList();});
    }
    @Test void explicitPeopleAndEnabledDescendantsAreMergedAndDeduplicated() {
        assertEquals(List.of(10L,11L,12L,13L),service.resolve(List.of(10L,10L,99L,15L),List.of(1L,1L),true));
        verify(users).getUserListByDeptIds(Set.of(1L,2L,3L));
    }
    @Test void disabledBranchesAndMissingDepartmentsDoNotExpand() {
        assertEquals(List.of(),service.resolve(List.of(),List.of(4L,99L),true));
        verify(depts,never()).getChildDeptList(anyCollection()); verify(users,never()).getUserListByDeptIds(anyCollection());
    }
    @Test void childDepartmentsCanBeExcluded() {
        assertEquals(List.of(10L,11L),service.resolve(null,List.of(1L),false));
        verify(users).getUserListByDeptIds(Set.of(1L));
    }
    @Test void currentMembershipIsReadAgainWhenGeneratingANewPlan() {
        when(users.getUserListByDeptIds(anyCollection())).thenReturn(List.of(user(10,0)),List.of(user(17,0)));
        assertEquals(List.of(10L),service.resolve(List.of(),List.of(1L),false));
        assertEquals(List.of(17L),service.resolve(List.of(),List.of(1L),false));
    }
    @Test void emptyScopeDoesNotReadUsersAndExplicitUsersNeedNoDepartment() {
        assertEquals(List.of(),service.resolve(null,null,true)); verifyNoInteractions(users,depts);
        assertEquals(List.of(10L),service.resolve(List.of(10L),List.of(),true)); verifyNoInteractions(depts);
    }
    @Test void interfaceConstraintsAreValidUnderRealSpringMethodValidation() {
        try(var factory=jakarta.validation.Validation.buildDefaultValidatorFactory()) {
            var pf=new org.springframework.aop.framework.ProxyFactory(service); pf.setProxyTargetClass(true);
            pf.addAdvice(new org.springframework.validation.beanvalidation.MethodValidationInterceptor(factory.getValidator()));
            var proxy=(DingTalkNotificationRecipientService)pf.getProxy();
            assertEquals(List.of(10L),proxy.resolve(List.of(10L),List.of(),true));
            clearInvocations(users,depts);
            assertThrows(jakarta.validation.ConstraintViolationException.class,()->proxy.resolve(List.of(-1L),List.of(),true));
            verifyNoInteractions(users,depts);
        }
    }
    private DeptRespDTO dept(long id,long parent,int status) { var d=new DeptRespDTO(); d.setId(id); d.setParentId(parent); d.setStatus(status); return d; }
    private AdminUserRespDTO user(long id,int status) { var u=new AdminUserRespDTO(); u.setId(id); u.setNickname("员工"+id); u.setStatus(status); return u; }
}
