package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentPlanDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleUrgentPlanMapper extends BaseMapperX<SampleUrgentPlanDO> {
    default SampleUrgentPlanDO lock(Long id) {
        return selectOne(new LambdaQueryWrapperX<SampleUrgentPlanDO>()
                .eq(SampleUrgentPlanDO::getId, id).last("FOR UPDATE"));
    }
}
