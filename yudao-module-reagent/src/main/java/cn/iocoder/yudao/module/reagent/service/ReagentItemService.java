package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;

import java.util.List;

/**
 * 项目号（老样本系统同步）Service 接口
 *
 * @author yudao
 */
public interface ReagentItemService {

    /** 获得项目号分页 */
    PageResult<ReagentItemDO> getItemPage(ReagentItemPageReqVO reqVO);

    /** 下拉选项：项目号/方案名称/方案编号模糊搜索，可按临床方案ID过滤 */
    List<ReagentItemDO> listOptions(Long clinicalId, String keyword);

}
