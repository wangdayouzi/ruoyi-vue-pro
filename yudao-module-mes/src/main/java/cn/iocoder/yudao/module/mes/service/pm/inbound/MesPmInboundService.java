package cn.iocoder.yudao.module.mes.service.pm.inbound;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundRespVO;

/**
 * 老ERP采购入库单 Service
 *
 * @author yudao
 */
public interface MesPmInboundService {

    /** 明细数据分页（扁平行 = 明细 JOIN 主表） */
    PageResult<MesPmInboundRespVO> getDetailPage(MesPmInboundPageReqVO pageReqVO);

}
