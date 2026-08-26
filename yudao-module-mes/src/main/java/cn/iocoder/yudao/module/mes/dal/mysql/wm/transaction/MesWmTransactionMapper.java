package cn.iocoder.yudao.module.mes.dal.mysql.wm.transaction;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transaction.vo.MesWmTransactionPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transaction.MesWmTransactionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

/**
 * MES 库存事务流水 Mapper
 */
@Mapper
public interface MesWmTransactionMapper extends BaseMapperX<MesWmTransactionDO> {

    default PageResult<MesWmTransactionDO> selectPage(MesWmTransactionPageReqVO reqVO, Collection<Long> itemIds,
                                                      Collection<Long> batchIds, Collection<Long> vendorBatchIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmTransactionDO>()
                .inIfPresent(MesWmTransactionDO::getItemId, itemIds)
                .likeIfPresent(MesWmTransactionDO::getBatchCode, reqVO.getBatchCode())
                .inIfPresent(MesWmTransactionDO::getBatchId, batchIds)
                .inIfPresent(MesWmTransactionDO::getBatchId, vendorBatchIds)
                .eqIfPresent(MesWmTransactionDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(MesWmTransactionDO::getType, reqVO.getType())
                .eqIfPresent(MesWmTransactionDO::getBizType, reqVO.getBizType())
                .likeIfPresent(MesWmTransactionDO::getBizCode, reqVO.getBizCode())
                .geIfPresent(MesWmTransactionDO::getQuantity, reqVO.getMinQty())
                .leIfPresent(MesWmTransactionDO::getQuantity, reqVO.getMaxQty())
                .betweenIfPresent(MesWmTransactionDO::getErpTime, reqVO.getBeginTime(), reqVO.getEndTime())
                .orderByDesc(MesWmTransactionDO::getId));
    }

}
