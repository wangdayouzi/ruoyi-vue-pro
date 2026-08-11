package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessTaskApi;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.reagent.dal.redis.no.ReagentNoRedisDAO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;

/**
 * 发货单 Service 实现
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ReagentShipmentServiceImpl implements ReagentShipmentService {

    @Resource
    private ReagentShipmentMapper reagentShipmentMapper;

    @Resource
    private ReagentShipmentItemMapper reagentShipmentItemMapper;

    @Resource
    private ReagentApplyMapper reagentApplyMapper;

    @Resource
    private ReagentApplyItemMapper reagentApplyItemMapper;

    @Resource
    private BpmProcessTaskApi bpmProcessTaskApi;

    @Resource
    private ReagentNoRedisDAO reagentNoRedisDAO;

    /**
     * Flowable 任务节点 Key：样品组审核发货（全部发货完成后自动通过该节点）
     */
    private static final String TASK_AUDIT = "reagent-audit";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long confirmShipment(ReagentShipmentSaveReqVO reqVO) {
        // 1. 校验申请单存在且状态为待发货/部分发货
        ReagentApplyDO apply = validateApplyCanShip(reqVO.getApplyId());

        // 2. 校验发货明细 & 数量合法性
        List<ReagentApplyItemDO> applyItems = reagentApplyItemMapper.selectListByApplyId(reqVO.getApplyId());
        for (ReagentShipmentItemVO itemVO : reqVO.getItems()) {
            if (itemVO.getQuantityShipped() == null || itemVO.getQuantityShipped() <= 0) {
                throw exception(REAGENT_SHIPMENT_QTY_REQUIRED);
            }
            // 查找对应申请明细
            ReagentApplyItemDO applyItem = applyItems.stream()
                    .filter(i -> i.getId().equals(itemVO.getApplyItemId()))
                    .findFirst()
                    .orElseThrow(() -> exception(REAGENT_APPLY_NO_ITEMS));
            // 累计发货不能超过需求
            int newTotal = applyItem.getShippedQtyTotal() + itemVO.getQuantityShipped();
            if (newTotal > applyItem.getRequestedQty()) {
                throw exception(REAGENT_APPLY_QTY_EXCEEDED);
            }
        }

        // 4. 生成发货单号 & 保存发货单主表
        String shipmentNo = generateShipmentNo();
        ReagentShipmentDO shipment = BeanUtils.toBean(reqVO, ReagentShipmentDO.class);
        shipment.setShipmentNo(shipmentNo);
        shipment.setShipmentDate(LocalDateTime.now());
        reagentShipmentMapper.insert(shipment);

        // 5. 保存发货明细 & 累加已发货数量
        for (ReagentShipmentItemVO itemVO : reqVO.getItems()) {
            ReagentShipmentItemDO shipmentItem = new ReagentShipmentItemDO();
            shipmentItem.setShipmentId(shipment.getId());
            shipmentItem.setApplyItemId(itemVO.getApplyItemId());
            shipmentItem.setLotNo(itemVO.getLotNo());
            shipmentItem.setQuantityShipped(itemVO.getQuantityShipped());
            reagentShipmentItemMapper.insert(shipmentItem);

            // 累加 shipped_qty_total
            ReagentApplyItemDO applyItem = applyItems.stream()
                    .filter(i -> i.getId().equals(itemVO.getApplyItemId()))
                    .findFirst()
                    .orElse(null);
            if (applyItem != null) {
                ReagentApplyItemDO updateItem = new ReagentApplyItemDO();
                updateItem.setId(applyItem.getId());
                updateItem.setShippedQtyTotal(applyItem.getShippedQtyTotal() + itemVO.getQuantityShipped());
                reagentApplyItemMapper.updateById(updateItem);
                // 更新内存中的值，供后续判断使用
                applyItem.setShippedQtyTotal(updateItem.getShippedQtyTotal());
            }
        }

        // 6. 判定是否全部发货完成，更新状态 + 触发 Flowable 流程
        List<ReagentApplyItemDO> updatedItems = reagentApplyItemMapper.selectListByApplyId(reqVO.getApplyId());
        boolean allShipped = updatedItems.stream()
                .allMatch(i -> i.getShippedQtyTotal() >= i.getRequestedQty());

        ReagentApplyDO updateApply = new ReagentApplyDO();
        updateApply.setId(reqVO.getApplyId());
        if (allShipped) {
            updateApply.setStatus(ReagentApplyServiceImpl.STATUS_COMPLETED);
            reagentApplyMapper.updateById(updateApply);

            // Flowable：全部发货完成，自动完成"样品组审核发货"节点，触发 complete 事件（发货完成邮件）
            if (apply.getProcessInstanceId() != null) {
                try {
                    bpmProcessTaskApi.completeTaskByKey(apply.getProcessInstanceId(), TASK_AUDIT,
                            "全部发货完成，自动通过", SecurityFrameworkUtils.getLoginUserId());
                    log.info("[reagent] 申请单 {} 全部发货完成，已自动完成 Flowable 审核节点", apply.getApplyNo());
                } catch (Exception e) {
                    log.error("[reagent] 申请单 {} Flowable 审核节点自动完成失败", apply.getApplyNo(), e);
                }
            }
        } else {
            updateApply.setStatus(ReagentApplyServiceImpl.STATUS_PARTIAL_SHIP);
            reagentApplyMapper.updateById(updateApply);
            log.info("[reagent] 申请单 {} 部分发货，状态更新为部分发货，等待继续发货", apply.getApplyNo());
        }

        return shipment.getId();
    }

    @Override
    public ReagentShipmentDO getShipment(Long id) {
        ReagentShipmentDO shipment = reagentShipmentMapper.selectById(id);
        if (shipment == null) {
            throw exception(REAGENT_SHIPMENT_NOT_EXISTS);
        }
        return shipment;
    }

    @Override
    public PageResult<ReagentShipmentDO> getShipmentPage(ReagentShipmentPageReqVO pageReqVO) {
        return reagentShipmentMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ReagentShipmentDO> getShipmentListByApplyId(Long applyId) {
        return reagentShipmentMapper.selectListByApplyId(applyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeShipment(Long id) {
        ReagentShipmentDO shipment = reagentShipmentMapper.selectById(id);
        if (shipment == null) {
            throw exception(REAGENT_SHIPMENT_NOT_EXISTS);
        }

        // 1. 回退已发数量
        List<ReagentShipmentItemDO> items = reagentShipmentItemMapper.selectListByShipmentId(id);
        for (ReagentShipmentItemDO item : items) {
            ReagentApplyItemDO applyItem = reagentApplyItemMapper.selectById(item.getApplyItemId());
            if (applyItem != null) {
                ReagentApplyItemDO updateItem = new ReagentApplyItemDO();
                updateItem.setId(applyItem.getId());
                updateItem.setShippedQtyTotal(
                        Math.max(0, applyItem.getShippedQtyTotal() - item.getQuantityShipped()));
                reagentApplyItemMapper.updateById(updateItem);
            }
        }

        // 2. 删除发货明细
        reagentShipmentItemMapper.deleteByShipmentId(id);

        // 3. 删除发货单
        reagentShipmentMapper.deleteById(id);

        // 4. 重新判定申请单状态
        ReagentApplyDO apply = reagentApplyMapper.selectById(shipment.getApplyId());
        if (apply != null) {
            List<ReagentShipmentDO> remainingShipments = reagentShipmentMapper.selectListByApplyId(apply.getId());
            ReagentApplyDO updateApply = new ReagentApplyDO();
            updateApply.setId(apply.getId());
            if (remainingShipments.isEmpty()) {
                updateApply.setStatus(ReagentApplyServiceImpl.STATUS_PENDING_SHIP);
            } else {
                updateApply.setStatus(ReagentApplyServiceImpl.STATUS_PARTIAL_SHIP);
            }
            reagentApplyMapper.updateById(updateApply);
        }
        log.info("[reagent] 发货单 {} 已撤回，申请单 {} 状态已回退", shipment.getShipmentNo(), shipment.getApplyId());
    }

    // ==================== 私有方法 ====================

    private ReagentApplyDO validateApplyCanShip(Long applyId) {
        ReagentApplyDO apply = reagentApplyMapper.selectById(applyId);
        if (apply == null) {
            throw exception(REAGENT_APPLY_NOT_EXISTS);
        }
        if (apply.getStatus() != ReagentApplyServiceImpl.STATUS_PENDING_SHIP
                && apply.getStatus() != ReagentApplyServiceImpl.STATUS_PARTIAL_SHIP) {
            throw exception(REAGENT_APPLY_CANNOT_EDIT);
        }
        return apply;
    }

    private String generateShipmentNo() {
        return reagentNoRedisDAO.generate(ReagentNoRedisDAO.SHIPMENT_NO_PREFIX);
    }

    @Override
    public void updateShipmentLogistics(Long id, String trackingNumber, String expressCompany) {
        ReagentShipmentDO shipment = reagentShipmentMapper.selectById(id);
        if (shipment == null) {
            throw exception(REAGENT_SHIPMENT_NOT_EXISTS);
        }
        ReagentShipmentDO update = new ReagentShipmentDO();
        update.setId(id);
        update.setTrackingNumber(trackingNumber);
        update.setExpressCompany(expressCompany);
        reagentShipmentMapper.updateById(update);
    }

}
