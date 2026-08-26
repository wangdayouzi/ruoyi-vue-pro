package cn.iocoder.yudao.module.mes.service.pm.po;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoRespVO;

/**
 * 老ERP采购订单数据 Service
 *
 * @author yudao
 */
public interface MesPmPoService {

    /** 采购订单数据分页 */
    PageResult<MesPmPoRespVO> getPoPage(MesPmPoPageReqVO pageReqVO);

}
