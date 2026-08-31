package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSaveReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSimplePageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;

/**
 * 老ERP同步 试剂基础数据(扁平) Service 接口
 *
 * @author yudao
 */
public interface ReagentBaseFlatService {

    /**
     * 获得试剂基础数据(扁平)分页
     */
    PageResult<ReagentBaseFlatDO> getReagentBaseFlatPage(ReagentBaseFlatPageReqVO pageReqVO);

    /**
     * 修改试剂基础数据(扁平)可维护字段
     */
    void updateReagentBaseFlat(ReagentBaseFlatSaveReqVO updateReqVO);

    /**
     * 试剂基础数据(扁平)简易分页（申请单选批号用，关键词+仓库搜索）
     */
    PageResult<ReagentBaseFlatDO> getReagentBaseFlatSimplePage(ReagentBaseFlatSimplePageReqVO reqVO);

    /**
     * 删除试剂基础数据(扁平)（物理删除，重新同步可恢复）
     */
    void deleteReagentBaseFlat(Long id);

}
