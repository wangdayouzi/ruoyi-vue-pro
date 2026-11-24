package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundLogDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MatrixInboundLogMapper extends BaseMapperX<MatrixInboundLogDO> {
    default List<MatrixInboundLogDO> selectByInbound(Long tenantId, Long inboundId) {
        return selectList(new LambdaQueryWrapperX<MatrixInboundLogDO>()
                .eq(MatrixInboundLogDO::getTenantId, tenantId).eq(MatrixInboundLogDO::getInboundId, inboundId)
                .orderByDesc(MatrixInboundLogDO::getId));
    }
}
