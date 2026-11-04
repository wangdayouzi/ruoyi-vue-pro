package cn.iocoder.yudao.module.reagent.dal.mysql;

import org.springframework.util.StringUtils;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentShipmentPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 发货单主表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentShipmentMapper extends BaseMapperX<ReagentShipmentDO> {

    default PageResult<ReagentShipmentDO> selectPage(ReagentShipmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentShipmentDO>()
                .eqIfPresent(ReagentShipmentDO::getApplyId, reqVO.getApplyId())
                .apply(StringUtils.hasText(reqVO.getShipmentNo()), "shipment_no ILIKE {0}", "%" + reqVO.getShipmentNo() + "%")
                .orderByDesc(ReagentShipmentDO::getId));
    }

    default List<ReagentShipmentDO> selectListByApplyId(Long applyId) {
        return selectList(new LambdaQueryWrapperX<ReagentShipmentDO>()
                .eq(ReagentShipmentDO::getApplyId, applyId)
                .orderByDesc(ReagentShipmentDO::getId));
    }

}
