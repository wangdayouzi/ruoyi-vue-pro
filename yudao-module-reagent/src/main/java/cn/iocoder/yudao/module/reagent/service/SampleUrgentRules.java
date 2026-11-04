package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

public final class SampleUrgentRules {
    private SampleUrgentRules() {}
    public static boolean isParty(SampleUrgentDO row,Long userId) {
        return Objects.equals(row.getRequesterId(),userId) || (row.getAppointmentUserIds()!=null && row.getAppointmentUserIds().contains(userId));
    }
    public static String normalizeBas(String value) {
        if (value == null) return "";
        return Arrays.stream(value.split("[,，、;；\\r\\n]+"))
                .map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.joining("、"));
    }
    public static void validateSave(SampleUrgentSaveReqVO req) {
        if (Boolean.TRUE.equals(req.getNewProject()) && normalizeBas(req.getBasNos()).isBlank())
            throw invalidParamException("新项目请填写 BAS 号");
        // 预计到样已退出新表单，仅保留历史值，不能用不可编辑的旧字段阻断预约改期。
    }
    public static void validateProgress(SampleUrgentActionReqVO req, SampleUrgentDO row) {
        if (req.getStatus()==null || !Set.of(1,2,3).contains(req.getStatus())) throw invalidParamException("请选择有效的处理进度");
        if (row.getStatus()==2 && req.getStatus()==1) throw invalidParamException("已经部分入库，请保留部分入库状态");
        if (req.getExceptionType()==null || !Set.of("NONE","NOT_ARRIVED","WAIT_REPLY","SAMPLE_ISSUE","DELAY","OTHER").contains(req.getExceptionType()))
            throw invalidParamException("请选择有效的异常类型");
        if (!"NONE".equals(req.getExceptionType()) && blank(req.getExceptionNote())) throw invalidParamException("请填写异常说明");
        if (req.getStatus()==3) {
            if (!"NONE".equals(req.getExceptionType())) throw invalidParamException("请先处理异常，再确认全部完成");
            if (!blank(req.getRemainingScope())) throw invalidParamException("仍有剩余范围，不能确认全部完成");
            if (req.getCompletedTime()==null || req.getCompletedTime().isAfter(LocalDateTime.now()))
                throw invalidParamException("请填写不晚于当前时间的实际入库时间");
            if (row.getCreateTime()!=null && req.getCompletedTime().isBefore(row.getCreateTime()))
                throw invalidParamException("实际入库时间不能早于需求登记时间");
        }
    }
    public static boolean blank(String value) { return value==null || value.isBlank(); }
}
