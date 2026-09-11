package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyItemDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentApplyItemMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentApplyMapper;
import cn.iocoder.yudao.module.reagent.dal.redis.no.ReagentNoRedisDAO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.reagent.enums.LogRecordConstants.*;

/**
 * 试剂申请单 Service 实现
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ReagentApplyServiceImpl implements ReagentApplyService {

    @Resource
    private ReagentApplyMapper reagentApplyMapper;

    @Resource
    private ReagentApplyItemMapper reagentApplyItemMapper;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Resource
    private ReagentNoRedisDAO reagentNoRedisDAO;

    @Resource
    private AdminUserApi adminUserApi;

    private static final DateTimeFormatter APPLY_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Flowable 流程定义 Key
     */
    private static final String PROCESS_DEFINITION_KEY = "reagent-apply";

    /**
     * 申请单状态枚举
     */
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PENDING_SHIP = 1;
    public static final int STATUS_PARTIAL_SHIP = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_REJECTED = 4;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_APPLY_TYPE, subType = REAGENT_APPLY_CREATE_SUB_TYPE, bizNo = "{{#apply.id}}",
            success = REAGENT_APPLY_CREATE_SUCCESS)
    public Long createApply(ReagentApplySaveReqVO createReqVO) {
        // 生成申请单号
        String applyNo = generateApplyNo();

        // 保存主表
        ReagentApplyDO apply = BeanUtils.toBean(createReqVO, ReagentApplyDO.class);
        apply.setApplyNo(applyNo);
        apply.setStatus(STATUS_DRAFT);
        // 发货方默认值
        apply.setConsignorUnit(defaultIfBlank(apply.getConsignorUnit(), "精翰样品管理组"));
        apply.setConsignorAddress(defaultIfBlank(apply.getConsignorAddress(), "上海市浦东新区(上海)自由贸易试验区加枫路8号7层A32"));
        apply.setConsignorName(defaultIfBlank(apply.getConsignorName(), "样品管理组"));
        apply.setConsignorPhone(defaultIfBlank(apply.getConsignorPhone(), "18117369294"));
        reagentApplyMapper.insert(apply);

        // 保存明细
        saveApplyItems(apply.getId(), createReqVO.getItems());

        LogRecordContext.putVariable("apply", apply);
        return apply.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_APPLY_TYPE, subType = REAGENT_APPLY_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = REAGENT_APPLY_UPDATE_SUCCESS)
    public void updateApply(ReagentApplySaveReqVO updateReqVO) {
        ReagentApplyDO apply = validateApplyExists(updateReqVO.getId());
        if (apply.getStatus() != STATUS_DRAFT && apply.getStatus() != STATUS_REJECTED) {
            throw exception(REAGENT_APPLY_CANNOT_EDIT);
        }

        ReagentApplyDO updateObj = BeanUtils.toBean(updateReqVO, ReagentApplyDO.class);
        updateObj.setStatus(STATUS_DRAFT); // 重置为草稿
        reagentApplyMapper.updateById(updateObj);

        // 先删后插明细
        reagentApplyItemMapper.deleteByApplyId(updateReqVO.getId());
        saveApplyItems(updateReqVO.getId(), updateReqVO.getItems());

        LogRecordContext.putVariable("apply", apply);
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT,
                BeanUtils.toBean(apply, ReagentApplySaveReqVO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_APPLY_TYPE, subType = REAGENT_APPLY_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = REAGENT_APPLY_DELETE_SUCCESS)
    public void deleteApply(Long id) {
        ReagentApplyDO apply = validateApplyExists(id);
        reagentApplyMapper.deleteById(id);
        LogRecordContext.putVariable("apply", apply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_APPLY_TYPE, subType = REAGENT_APPLY_SUBMIT_SUB_TYPE, bizNo = "{{#id}}",
            success = REAGENT_APPLY_SUBMIT_SUCCESS)
    public void submitApply(Long id) {
        ReagentApplyDO apply = validateApplyExists(id);
        if (apply.getStatus() != STATUS_DRAFT && apply.getStatus() != STATUS_REJECTED) {
            throw exception(REAGENT_APPLY_CANNOT_EDIT);
        }

        // 校验明细不为空
        List<ReagentApplyItemDO> items = reagentApplyItemMapper.selectListByApplyId(id);
        if (items.isEmpty()) {
            throw exception(REAGENT_APPLY_NO_ITEMS);
        }

        // 启动 Flowable 工作流
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) { userId = 1L; }
        try {
            // 业务字段写入流程变量，供流程监听器/邮件模板使用（create 事件在 processInstanceId 落库前触发，故不能依赖查库）
            Map<String, Object> variables = new HashMap<>();
            variables.put("applyNo", StrUtil.nullToEmpty(apply.getApplyNo()));
            variables.put("receiverUnit", StrUtil.nullToEmpty(apply.getReceiverUnit()));
            variables.put("receiverName", StrUtil.nullToEmpty(apply.getReceiverName()));
            variables.put("receiverPhone", StrUtil.nullToEmpty(apply.getReceiverPhone()));
            variables.put("receiverAddress", StrUtil.nullToEmpty(apply.getReceiverAddress()));
            // 发货区域（上海/宁波）：create 邮件监听器据此选择写死的发货处理人邮箱
            variables.put("region", StrUtil.nullToEmpty(apply.getRegion()));
            // 申请人（提单人昵称）+ 发货方/基础信息：邮件模板预留 {{applicant}}、{{consignorUnit}} 等变量
            variables.put("applicant", resolveApplicant(apply.getCreator()));
            variables.put("consignorUnit", StrUtil.nullToEmpty(apply.getConsignorUnit()));
            variables.put("consignorAddress", StrUtil.nullToEmpty(apply.getConsignorAddress()));
            variables.put("consignorName", StrUtil.nullToEmpty(apply.getConsignorName()));
            variables.put("consignorPhone", StrUtil.nullToEmpty(apply.getConsignorPhone()));
            variables.put("consignorEmail", StrUtil.nullToEmpty(apply.getConsignorEmail()));
            variables.put("freightSettlement", StrUtil.nullToEmpty(apply.getFreightSettlement()));
            variables.put("projectNo", StrUtil.nullToEmpty(apply.getProjectNo()));
            variables.put("transportTemp", StrUtil.nullToEmpty(apply.getTransportTemp()));
            variables.put("hasTempLogger", apply.getHasTempLogger() == null ? "" : apply.getHasTempLogger().toString());
            variables.put("plannedShipDate", apply.getPlannedShipDate() == null ? "" : apply.getPlannedShipDate().format(APPLY_DATE_FMT));
            variables.put("note", StrUtil.nullToEmpty(apply.getNote()));

            String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId,
                    new BpmProcessInstanceCreateReqDTO()
                            .setProcessDefinitionKey(PROCESS_DEFINITION_KEY)
                            .setBusinessKey(id.toString())
                            .setVariables(variables));
            apply.setProcessInstanceId(processInstanceId);
            log.info("[reagent] 申请单 {} 已启动 Flowable 流程，实例ID: {}", apply.getApplyNo(), processInstanceId);
        } catch (Exception e) {
            log.error("[reagent] 申请单 {} 启动 Flowable 流程失败", apply.getApplyNo(), e);
            // 流程启动失败不阻塞提交，继续更新状态
        }

        // 更新状态为待发货
        apply.setStatus(STATUS_PENDING_SHIP);
        reagentApplyMapper.updateById(apply);
        LogRecordContext.putVariable("apply", apply);
    }

    /**
     * 申请人 = 提单人（申请单创建人）昵称；查不到用户时回退用户 ID
     */
    private String resolveApplicant(String creator) {
        if (StrUtil.isBlank(creator)) {
            return "";
        }
        try {
            AdminUserRespDTO user = adminUserApi.getUser(Long.parseLong(creator));
            if (user != null) {
                return StrUtil.blankToDefault(user.getNickname(), creator);
            }
            return creator;
        } catch (NumberFormatException e) {
            return creator;
        } catch (Exception e) {
            // 查询用户失败不影响提单/流程启动，回退用户 ID
            log.warn("[reagent] 解析申请人昵称失败 creator={}", creator, e);
            return creator;
        }
    }

    @Override
    public ReagentApplyDO getApply(Long id) {
        ReagentApplyDO apply = validateApplyExists(id);
        // 填充明细
        List<ReagentApplyItemDO> items = reagentApplyItemMapper.selectListByApplyId(id);
        // 明细通过接口层转换，这里只返回DO
        return apply;
    }

    @Override
    public PageResult<ReagentApplyDO> getApplyPage(ReagentApplyPageReqVO pageReqVO) {
        return reagentApplyMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_APPLY_TYPE, subType = REAGENT_APPLY_REJECT_SUB_TYPE, bizNo = "{{#reqVO.id}}",
            success = REAGENT_APPLY_REJECT_SUCCESS)
    public void rejectApply(ReagentApplyRejectReqVO reqVO) {
        ReagentApplyDO apply = validateApplyExists(reqVO.getId());
        if (apply.getStatus() != STATUS_PENDING_SHIP) {
            throw exception(REAGENT_APPLY_CANNOT_EDIT);
        }

        // 业务表单侧拒绝/退回：仅更新状态，不依赖 BPM 退回节点（该节点为中间抛出事件，非等待节点）
        apply.setStatus(STATUS_REJECTED);
        apply.setRemark(reqVO.getRemark());
        reagentApplyMapper.updateById(apply);
        log.info("[reagent] 申请单 {} 已拒单退回，理由: {}", apply.getApplyNo(), reqVO.getRemark());
        LogRecordContext.putVariable("apply", apply);
    }

    // ==================== 私有方法 ====================

    private void saveApplyItems(Long applyId, List<ReagentApplyItemVO> itemVOs) {
        for (ReagentApplyItemVO itemVO : itemVOs) {
            ReagentApplyItemDO item = BeanUtils.toBean(itemVO, ReagentApplyItemDO.class);
            item.setApplyId(applyId);
            item.setShippedQtyTotal(BigDecimal.ZERO);
            reagentApplyItemMapper.insert(item);
        }
    }

    private String generateApplyNo() {
        return reagentNoRedisDAO.generate(ReagentNoRedisDAO.APPLY_NO_PREFIX);
    }

    private ReagentApplyDO validateApplyExists(Long id) {
        ReagentApplyDO apply = reagentApplyMapper.selectById(id);
        if (apply == null) {
            throw exception(REAGENT_APPLY_NOT_EXISTS);
        }
        return apply;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

}
