package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import static org.junit.jupiter.api.Assertions.*;

class SampleUrgentPlanRulesTest {
    @Test void planDatesAndTimeSerializeAsStringsEvenWithTimestampSerializationEnabled() {
        var row = new SampleUrgentPlanDO();
        row.setStartDate(LocalDate.of(2026, 11, 19)); row.setEndDate(LocalDate.of(2026, 11, 25));
        row.setNextDate(LocalDate.of(2026, 11, 20)); row.setDailyTime(LocalTime.of(17, 30));
        var json = JsonUtils.toJsonString(row);
        assertTrue(json.contains("\"startDate\":\"2026-11-19\""), json);
        assertTrue(json.contains("\"endDate\":\"2026-11-25\""), json);
        assertTrue(json.contains("\"nextDate\":\"2026-11-20\""), json);
        assertTrue(json.contains("\"dailyTime\":\"17:30:00\""), json);
    }
    private final LocalDate today = LocalDate.of(2026, 11, 19); // 周四
    private SampleUrgentPlanSaveReqVO request() {
        var req = new SampleUrgentPlanSaveReqVO();
        req.setStartDate(today); req.setEndDate(today.plusDays(14));
        req.setDailyTime(LocalTime.of(17, 30)); req.setWeekdays(List.of(1, 2, 3, 4, 5));
        req.setTemplate(new SampleUrgentSaveReqVO());
        return req;
    }
    @Test void isoWeekdaysSkipWeekendAndIncludeLastDay() {
        assertEquals(LocalDate.of(2026, 11, 23), SampleUrgentPlanRules.nextOccurrence(
                LocalDate.of(2026, 11, 21), LocalDate.of(2026, 11, 23), List.of(1, 2, 3, 4, 5)));
        assertEquals(LocalDate.of(2026, 11, 22), SampleUrgentPlanRules.nextOccurrence(
                LocalDate.of(2026, 11, 21), LocalDate.of(2026, 11, 22), List.of(7)));
    }
    @Test void acceptsTodayAndRejectsEmptyOccurrenceAndPastStart() {
        var req = request();
        assertDoesNotThrow(() -> SampleUrgentPlanRules.validate(req, today));
        req.setEndDate(today); req.setWeekdays(List.of(1));
        assertThrows(RuntimeException.class, () -> SampleUrgentPlanRules.validate(req, today));
        req.setStartDate(today.minusDays(1));
        assertThrows(RuntimeException.class, () -> SampleUrgentPlanRules.validate(req, today));
    }
    @Test void limitsDurationAndCannotReuseExistingOrOneOffSampleInformation() {
        var req = request(); req.setEndDate(today.plusDays(366));
        assertThrows(RuntimeException.class, () -> SampleUrgentPlanRules.validate(req, today));
        req.setEndDate(today.plusDays(365));
        assertDoesNotThrow(() -> SampleUrgentPlanRules.validate(req, today));
        req.getTemplate().setId(1L);
        assertThrows(RuntimeException.class, () -> SampleUrgentPlanRules.validate(req, today));
        req.getTemplate().setId(null); req.getTemplate().setSampleReceivedTime(today.atStartOfDay());
        assertThrows(RuntimeException.class, () -> SampleUrgentPlanRules.validate(req, today));
    }
}
