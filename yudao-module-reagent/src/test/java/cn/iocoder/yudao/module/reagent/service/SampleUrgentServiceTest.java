package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SampleUrgentServiceTest {
    private SampleUrgentService service;
    private SampleUrgentMapper mapper;
    private SampleUrgentLogMapper logs;
    private PermissionApi permissions;
    private SampleUrgentDO row;
    @BeforeEach void setup() {
        service=new SampleUrgentService(); mapper=mock(SampleUrgentMapper.class);
        logs=mock(SampleUrgentLogMapper.class); permissions=mock(PermissionApi.class);
        var users=mock(AdminUserApi.class);
        ReflectionTestUtils.setField(service,"mapper",mapper); ReflectionTestUtils.setField(service,"logs",logs);
        ReflectionTestUtils.setField(service,"users",users); ReflectionTestUtils.setField(service,"permissions",permissions);
        ReflectionTestUtils.setField(service,"events",mock(org.springframework.context.ApplicationEventPublisher.class));
        var people=mock(SampleUrgentPeopleService.class);
        doAnswer(i -> { SampleUrgentSaveReqVO r=i.getArgument(0); if(r.getAppointmentUserIds()==null) r.setAppointmentUserIds(java.util.List.of(10L)); if(r.getNoticeUserIds()==null) r.setNoticeUserIds(java.util.List.of(20L)); if(r.getNoticeDeptIds()==null) r.setNoticeDeptIds(java.util.List.of()); return null; }).when(people).prepare(any());
        ReflectionTestUtils.setField(service,"people",people);
        cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.setTenantId(1L);
        var user=new AdminUserRespDTO(); user.setNickname("测试人员"); when(users.getUser(10L)).thenReturn(user);
        var login=new LoginUser(); login.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login,null));
        row=new SampleUrgentDO(); row.setId(1L); row.setRevision(3); row.setStatus(0); row.setRequesterId(10L);
        row.setCreateTime(LocalDateTime.now().minusDays(1));
        when(mapper.lock(1L)).thenReturn(row);
    }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.clear(); }
    @Test void staleVersionCannotOverwriteNewProgress() {
        var req=action("CLAIM"); req.setRevision(2);
        assertThrows(ServiceException.class,()->service.action(req));
        verify(mapper,never()).updateById(any(SampleUrgentDO.class)); verifyNoInteractions(logs);
    }
    @Test void queryPermissionAloneDoesNotAllowHandling() {
        assertThrows(ServiceException.class,()->service.action(action("CLAIM")));
        verifyNoInteractions(logs);
    }
    @Test void anotherRequesterCannotCancelSomeoneElsesDemand() {
        row.setRequesterId(20L); when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:update")).thenReturn(true);
        var req=action("CANCEL"); req.setContent("取消");
        assertThrows(ServiceException.class,()->service.action(req)); verifyNoInteractions(logs);
    }
    @Test void samplesCanBeClaimedOnceAndRevisionAdvances() {
        allowHandle(); service.action(action("CLAIM"));
        assertEquals(10L,row.getHandlerId()); assertEquals(1,row.getStatus()); assertEquals(4,row.getRevision());
        verify(mapper).updateById(row); verify(logs).insert(any(SampleUrgentLogDO.class));
    }
    @Test void anotherHandlerCannotOverwriteCurrentHandlersWork() {
        allowHandle(); row.setHandlerId(20L);
        var req=action("PROGRESS"); req.setStatus(1); req.setExceptionType("NONE");
        assertThrows(ServiceException.class,()->service.action(req)); verifyNoInteractions(logs);
    }
    @Test void completedDemandCannotBeCancelledOrEditedThroughProgress() {
        allowHandle(); row.setStatus(3);
        assertThrows(ServiceException.class,()->service.action(action("PROGRESS"))); verifyNoInteractions(logs);
    }
    @Test void commentDoesNotClearExceptionsOrCompletePartialInbound() {
        row.setStatus(2); row.setExceptionType("WAIT_REPLY"); row.setRemainingScope("一管待回复");
        var req=action("COMMENT"); req.setContent("已补充资料，请样品组核对"); service.action(req);
        assertEquals(2,row.getStatus()); assertEquals("WAIT_REPLY",row.getExceptionType());
        verify(mapper,never()).updateById(any(SampleUrgentDO.class)); verify(logs).insert(any(SampleUrgentLogDO.class));
    }
    @Test void newDemandAlwaysStartsPendingAndGetsCurrentRequester() {
        var req=new SampleUrgentSaveReqVO(); req.setProjectNo("  PROJECT-1 "); req.setContacts("项目联系人");
        req.setSampleScope("全部PK管"); req.setReason("次日上午检测"); req.setDeadline(LocalDateTime.now().plusDays(1));
        req.setNewProject(false); req.setBasNos("26BAS0001,26BAS0002-Blind;"); req.setRevision(99);
        doAnswer(inv -> { SampleUrgentDO inserted=inv.getArgument(0); inserted.setId(2L);
            assertEquals(0,inserted.getStatus()); assertEquals(0,inserted.getRevision()); assertEquals(10L,inserted.getRequesterId());
            assertEquals("26BAS0001、26BAS0002-Blind",inserted.getBasNos()); return 1;
        }).when(mapper).insert(any(SampleUrgentDO.class));
        assertEquals(2L,service.save(req));
    }
    @Test void appointedPersonCanCancelAndCommentWithoutBecomingSubmitter() {
        row.setRequesterId(20L); row.setAppointmentUserIds(java.util.List.of(10L,30L));
        when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:update")).thenReturn(true);
        var comment=action("COMMENT"); comment.setContent("预约人补充"); service.action(comment);
        var cancel=action("CANCEL"); cancel.setContent("预约取消"); service.action(cancel);
        assertEquals(20L,row.getRequesterId()); assertEquals(4,row.getStatus());
    }
    private void allowHandle() { when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:handle")).thenReturn(true); }
    @Test void registrationWithoutTenantContextDoesNotFailAtNoticePublication() {
        cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.clear();
        var publisher=mock(org.springframework.context.ApplicationEventPublisher.class);
        ReflectionTestUtils.setField(service,"events",publisher);
        var req=new SampleUrgentSaveReqVO(); req.setProjectNo("SINGLE-TENANT"); req.setContacts("项目人员");
        req.setSampleScope("全部PK管"); req.setReason("次日检测"); req.setDeadline(LocalDateTime.now().plusDays(1)); req.setNewProject(false);
        doAnswer(i -> { ((SampleUrgentDO)i.getArgument(0)).setId(2L); return 1; }).when(mapper).insert(any(SampleUrgentDO.class));
        assertEquals(2L,service.save(req));
        var event=org.mockito.ArgumentCaptor.forClass(SampleUrgentNoticeEvent.class);
        verify(publisher).publishEvent(event.capture()); assertNull(event.getValue().tenantId());
    }
    private SampleUrgentActionReqVO action(String action) {
        var req=new SampleUrgentActionReqVO(); req.setId(1L); req.setRevision(3); req.setAction(action); return req;
    }
}
