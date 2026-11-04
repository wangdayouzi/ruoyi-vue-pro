package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentSaveReqVO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SampleUrgentPeopleServiceTest {
    AdminUserApi users;
    DeptApi depts;
    PermissionApi permissions;
    SampleUrgentPeopleService service;
    cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi recipients;
    AdminUserRespDTO project,admin,other;
    @BeforeEach void setup() {
        users=mock(AdminUserApi.class); depts=mock(DeptApi.class); permissions=mock(PermissionApi.class);
        recipients=mock(cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationRecipientApi.class);
        when(recipients.resolve(any(),any(),eq(true))).thenAnswer(i -> { var ids=new LinkedHashSet<Long>(); List<Long> direct=i.getArgument(0); List<Long> deptIds=i.getArgument(1); if(direct!=null) ids.addAll(direct); if(deptIds!=null && !deptIds.isEmpty()) ids.addAll(List.of(20L,21L)); return new ArrayList<>(ids); });
        service=new SampleUrgentPeopleService(users,depts,mock(DeptMapper.class),permissions,recipients);
        project=user(10L,"预约人员"); admin=user(20L,"样品管理员"); other=user(21L,"普通人员");
        when(users.getUserList(anyCollection())).thenAnswer(i -> { Collection<Long> ids=i.getArgument(0); return List.of(project,admin,other).stream().filter(u->ids.contains(u.getId())).toList(); });
        when(users.getUserMap(anyCollection())).thenReturn(Map.of(10L,project,20L,admin,21L,other));
        when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:query")).thenReturn(true);
        when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:create","reagent:sample-urgent:update")).thenReturn(true);
        when(permissions.hasAnyPermissions(20L,"reagent:sample-urgent:query")).thenReturn(true);
        when(permissions.hasAnyPermissions(20L,"reagent:sample-urgent:handle")).thenReturn(true);
        when(depts.getChildDeptList(anyCollection())).thenReturn(List.of(dept(101L)));
        when(depts.getDeptList(anyCollection())).thenReturn(List.of(dept(100L),dept(101L)));
        when(users.getUserListByDeptIds(anyCollection())).thenReturn(List.of(admin,other));
    }
    @Test void selectingDepartmentAndPersonDeduplicatesAndExcludesNonAdministrators() {
        assertEquals(Set.of(20L),service.administrators(List.of(20L),List.of(100L)));
        when(permissions.hasAnyPermissions(20L,"reagent:sample-urgent:handle")).thenReturn(false); assertTrue(service.administrators(List.of(20L),List.of(100L)).isEmpty());
        verify(depts,never()).getChildDeptList(anyCollection());
        verify(users,never()).getUserListByDeptIds(anyCollection());
    }
    @Test void reservationNamesComeFromAccountsRatherThanClientText() {
        var req=request(); req.setAppointmentUserIds(List.of(10L,10L)); req.setContacts("伪造姓名");
        service.prepare(req);
        assertEquals(List.of(10L),req.getAppointmentUserIds()); assertEquals("预约人员",req.getContacts());
    }
    @Test void allowsEmptyNoticeTargetsAndRejectsUnauthorizedPeople() {
        var req=request(); req.setNoticeUserIds(List.of());
        var missingTarget=req;
        assertDoesNotThrow(()->service.prepare(missingTarget));
        req=request(); req.setAppointmentUserIds(List.of(21L)); var badAppointment=req;
        assertThrows(RuntimeException.class,()->service.prepare(badAppointment));
        req=request(); req.setNoticeUserIds(List.of(21L)); var badAdministrator=req;
        assertThrows(RuntimeException.class,()->service.prepare(badAdministrator));
    }
    private SampleUrgentSaveReqVO request() {
        var req=new SampleUrgentSaveReqVO(); req.setAppointmentUserIds(List.of(10L));
        req.setNoticeUserIds(List.of(20L)); req.setNoticeDeptIds(List.of()); return req;
    }
    private AdminUserRespDTO user(long id,String name) { var u=new AdminUserRespDTO(); u.setId(id); u.setNickname(name); u.setStatus(0); return u; }
    private DeptRespDTO dept(long id) { var d=new DeptRespDTO(); d.setId(id); d.setStatus(0); return d; }
}
