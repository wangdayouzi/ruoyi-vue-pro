package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentPlanSaveReqVO;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

public final class SampleUrgentPlanRules {
    private SampleUrgentPlanRules() {}
    public static LocalDate today() { return LocalDate.now(ZoneId.of("Asia/Shanghai")); }
    public static LocalDate nextOccurrence(LocalDate from, LocalDate end, List<Integer> weekdays) {
        for (LocalDate date = from; !date.isAfter(end); date = date.plusDays(1)) {
            if (weekdays.contains(date.getDayOfWeek().getValue())) return date;
        }
        return null;
    }
    public static void validate(SampleUrgentPlanSaveReqVO req, LocalDate today) {
        if (req.getStartDate() == null || req.getEndDate() == null || req.getDailyTime() == null
                || req.getWeekdays() == null || req.getWeekdays().isEmpty()
                || req.getWeekdays().stream().anyMatch(d -> d == null || d < 1 || d > 7)) {
            throw invalidParamException("请完整填写持续日期、重复星期和每天截止时间");
        }
        if (req.getStartDate().isBefore(today) || req.getEndDate().isBefore(req.getStartDate()))
            throw invalidParamException("周期开始日期不能早于今天，结束日期不能早于开始日期");
        if (ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) >= 366)
            throw invalidParamException("单次周期计划最长366天，请分段预约");
        if (nextOccurrence(req.getStartDate(), req.getEndDate(), req.getWeekdays()) == null)
            throw invalidParamException("持续日期内没有符合所选星期的预约日期");
        if (req.getTemplate() == null || req.getTemplate().getId() != null)
            throw invalidParamException("周期计划必须使用新的预约信息");
        if (req.getTemplate().getExpectedArrival() != null || req.getTemplate().getSampleReceivedTime() != null
                || req.getTemplate().getPlannedTestTime() != null || req.getTemplate().getLogisticsNo() != null
                && !req.getTemplate().getLogisticsNo().isBlank())
            throw invalidParamException("周期计划不能复用一次性的到样、接收、检测时间或物流单号");
    }
}
