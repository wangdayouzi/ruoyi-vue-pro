package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentPlanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SampleUrgentPlanServiceTest {
    private final SampleUrgentPlanMapper plans = mock(SampleUrgentPlanMapper.class);
    private final SampleUrgentPlanGenerator generator = mock(SampleUrgentPlanGenerator.class);
    private final SampleUrgentPlanService service = new SampleUrgentPlanService(plans, mock(SampleUrgentPeopleService.class), mock(AdminUserApi.class), generator);
    private SampleUrgentPlanDO plan;
    @BeforeEach void setup() {
        var login = new LoginUser(); login.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, null));
        plan = new SampleUrgentPlanDO(); plan.setId(1L); plan.setRevision(3); plan.setStatus(1); plan.setRequesterId(10L);
        plan.setStartDate(SampleUrgentPlanRules.today().minusDays(5)); plan.setEndDate(SampleUrgentPlanRules.today().plusDays(5));
        plan.setNextDate(SampleUrgentPlanRules.today().minusDays(3)); plan.setWeekdays(List.of(1,2,3,4,5,6,7));
        plan.setTemplate(new SampleUrgentSaveReqVO()); plan.getTemplate().setAppointmentUserIds(List.of(20L));
        when(plans.lock(1L)).thenReturn(plan);
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    private SampleUrgentPlanActionReqVO action(String action) {
        var req = new SampleUrgentPlanActionReqVO(); req.setId(1L); req.setRevision(3); req.setAction(action); return req;
    }
    @Test void staleVersionAndOtherPeopleCannotManagePlan() {
        var req = action("END"); req.setRevision(2);
        assertThrows(RuntimeException.class, () -> service.action(req));
        req.setRevision(3); plan.setRequesterId(30L);
        assertThrows(RuntimeException.class, () -> service.action(req));
        verifyNoInteractions(generator);
    }
    @Test void resumeSkipsPausedDaysAndGeneratesToday() {
        service.action(action("RESUME"));
        assertEquals(SampleUrgentPlanRules.today(), plan.getNextDate()); assertEquals(0, plan.getStatus());
        assertEquals(4, plan.getRevision()); verify(generator).generate(1L, SampleUrgentPlanRules.today());
    }
    @Test void endingPlanDoesNotCancelExistingDailyRecords() {
        service.action(action("END"));
        assertEquals(2, plan.getStatus()); verifyNoInteractions(generator);
    }
    @Test void endedPlanCannotResumeAndExpiredPlanCannotResume() {
        plan.setStatus(2); assertThrows(RuntimeException.class, () -> service.action(action("RESUME")));
        plan.setStatus(1); plan.setEndDate(SampleUrgentPlanRules.today().minusDays(1));
        assertThrows(RuntimeException.class, () -> service.action(action("RESUME")));
    }
    @Test void scheduleCanExtendPausedPlanWithoutGeneratingOrChangingDailyRecords() {
        var req = new SampleUrgentPlanScheduleReqVO(); req.setId(1L); req.setRevision(3);
        req.setEndDate(SampleUrgentPlanRules.today().plusDays(8)); req.setDailyTime(LocalTime.of(12, 0));
        req.setWeekdays(List.of(1, 2, 3, 4, 5, 6, 7));
        service.schedule(req);
        assertEquals(1, plan.getStatus()); assertEquals(4, plan.getRevision());
        assertEquals(req.getEndDate(), plan.getEndDate()); assertEquals(req.getDailyTime(), plan.getDailyTime());
        verifyNoInteractions(generator);
    }
    @Test void scheduleRejectsPastEndAndEndedPlans() {
        var req = new SampleUrgentPlanScheduleReqVO(); req.setId(1L); req.setRevision(3);
        req.setEndDate(SampleUrgentPlanRules.today().minusDays(1)); req.setDailyTime(LocalTime.NOON);
        req.setWeekdays(List.of(1, 2, 3, 4, 5));
        assertThrows(RuntimeException.class, () -> service.schedule(req));
        req.setEndDate(SampleUrgentPlanRules.today().plusDays(3)); plan.setStatus(2);
        assertThrows(RuntimeException.class, () -> service.schedule(req));
        verifyNoInteractions(generator);
    }
}
