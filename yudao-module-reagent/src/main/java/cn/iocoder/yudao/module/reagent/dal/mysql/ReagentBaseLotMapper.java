package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseLotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试剂批号表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentBaseLotMapper extends BaseMapperX<ReagentBaseLotDO> {

    default List<ReagentBaseLotDO> selectListByBaseId(Long baseId) {
        return selectList(new LambdaQueryWrapperX<ReagentBaseLotDO>()
                .eq(ReagentBaseLotDO::getBaseId, baseId)
                .orderByDesc(ReagentBaseLotDO::getId));
    }

    default ReagentBaseLotDO selectByBaseIdAndLotNo(Long baseId, String lotNo) {
        return selectOne(new LambdaQueryWrapperX<ReagentBaseLotDO>()
                .eq(ReagentBaseLotDO::getBaseId, baseId)
                .eq(ReagentBaseLotDO::getLotNo, lotNo));
    }

    default List<ReagentBaseLotDO> selectListByBasIdAndLotNo(String basId, String lotNo) {
        return selectList(new LambdaQueryWrapperX<ReagentBaseLotDO>()
                .eq(ReagentBaseLotDO::getLotNo, lotNo));
    }

}
