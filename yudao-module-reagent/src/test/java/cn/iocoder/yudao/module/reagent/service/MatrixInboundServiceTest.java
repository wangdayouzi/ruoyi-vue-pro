package cn.iocoder.yudao.module.reagent.service;

import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.dingtalk.*;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatrixInboundServiceTest {
    private MatrixInboundService service;
    private MatrixInboundMapper mapper;
    private MatrixInboundCounterMapper counters;
    private MatrixInboundLogMapper logs;
    private MatrixInboundNotifier notifier;
    private AdminUserApi users;
    private PermissionApi permissions;
    private DingTalkNotificationQueue queue;

    @BeforeEach void setup() {
        service = new MatrixInboundService();
        mapper = mock(MatrixInboundMapper.class);
        counters = mock(MatrixInboundCounterMapper.class);
        logs = mock(MatrixInboundLogMapper.class);
        users = mock(AdminUserApi.class);
        permissions = mock(PermissionApi.class);
        notifier = new MatrixInboundNotifier();
        queue = mock(DingTalkNotificationQueue.class);
        ReflectionTestUtils.setField(notifier, "templates", mock(DingTalkMessageSendApi.class));
        ReflectionTestUtils.setField(notifier, "queue", queue);
        ReflectionTestUtils.setField(notifier, "permissions", permissions);
        ReflectionTestUtils.setField(service, "mapper", mapper);
        ReflectionTestUtils.setField(service, "counters", counters);
        ReflectionTestUtils.setField(service, "logs", logs);
        ReflectionTestUtils.setField(service, "users", users);
        ReflectionTestUtils.setField(service, "permissions", permissions);
        ReflectionTestUtils.setField(service, "notifier", notifier);
        ReflectionTestUtils.setField(service, "exporter", new MatrixInboundExporter());
        var login = new LoginUser();
        login.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, null));
        TenantContextHolder.clear();
    }

    @AfterEach void cleanup() {
        SecurityContextHolder.clearContext();
        TenantContextHolder.clear();
    }

    @Test void singleTenantQueryShowsAllRecordsWithoutApplicantRestriction() {
        var filter = new MatrixInboundPageReqVO();
        when(mapper.page(eq(filter), eq(1L), eq(10L), any())).thenReturn(new PageResult<>(List.of(), 0L));
        assertTrue(service.page(filter).getList().isEmpty());
        verify(mapper).page(eq(filter), eq(1L), eq(10L), any());
    }

    @Test void invertedDateRangeIsRejectedBeforeQuery() {
        var filter = new MatrixInboundPageReqVO();
        filter.setFrom(LocalDate.of(2026, 11, 20));
        filter.setTo(LocalDate.of(2026, 11, 19));
        assertThrows(RuntimeException.class, () -> service.page(filter));
        verifyNoInteractions(mapper);
    }

    @Test void registerPermissionAllowsBothSitesWithoutNotificationConfiguration() {
        when(permissions.hasAnyPermissions(10L, MatrixInboundSupport.QUERY_PERMISSION)).thenReturn(true);
        when(permissions.hasAnyPermissions(10L, MatrixInboundSupport.REGISTER_PERMISSION)).thenReturn(true);
        assertEquals(List.of("NINGBO", "SHANGHAI"), service.managedSites());
    }

    @Test void queryPermissionAloneDoesNotAllowHandling() {
        when(permissions.hasAnyPermissions(10L, MatrixInboundSupport.QUERY_PERMISSION)).thenReturn(true);
        assertTrue(service.managedSites().isEmpty());
    }

    @Test void staleOrCompletedRecordCannotBeEdited() {
        MatrixInboundDO row = row(3, false);
        when(mapper.lock(1L, 9L)).thenReturn(row);
        assertThrows(RuntimeException.class, () -> service.update(new MatrixInboundUpdateReqVO(
                9L, 4, row.getAppointmentDate(), true, false, List.of(10L))));
        verify(mapper, never()).updateCurrent(any(), any());
    }

    @Test void staleDeleteDoesNotCancelNotification() {
        when(mapper.lock(1L, 9L)).thenReturn(row(0, false));
        assertThrows(RuntimeException.class, () -> service.delete(9L, 2));
        verifyNoInteractions(queue);
    }

    @Test void softDeleteKeepsNumberAndCancelsPendingNotifications() {
        when(mapper.lock(1L, 9L)).thenReturn(row(0, false));
        when(mapper.deleteCurrent(eq(1L), eq(9L), eq(4), eq(10L), any())).thenReturn(1);
        service.delete(9L, 4);
        verify(queue).cancel("matrix-inbound:9");
        verify(queue).cancel("matrix-deadline:9:4:soon");
        verifyNoInteractions(counters);
    }

    @Test void completedRecordCanChangeDirectlyToPartialInbound() {
        MatrixInboundDO row = row(3, false);
        when(mapper.lock(1L, 9L)).thenReturn(row);
        when(mapper.updateCurrent(any(), eq(4))).thenReturn(1);
        when(permissions.hasAnyPermissions(eq(10L), anyString())).thenReturn(true);
        AdminUserRespDTO operator = new AdminUserRespDTO();
        operator.setNickname("样管");
        when(users.getUser(10L)).thenReturn(operator);
        service.register(new MatrixInboundRegisterReqVO(9L, 4, 2, "部分完成"));
        assertEquals(2, row.getInboundStatus());
        assertEquals(5, row.getVersion());
        var captured = ArgumentCaptor.forClass(MatrixInboundLogDO.class);
        verify(logs).insert(captured.capture());
        assertEquals(3, captured.getValue().getFromStatus());
        assertEquals(2, captured.getValue().getToStatus());
    }

    @Test void registrationCannotResetToBookedState() {
        assertThrows(RuntimeException.class, () -> service.register(new MatrixInboundRegisterReqVO(9L, 4, 0, "")));
        verifyNoInteractions(mapper, queue);
    }

    @Test void basExportWritesFourSheetsWithRegistrationAndSubmissionDetails() throws Exception {
        MatrixInboundDO row = row(2, false);
        row.setSite("NINGBO");
        row.setMatrixKind("P");
        row.setBasNo("26BAS0001P");
        row.setRegisteredName("样管");
        row.setRegisteredAt(LocalDateTime.of(2026, 11, 20, 15, 30));
        var filter = new MatrixInboundPageReqVO();
        filter.setDateOrder("desc");
        when(mapper.export(eq(filter), eq(1L), eq(10L), any(), eq(true))).thenReturn(List.of(row));
        var response = new MockHttpServletResponse();
        service.exportBas(response, filter);
        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(4, workbook.getNumberOfSheets());
            var sheet = workbook.getSheet("NB混合基质");
            assertEquals(7, sheet.getRow(0).getLastCellNum());
            assertEquals(24 * 256, sheet.getColumnWidth(0));
            assertTrue(sheet.getPaneInformation().isFreezePane());
            assertEquals("26BAS0001P", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("9", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("部分入库", sheet.getRow(1).getCell(3).getStringCellValue());
            assertEquals("样管", sheet.getRow(1).getCell(4).getStringCellValue());
        }
    }

    @Test void managerNotificationsOnlyRequireUrgencyInShanghai() {
        for (String site : List.of("NINGBO", "SHANGHAI")) {
            for (boolean urgent : List.of(false, true)) {
                reset(queue);
                MatrixInboundDO row = row(0, urgent);
                row.setSite(site);
                row.setAppointmentDate(MatrixInboundSupport.now().minusMinutes(1));
                notifier.created(row);
                if ("NINGBO".equals(site) || urgent) verify(queue).enqueuePlan(any(), any());
                else verifyNoInteractions(queue);
            }
        }
    }

    @Test void shanghaiNonUrgentStatusUpdateStillNotifiesSubmitter() {
        MatrixInboundDO row = row(3, false);
        row.setSite("SHANGHAI");
        notifier.registered(row);
        verify(queue).enqueuePlan(any(), any());
    }

    @Test void urgentReservationSchedulesOnlyTwoHourReminder() {
        MatrixInboundDO row = row(2, true);
        notifier.schedule(row);
        var plans = ArgumentCaptor.forClass(DingTalkNotificationPlanReqDTO.class);
        verify(queue).enqueuePlan(plans.capture(), any());
        assertEquals(row.getAppointmentDate().minusHours(2), plans.getValue().getSendAfter());
        assertEquals(row.getAppointmentDate(), plans.getValue().getExpiresAt());
    }

    @Test void ordinaryAndCompletedReservationsDoNotScheduleReminders() {
        for (MatrixInboundDO row : List.of(row(0, false), row(3, true))) {
            notifier.schedule(row);
        }
        verifyNoInteractions(queue);
    }

    @Test void reminderGuardRejectsOldVersionAndExpiredDeadline() {
        MatrixInboundDO row = row(2, true);
        row.setAppointmentDate(MatrixInboundSupport.now().plusMinutes(30));
        when(mapper.selectByTenantId(1L, 9L)).thenReturn(row);
        var guard = new MatrixInboundNotificationGuard(mapper);
        assertTrue(guard.applicable("matrix-deadline:9:4:soon", row.getAppointmentDate()));
        assertFalse(guard.applicable("matrix-deadline:9:3:soon", row.getAppointmentDate()));
        row.setAppointmentDate(MatrixInboundSupport.now().minusMinutes(1));
        assertFalse(guard.applicable("matrix-deadline:9:4:soon", row.getAppointmentDate()));
    }

    @Test void alreadyOverdueUrgentReservationDoesNotScheduleAnything() {
        MatrixInboundDO row = row(2, true);
        row.setAppointmentDate(MatrixInboundSupport.now().minusMinutes(1));
        notifier.schedule(row);
        verifyNoInteractions(queue);
    }

    private MatrixInboundDO row(int status, boolean urgent) {
        MatrixInboundDO row = new MatrixInboundDO();
        row.setId(9L);
        row.setTenantId(1L);
        row.setSite("NINGBO");
        row.setMatrixKind("B");
        row.setBasNo("26BAS0001B");
        row.setApplicantId(10L);
        row.setApplicantName("预约人");
        row.setAppointmentUserIds(List.of(10L));
        row.setCreatedAt(LocalDateTime.of(2026, 11, 19, 9, 0));
        row.setAppointmentDate(MatrixInboundSupport.now().plusHours(6).withNano(0));
        row.setUrgent(urgent);
        row.setInboundStatus(status);
        row.setVersion(4);
        row.setDeleted(false);
        return row;
    }
}
