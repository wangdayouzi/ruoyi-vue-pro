package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 发货单明细表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentShipmentItemMapper extends BaseMapperX<ReagentShipmentItemDO> {

    default List<ReagentShipmentItemDO> selectListByShipmentId(Long shipmentId) {
        return selectList(new LambdaQueryWrapperX<ReagentShipmentItemDO>()
                .eq(ReagentShipmentItemDO::getShipmentId, shipmentId));
    }

    default int deleteByShipmentId(Long shipmentId) {
        return delete(new LambdaQueryWrapperX<ReagentShipmentItemDO>()
                .eq(ReagentShipmentItemDO::getShipmentId, shipmentId));
    }

}
