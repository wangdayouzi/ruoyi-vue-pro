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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;

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
    public Long createApply(ReagentApplySaveReqVO createReqVO) {
        // 生成申请单号
        String applyNo = generateApplyNo();

        // 保存主表
        ReagentApplyDO apply = BeanUtils.toBean(createReqVO, ReagentApplyDO.class);
        apply.setApplyNo(applyNo);
        apply.setStatus(STATUS_DRAFT);
        // 发货方默认值
        apply.setConsignorUnit(defaultIfBlank(apply.getConsignorUnit(), "精翰生物"));
        apply.setConsignorAddress(defaultIfBlank(apply.getConsignorAddress(), "上海市浦东新区张江高科技园区XXX号"));
        apply.setConsignorName(defaultIfBlank(apply.getConsignorName(), "仓库管理员"));
        apply.setConsignorPhone(defaultIfBlank(apply.getConsignorPhone(), "021-XXXXXXXX"));
        reagentApplyMapper.insert(apply);

        // 保存明细
        saveApplyItems(apply.getId(), createReqVO.getItems());

        return apply.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApply(Long id) {
        validateApplyExists(id);
        reagentApplyMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    }

    // ==================== 私有方法 ====================

    private void saveApplyItems(Long applyId, List<ReagentApplyItemVO> itemVOs) {
        for (ReagentApplyItemVO itemVO : itemVOs) {
            ReagentApplyItemDO item = BeanUtils.toBean(itemVO, ReagentApplyItemDO.class);
            item.setApplyId(applyId);
            item.setShippedQtyTotal(0);
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
