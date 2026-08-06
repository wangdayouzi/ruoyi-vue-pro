package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseLotDO;

import java.util.List;

/**
 * 试剂基础数据 Service 接口
 *
 * @author yudao
 */
public interface ReagentBaseService {

    // ========== 试剂主表 ==========

    /**
     * 创建试剂
     */
    Long createReagentBase(ReagentBaseSaveReqVO createReqVO);

    /**
     * 更新试剂
     */
    void updateReagentBase(ReagentBaseSaveReqVO updateReqVO);

    /**
     * 删除试剂（同时级联删除批号）
     */
    void deleteReagentBase(Long id);

    /**
     * 获得试剂详情
     */
    ReagentBaseDO getReagentBase(Long id);

    /**
     * 获得试剂分页
     */
    PageResult<ReagentBaseDO> getReagentBasePage(ReagentBasePageReqVO pageReqVO);

    /**
     * 获得试剂列表（简化版，供下拉选择）
     */
    List<ReagentBaseRespVO> getReagentBaseSimpleList();

    // ========== 试剂批号 ==========

    /**
     * 创建批号
     */
    Long createReagentBaseLot(ReagentBaseLotSaveReqVO createReqVO);

    /**
     * 更新批号
     */
    void updateReagentBaseLot(ReagentBaseLotSaveReqVO updateReqVO);

    /**
     * 删除批号
     */
    void deleteReagentBaseLot(Long id);

    /**
     * 根据试剂ID获取批号列表
     */
    List<ReagentBaseLotDO> getLotListByBaseId(Long baseId);

    /**
     * 根据试剂编号获取批号列表（供申请时选择批号用）
     */
    List<ReagentBaseLotDO> getLotListByBasId(String basId);

}
