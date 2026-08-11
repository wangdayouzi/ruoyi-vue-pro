package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentDO;

import java.util.List;

/**
 * 发货单 Service 接口
 *
 * @author yudao
 */
public interface ReagentShipmentService {

    /**
     * 确认发货（样品组操作）
     * 1. 校验物流必填项
     * 2. 保存发货单及明细
     * 3. 累加申请明细的 shippedQtyTotal
     * 4. 判定是否全部发货完成，更新申请单状态
     * 5. 发送邮件通知
     */
    Long confirmShipment(ReagentShipmentSaveReqVO reqVO);

    /**
     * 获得发货单详情
     */
    ReagentShipmentDO getShipment(Long id);

    /**
     * 获得发货单分页
     */
    PageResult<ReagentShipmentDO> getShipmentPage(ReagentShipmentPageReqVO pageReqVO);

    /**
     * 根据申请单ID获取发货单列表
     */
    List<ReagentShipmentDO> getShipmentListByApplyId(Long applyId);

    /**
     * 撤回发货：删除发货记录，回退已发数量，恢复申请单状态
     */
    void revokeShipment(Long id);

    /**
     * 更新发货物流信息（快递单号 / 物流公司）
     */
    void updateShipmentLogistics(Long id, String trackingNumber, String expressCompany);

}
