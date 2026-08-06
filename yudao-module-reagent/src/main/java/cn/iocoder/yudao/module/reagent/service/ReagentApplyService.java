package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;

/**
 * 试剂申请单 Service 接口
 *
 * @author yudao
 */
public interface ReagentApplyService {

    /**
     * 创建申请单（项目组提单）
     */
    Long createApply(ReagentApplySaveReqVO createReqVO);

    /**
     * 更新申请单（仅草稿状态可编辑）
     */
    void updateApply(ReagentApplySaveReqVO updateReqVO);

    /**
     * 删除申请单
     */
    void deleteApply(Long id);

    /**
     * 提交申请单（草稿 → 待发货，启动 Flowable 流程）
     */
    void submitApply(Long id);

    /**
     * 获得申请单详情
     */
    ReagentApplyDO getApply(Long id);

    /**
     * 获得申请单分页
     */
    PageResult<ReagentApplyDO> getApplyPage(ReagentApplyPageReqVO pageReqVO);

    /**
     * 拒绝/退回申请单（样品组操作）
     */
    void rejectApply(ReagentApplyRejectReqVO reqVO);

}
