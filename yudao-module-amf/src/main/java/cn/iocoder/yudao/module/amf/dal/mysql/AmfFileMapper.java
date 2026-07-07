package cn.iocoder.yudao.module.amf.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmfFileMapper extends BaseMapperX<AmfFileDO> {

    default List<AmfFileDO> selectListByBusinessId(Long businessId) {
        return selectList(new LambdaQueryWrapperX<AmfFileDO>()
                .eq(AmfFileDO::getBusinessId, businessId)
                .orderByDesc(AmfFileDO::getId));
    }

}
