package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试剂申请明细表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentApplyItemMapper extends BaseMapperX<ReagentApplyItemDO> {

    default List<ReagentApplyItemDO> selectListByApplyId(Long applyId) {
        return selectList(new LambdaQueryWrapperX<ReagentApplyItemDO>()
                .eq(ReagentApplyItemDO::getApplyId, applyId));
    }

    default void deleteByApplyId(Long applyId) {
        delete(new LambdaQueryWrapperX<ReagentApplyItemDO>()
                .eq(ReagentApplyItemDO::getApplyId, applyId));
    }

}
