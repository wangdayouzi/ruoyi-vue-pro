package cn.iocoder.yudao.module.infra.dal.mysql.number;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberRuleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NumberRuleMapper extends BaseMapperX<NumberRuleDO> {
    default NumberRuleDO selectByCode(String code) {
        return selectOne(NumberRuleDO::getCode, code);
    }

    @Select("SELECT * FROM infra_number_rule WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    NumberRuleDO selectForUpdate(@Param("id") Long id);
}
