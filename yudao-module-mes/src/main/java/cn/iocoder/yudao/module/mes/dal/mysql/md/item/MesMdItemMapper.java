package cn.iocoder.yudao.module.mes.dal.mysql.md.item;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.MesMdItemPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdItemDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MES 物料产品 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesMdItemMapper extends BaseMapperX<MesMdItemDO> {

    default PageResult<MesMdItemDO> selectPage(MesMdItemPageReqVO reqVO, Collection<Long> itemTypeIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesMdItemDO>()
                .likeIfPresent(MesMdItemDO::getCode, reqVO.getCode())
                .likeIfPresent(MesMdItemDO::getName, reqVO.getName())
                .inIfPresent(MesMdItemDO::getItemTypeId, itemTypeIds)
                .eqIfPresent(MesMdItemDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesMdItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesMdItemDO::getId));
    }

    default MesMdItemDO selectByCode(String code) {
        return selectOne(MesMdItemDO::getCode, code);
    }

    /** 已存在的物料编码集合（基础数据按需补缺，判断缺失用） */
    default List<String> selectCodeList() {
        return selectObjs(new QueryWrapper<MesMdItemDO>().select("code")).stream()
                .map(v -> v == null ? null : String.valueOf(v))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    default MesMdItemDO selectByName(String name) {
        return selectOne(MesMdItemDO::getName, name);
    }

    default Long selectCountByItemTypeId(Long itemTypeId) {
        return selectCount(MesMdItemDO::getItemTypeId, itemTypeId);
    }

    default Long selectCountByUnitMeasureId(Long unitMeasureId) {
        return selectCount(MesMdItemDO::getUnitMeasureId, unitMeasureId);
    }

}
