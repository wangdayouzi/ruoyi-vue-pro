package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.reagent.enums.MatrixInboundStatusEnum;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.reagent.service.MatrixInboundSupport.*;

@Service
@Validated
public class MatrixInboundService {
    private static final int EXPORT_LIMIT = 50000;
    @Resource private MatrixInboundMapper mapper;
    @Resource private MatrixInboundCounterMapper counters;
    @Resource private MatrixInboundLogMapper logs;
    @Resource private AdminUserApi users;
    @Resource private PermissionApi permissions;
    @Resource private MatrixInboundNotifier notifier;
    @Resource private MatrixInboundExporter exporter;

    public List<String> managedSites() {
        Long userId = getLoginUserId();
        return permissions.hasAnyPermissions(userId, QUERY_PERMISSION) && permissions.hasAnyPermissions(userId, REGISTER_PERMISSION)
                ? List.of("NINGBO", "SHANGHAI") : List.of();
    }

    public List<String> listBasOptions(String keyword) {
        return mapper.selectBasOptions(tenantId(), keyword == null ? "" : keyword.trim());
    }

    public PageResult<MatrixInboundRespVO> page(MatrixInboundPageReqVO filter) {
        validateFilter(filter);
        PageResult<MatrixInboundDO> result = mapper.page(filter, tenantId(), getLoginUserId(), now().toLocalDate());
        return new PageResult<>(result.getList().stream().map(this::response).toList(), result.getTotal());
    }

    public MatrixInboundRespVO get(Long id) {
        return response(requireRecord(mapper.selectByTenantId(tenantId(), id)));
    }

    public List<MatrixInboundLogRespVO> logs(Long id) {
        get(id);
        return logs.selectByInbound(tenantId(), id).stream()
                .map(row -> BeanUtils.toBean(row, MatrixInboundLogRespVO.class)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public MatrixInboundRespVO create(@Valid MatrixInboundCreateReqVO input) {
        long currentTenant = tenantId();
        Long userId = getLoginUserId();
        String names = appointmentNames(input.appointmentUserIds());
        LocalDateTime current = now();

        // 请求锁先于流水锁：同一次提交只占一个号码，并与单据、通知任务一起提交或回滚。
        mapper.lockRequest("matrix-inbound:" + currentTenant + ":" + userId + ":" + input.requestId());
        MatrixInboundDO existing = mapper.selectByRequest(currentTenant, userId, input.requestId().toString());
        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getDeleted())) throw invalidParamException("该预约已删除，请重新新建预约");
            if (!sameRequest(existing, input)) throw invalidParamException("提交标识已用于其他预约内容");
            return response(existing);
        }

        int year = current.getYear();
        counters.ensureCounter(currentTenant, input.site(), year, input.matrixKind());
        Integer previousNo = counters.lockCounter(currentTenant, input.site(), year, input.matrixKind());
        if (previousNo == null) throw invalidParamException("无法获取基质流水");
        if (previousNo >= 9999) throw invalidParamException("本年度 BAS 流水已达到 9999");
        // 锁内递增并校验原值，单据或通知落库失败时，流水随当前事务一起回滚。
        if (counters.advanceCounter(currentTenant, input.site(), year, input.matrixKind(), previousNo) != 1)
            throw invalidParamException("基质流水更新失败，请重试");

