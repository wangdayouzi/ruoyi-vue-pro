package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentPlanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
@RequiredArgsConstructor
public class SampleUrgentPlanService {
    private final SampleUrgentPlanMapper plans;
    private final SampleUrgentPeopleService people;
    private final AdminUserApi users;
    private final SampleUrgentPlanGenerator generator;

    public PageResult<SampleUrgentPlanDO> page(PageParam req) {
        return plans.selectPage(req, new LambdaQueryWrapperX<SampleUrgentPlanDO>().orderByDesc(SampleUrgentPlanDO::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-加急入库", subType = "创建周期计划", bizNo = "{{#_ret}}", success = "创建周期预约计划")
    public Long create(SampleUrgentPlanSaveReqVO req) {
        var today = SampleUrgentPlanRules.today();
        SampleUrgentPlanRules.validate(req, today);
        var template = req.getTemplate();
        if (!Set.of("NINGBO", "SHANGHAI").contains(Objects.toString(template.getInboundSite(), "")))
            throw invalidParamException("请选择入库地点：上海或宁波");
        SampleUrgentRules.validateSave(template);
        people.prepare(template);
        template.setBasNos(SampleUrgentRules.normalizeBas(template.getBasNos()));
        template.setRevision(null);
        var first = SampleUrgentPlanRules.nextOccurrence(req.getStartDate(), req.getEndDate(), req.getWeekdays());
        template.setDeadline(first.atTime(req.getDailyTime()));
        var plan = new SampleUrgentPlanDO();
        plan.setTemplate(JsonUtils.parseObject(JsonUtils.toJsonString(template), SampleUrgentSaveReqVO.class));
        plan.setStartDate(req.getStartDate()); plan.setEndDate(req.getEndDate()); plan.setDailyTime(req.getDailyTime());
        plan.setWeekdays(req.getWeekdays().stream().distinct().sorted().toList());
        plan.setNextDate(first); plan.setStatus(0); plan.setRevision(0); plan.setLastError("");
        plan.setRequesterId(userId()); plan.setRequesterName(users.getUser(userId()).getNickname());
        plans.insert(plan);
        generator.generate(plan.getId(), today);
        return plan.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-加急入库", subType = "管理周期计划", bizNo = "{{#req.id}}", success = "周期计划操作：{{#req.action}}")
    public void action(SampleUrgentPlanActionReqVO req) {
        var plan = plans.lock(req.getId());
        if (plan == null) throw invalidParamException("周期计划不存在");
        if (!Objects.equals(plan.getRevision(), req.getRevision())) throw invalidParamException("计划已更新，请刷新后重试");
        if (!Objects.equals(plan.getRequesterId(), userId()) && !plan.getTemplate().getAppointmentUserIds().contains(userId()))
            throw invalidParamException("仅提交人或预约人可以管理周期计划");
        if (plan.getStatus() == 2) throw invalidParamException("计划已结束，请新建计划");
        switch (req.getAction()) {
            case "PAUSE" -> {
                if (plan.getStatus() != 0) throw invalidParamException("当前计划已暂停");
                plan.setStatus(1);
            }
            case "RESUME" -> {
                if (plan.getStatus() != 1) throw invalidParamException("当前计划未暂停");
                var from = plan.getNextDate().isBefore(SampleUrgentPlanRules.today()) ? SampleUrgentPlanRules.today() : plan.getNextDate();
                var next = SampleUrgentPlanRules.nextOccurrence(from, plan.getEndDate(), plan.getWeekdays());
                if (next == null) throw invalidParamException("计划已到期，请新建计划");
                plan.setNextDate(next); plan.setStatus(0); plan.setLastError("");
            }
            case "END" -> plan.setStatus(2);
            default -> throw invalidParamException("不支持的周期计划操作");
        }
        plan.setRevision(plan.getRevision() + 1);
        plans.updateById(plan);
        if ("RESUME".equals(req.getAction())) generator.generate(plan.getId(), SampleUrgentPlanRules.today());
    }

    /** 调整规则仅影响后续生成，已生成日期由唯一性检查保留原单据。 */
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-加急入库", subType = "调整预约周期", bizNo = "{{#req.id}}", success = "调整周期结束日期、重复星期及每日截止时间")
    public void schedule(SampleUrgentPlanScheduleReqVO req) {
        var plan = plans.lock(req.getId());
        if (plan == null || !Objects.equals(plan.getRevision(), req.getRevision())) throw invalidParamException("计划不存在或已更新，请刷新后重试");
        if (!Objects.equals(plan.getRequesterId(), userId()) && !plan.getTemplate().getAppointmentUserIds().contains(userId()))
            throw invalidParamException("仅提交人或预约人可以管理周期计划");
        if (plan.getStatus() == 2) throw invalidParamException("已结束的计划不能调整，请新建计划");
        var today = SampleUrgentPlanRules.today();
        if (req.getEndDate().isBefore(today) || req.getEndDate().isBefore(plan.getStartDate())
                || ChronoUnit.DAYS.between(plan.getStartDate(), req.getEndDate()) >= 366)
            throw invalidParamException("结束日期不能早于今天或开始日期，计划总跨度最长366天");
        var from = plan.getStartDate().isAfter(today) ? plan.getStartDate() : today;
        var next = SampleUrgentPlanRules.nextOccurrence(from, req.getEndDate(), req.getWeekdays());
        if (next == null) throw invalidParamException("调整后的周期内没有有效预约日期");
        plan.setEndDate(req.getEndDate()); plan.setDailyTime(req.getDailyTime());
        plan.setWeekdays(req.getWeekdays().stream().distinct().sorted().toList());
        plan.setNextDate(next); plan.setRevision(plan.getRevision() + 1);
        plans.updateById(plan);
        if (plan.getStatus() == 0) generator.generate(plan.getId(), today);
    }

    /** 失败后暂停，保留可读原因；不覆盖调度失败后已被用户更新的状态。 */
    public void pauseOnFailure(Long id, int revision, LocalDate expectedNextDate, String message) {
        String reason = Objects.toString(message, "生成失败，请联系管理员");
        plans.update(null, new LambdaUpdateWrapper<SampleUrgentPlanDO>()
                .eq(SampleUrgentPlanDO::getId, id).eq(SampleUrgentPlanDO::getRevision, revision)
                .eq(SampleUrgentPlanDO::getNextDate, expectedNextDate)
                .eq(SampleUrgentPlanDO::getStatus, 0).set(SampleUrgentPlanDO::getStatus, 1)
                .set(SampleUrgentPlanDO::getRevision, revision + 1)
                .set(SampleUrgentPlanDO::getLastError, reason.substring(0, Math.min(reason.length(), 1000))));
    }
    private Long userId() { return Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()); }
}
