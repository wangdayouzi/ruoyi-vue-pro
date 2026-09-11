package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReagentLabelTemplateMapper extends BaseMapperX<ReagentLabelTemplateDO> {

    default List<ReagentLabelTemplateDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<ReagentLabelTemplateDO>()
                .eq(ReagentLabelTemplateDO::getStatus, 1)
                .orderByAsc(ReagentLabelTemplateDO::getId));
    }

    default ReagentLabelTemplateDO selectEnabledByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<ReagentLabelTemplateDO>()
                .eq(ReagentLabelTemplateDO::getCode, code)
                .eq(ReagentLabelTemplateDO::getStatus, 1));
    }
}
