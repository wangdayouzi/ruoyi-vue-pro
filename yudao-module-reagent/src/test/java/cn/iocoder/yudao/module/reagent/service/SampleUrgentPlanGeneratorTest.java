package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentSaveReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import org.junit.jupiter.api.*;
import java.time.*;
import java.util.List;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SampleUrgentPlanGeneratorTest {
    private final SampleUrgentPlanMapper plans = mock(SampleUrgentPlanMapper.class);
    private final SampleUrgentMapper records = mock(SampleUrgentMapper.class);
    private final SampleUrgentService service = mock(SampleUrgentService.class);
    private final SampleUrgentPlanGenerator generator = new SampleUrgentPlanGenerator(plans, records, service);
    private SampleUrgentPlanDO plan;
    private final LocalDate friday = LocalDate.of(2026, 11, 20);
    @BeforeEach void setup() {
        plan = new SampleUrgentPlanDO(); plan.setId(1L); plan.setRequesterId(10L); plan.setStatus(0); plan.setRevision(0);
        plan.setNextDate(friday); plan.setEndDate(friday.plusDays(7)); plan.setDailyTime(LocalTime.of(17, 30));
        plan.setWeekdays(List.of(1, 2, 3, 4, 5));
        var template = new SampleUrgentSaveReqVO(); template.setProjectNo("PLAN-A"); template.setBasNos("BAS-A");
        plan.setTemplate(template); when(plans.lock(1L)).thenReturn(plan);
    }
    @Test void catchesUpDueWeekdaysAndNeverGeneratesFutureDates() {
        generator.generate(1L, friday.plusDays(3)); // 周一
        verify(service).createFromPlan(argThat(r -> r.getDeadline().equals(friday.atTime(17, 30))), eq(1L), eq(friday), eq(10L));
        verify(service).createFromPlan(argThat(r -> r.getDeadline().equals(friday.plusDays(3).atTime(17, 30))), eq(1L), eq(friday.plusDays(3)), eq(10L));
        verify(service, times(2)).createFromPlan(any(), anyLong(), any(), anyLong());
        assertEquals(friday.plusDays(4), plan.getNextDate());
        assertNull(plan.getTemplate().getDeadline(), "不能修改计划模板上的时间");
    }
    @Test void repeatRunsAndExistingIncludingDeletedDayDoNotDuplicate() {
        when(records.countPlanDay(1L, friday)).thenReturn(1L);
        generator.generate(1L, friday);
        generator.generate(1L, friday);
        verifyNoInteractions(service);
        assertEquals(friday.plusDays(3), plan.getNextDate());
    }
    @Test void pausedEndedAndFuturePlansDoNothing() {
        plan.setStatus(1); generator.generate(1L, friday);
        plan.setStatus(2); generator.generate(1L, friday);
        plan.setStatus(0); plan.setNextDate(friday.plusDays(1)); generator.generate(1L, friday);
        verifyNoInteractions(service, records);
        verify(plans, never()).updateById(any(SampleUrgentPlanDO.class));
    }
    @Test void finalDayEndsPlanWithoutChangingExistingDailyRecords() {
        plan.setEndDate(friday); generator.generate(1L, friday);
        assertEquals(2, plan.getStatus()); assertEquals(friday.plusDays(1), plan.getNextDate());
        verify(records, never()).updateById(any(SampleUrgentDO.class));
    }
    @Test void failureDoesNotAdvanceCursor() {
        when(service.createFromPlan(any(), anyLong(), any(), anyLong())).thenThrow(new IllegalStateException("生成失败"));
        assertThrows(IllegalStateException.class, () -> generator.generate(1L, friday));
        assertEquals(friday, plan.getNextDate());
        verify(plans, never()).updateById(any(SampleUrgentPlanDO.class));
    }
}
