package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;

import java.util.List;

/**
 * 临床方案（老样本系统同步）Service 接口
 *
 * @author yudao
 */
public interface ReagentClinicalService {

    /** 获得临床方案分页 */
    PageResult<ReagentClinicalDO> getClinicalPage(ReagentClinicalPageReqVO reqVO);

    /** 下拉选项：按名称/编号模糊搜索 */
    List<ReagentClinicalDO> listOptions(String keyword);

    /** 修改临床方案本地字段（是否自动发送寄送通知邮件） */
    void updateClinicalLocalField(ReagentClinicalUpdateReqVO reqVO);

}