        MatrixInboundDO row = new MatrixInboundDO();
        row.setTenantId(currentTenant);
        row.setSite(input.site());
        row.setMatrixKind(input.matrixKind());
        row.setBasNo(String.format(Locale.ROOT, "%02dBAS%04d%s", year % 100, previousNo + 1, input.matrixKind()));
        row.setApplicantId(userId);
        row.setApplicantName(names);
        row.setAppointmentUserIds(List.copyOf(input.appointmentUserIds()));
        row.setRequestId(input.requestId());
        row.setCreatedAt(current);
        row.setAppointmentDate(input.appointmentDate());
        row.setLimsDesigned(input.limsDesigned());
        row.setUrgent(input.urgent());
        row.setInboundStatus(MatrixInboundStatusEnum.BOOKED.getValue());
        row.setVersion(0);
        row.setDeleted(false);
        mapper.insert(row);
        notifier.created(row);
        return response(row);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(@Valid MatrixInboundUpdateReqVO input) {
        MatrixInboundDO row = locked(input.id(), input.version());
        ensureEditable(row);
        row.setApplicantName(appointmentNames(input.appointmentUserIds()));
        row.setAppointmentUserIds(List.copyOf(input.appointmentUserIds()));
        row.setAppointmentDate(input.appointmentDate());
        row.setLimsDesigned(input.limsDesigned());
        row.setUrgent(input.urgent());
        save(row, input.version());
        notifier.updated(row);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Integer version) {
        MatrixInboundDO row = locked(id, version);
        ensureEditable(row);
        if (mapper.deleteCurrent(tenantId(), id, version, getLoginUserId(), now()) != 1) staleRecord();
        // 逻辑删除保留编号和请求标识，避免后续重试或清理时重复使用已发 BAS。
        notifier.deleted(id, version);
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(@Valid MatrixInboundRegisterReqVO input) {
        MatrixInboundStatusEnum target = MatrixInboundStatusEnum.of(input.inboundStatus());
        if (target == MatrixInboundStatusEnum.BOOKED) throw invalidParamException("入库登记不能改回已预约");
        MatrixInboundDO row = locked(input.id(), input.version());
        if (!managedSites().contains(row.getSite())) throw invalidParamException("无权登记该站点预约");
        var operator = users.getUser(getLoginUserId());
        if (operator == null) throw invalidParamException("登记人不存在");
        Integer previous = row.getInboundStatus();
        row.setInboundStatus(target.getValue());
        row.setLimsInbound(target == MatrixInboundStatusEnum.INBOUND);
        row.setExceptionRemark(input.exceptionRemark());
        row.setRegisteredBy(getLoginUserId());
        row.setRegisteredName(operator.getNickname());
        row.setRegisteredAt(now());
        save(row, input.version());

        MatrixInboundLogDO log = new MatrixInboundLogDO();
        log.setTenantId(row.getTenantId());
        log.setInboundId(row.getId());
        log.setAction(previous == MatrixInboundStatusEnum.BOOKED.getValue() ? "REGISTER" : "STATUS_CHANGE");
        log.setFromStatus(previous);
        log.setToStatus(target.getValue());
        log.setRemark(input.exceptionRemark());
        log.setOperatorId(getLoginUserId());
        log.setOperatorName(operator.getNickname());
        log.setCreatedAt(now());
        logs.insert(log);
        notifier.registered(row);
    }

    public void export(HttpServletResponse response, MatrixInboundPageReqVO filter) throws IOException {
        exporter.writeLedger(response, exportRows(filter, false));
    }

    public void exportBas(HttpServletResponse response, MatrixInboundPageReqVO filter) throws IOException {
        List<MatrixInboundDO> appointments = exportRows(filter, true);
        // 历史编号只匹配地区、类型和编号条件。
        boolean reservationFilter = !text(filter.getApplicantName()).isBlank()
                || filter.getFrom() != null || filter.getTo() != null
                || (filter.getResult() != null && !"all".equals(filter.getResult()))
                || (filter.getView() != null && !"all".equals(filter.getView()));
        List<MatrixBasNumber> history = reservationFilter ? List.of()
                : mapper.selectHistoricalBas(tenantId(), text(filter.getSite()), text(filter.getMatrixKind()),
                        text(filter.getBasNo()).trim().toUpperCase(Locale.ROOT));
        long distinctCount = Stream.concat(
                appointments.stream().map(row -> new MatrixBasNumber(row.getSite(), row.getMatrixKind(), row.getBasNo())),
                history.stream()).distinct().count();
        if (distinctCount > EXPORT_LIMIT) throw invalidParamException("导出最多支持五万条记录，请缩小筛选范围");
        exporter.writeBas(response, appointments, history);
    }

    private List<MatrixInboundDO> exportRows(MatrixInboundPageReqVO filter, boolean basOrder) {
        validateFilter(filter);
        List<MatrixInboundDO> rows = mapper.export(filter, tenantId(), getLoginUserId(), now().toLocalDate(), basOrder);
        if (rows.size() > EXPORT_LIMIT) throw invalidParamException("导出最多支持五万条记录，请缩小筛选范围");
        return rows;
    }

    private MatrixInboundDO locked(Long id, Integer version) {
        MatrixInboundDO row = requireRecord(mapper.lock(tenantId(), id));
        if (!Objects.equals(row.getVersion(), version)) staleRecord();
        return row;
    }

    private void save(MatrixInboundDO row, Integer previousVersion) {
        row.setUpdatedBy(getLoginUserId());
        row.setUpdatedAt(now());
        row.setVersion(previousVersion + 1);
        if (mapper.updateCurrent(row, previousVersion) != 1) staleRecord();
    }

    private void ensureEditable(MatrixInboundDO row) {
        if (row.getInboundStatus() >= MatrixInboundStatusEnum.PARTIAL.getValue())
            throw invalidParamException("部分入库和已入库单据仅可调整入库状态");
    }

    private String appointmentNames(List<Long> ids) {
        if (ids == null || ids.isEmpty() || ids.size() > 20 || new HashSet<>(ids).size() != ids.size())
            throw invalidParamException("请选择 1 至 20 位不重复的预约人");
        users.validateUserList(ids);
        var people = users.getUserMap(ids);
        String names = ids.stream().map(id -> {
            var person = people.get(id);
            if (person == null || person.getNickname() == null) throw invalidParamException("预约人不存在");
            return person.getNickname();
        }).collect(Collectors.joining("、"));
        if (names.length() > 200) throw invalidParamException("预约人姓名总长度不能超过 200 字");
        return names;
    }

    private boolean sameRequest(MatrixInboundDO row, MatrixInboundCreateReqVO input) {
        if (!Objects.equals(row.getSite(), input.site()) || !Objects.equals(row.getMatrixKind(), input.matrixKind()))
            return false;
        // 请求标识绑定首次提交；后续修改不会产生新单，重试创建时应返回现有单据，不能拿修改后的字段误判。
        // 未发生业务变更时仍校验原始内容，拒绝同一请求标识提交不同预约。
        if (row.getVersion() > 0) return true;
        return Objects.equals(row.getAppointmentDate(), input.appointmentDate())
                && Objects.equals(row.getLimsDesigned(), input.limsDesigned())
                && Objects.equals(row.getUrgent(), input.urgent())
                && Objects.equals(row.getAppointmentUserIds(), input.appointmentUserIds());
    }

    private MatrixInboundRespVO response(MatrixInboundDO row) {
        MatrixInboundRespVO result = BeanUtils.toBean(row, MatrixInboundRespVO.class);
        result.setBasNo(displayBas(row));
        return result;
    }

    private MatrixInboundDO requireRecord(MatrixInboundDO row) {
        if (row == null) throw invalidParamException("记录不存在或无权访问");
        return row;
    }

    private void validateFilter(MatrixInboundPageReqVO filter) {
        if (filter.getFrom() != null && filter.getTo() != null && filter.getFrom().isAfter(filter.getTo()))
            throw invalidParamException("预约日期的开始日期不能晚于结束日期");
    }

    private void staleRecord() {
        throw invalidParamException("记录已更新，请刷新后重试");
    }
}
