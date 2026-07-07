package cn.iocoder.yudao.module.amf.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfFileVersionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分析方法文件 - 文件版本记录 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AmfFileVersionMapper extends BaseMapperX<AmfFileVersionDO> {

    /**
     * 根据业务单据ID查询所有版本记录，按版本号降序
     */
    default List<AmfFileVersionDO> selectListByBusinessId(Long businessId) {
        return selectList(new LambdaQueryWrapperX<AmfFileVersionDO>()
                .eq(AmfFileVersionDO::getBusinessId, businessId)
                .orderByDesc(AmfFileVersionDO::getVersionNo));
    }

    default List<AmfFileVersionDO> selectListByFileId(Long fileId) {
        return selectList(new LambdaQueryWrapperX<AmfFileVersionDO>()
                .eq(AmfFileVersionDO::getFileId, fileId)
                .orderByDesc(AmfFileVersionDO::getVersionNo));
    }

}
