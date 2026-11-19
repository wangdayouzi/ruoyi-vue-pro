package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentSaveReqVO;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SampleUrgentPlanGenerator {
    private final SampleUrgentPlanMapper plans;
    private final SampleUrgentMapper records;
    private final SampleUrgentService service;

    /** 行锁串行化多实例调度；单据、日志、通知事件和游标在同一事务内提交。 */
    @Transactional(rollbackFor = Exception.class)
    public void generate(Long id, LocalDate today) {
        var plan = plans.lock(id);
        if (plan == null || plan.getStatus() != 0 || plan.getNextDate().isAfter(today)) return;
        LocalDate date = plan.getNextDate();
        while (!date.isAfter(today) && !date.isAfter(plan.getEndDate())) {
            if (plan.getWeekdays().contains(date.getDayOfWeek().getValue()) && records.countPlanDay(id, date) == 0) {
                var req = JsonUtils.parseObject(JsonUtils.toJsonString(plan.getTemplate()), SampleUrgentSaveReqVO.class);
                req.setDeadline(date.atTime(plan.getDailyTime()));
                service.createFromPlan(req, id, date, plan.getRequesterId());
            }
            date = date.plusDays(1);
        }
        LocalDate next = SampleUrgentPlanRules.nextOccurrence(date, plan.getEndDate(), plan.getWeekdays());
        plan.setNextDate(next == null ? plan.getEndDate().plusDays(1) : next);
        if (next == null) plan.setStatus(2);
        plan.setLastError("");
        // 自动生成不改变管理版本，避免调度与用户暂停产生无意义版本冲突。
        plans.updateById(plan);
    }
}
