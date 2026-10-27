package cn.iocoder.yudao.module.infra.dal.mysql.number;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberIssueDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NumberIssueMapper extends BaseMapperX<NumberIssueDO> {
}
