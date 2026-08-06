package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBasePageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试剂主表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentBaseMapper extends BaseMapperX<ReagentBaseDO> {

    default PageResult<ReagentBaseDO> selectPage(ReagentBasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentBaseDO>()
                .likeIfPresent(ReagentBaseDO::getBasId, reqVO.getBasId())
                .likeIfPresent(ReagentBaseDO::getReagentName, reqVO.getReagentName())
                .likeIfPresent(ReagentBaseDO::getCatNo, reqVO.getCatNo())
                .eqIfPresent(ReagentBaseDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentBaseDO::getId));
    }

    default ReagentBaseDO selectByBasId(String basId) {
        return selectOne(ReagentBaseDO::getBasId, basId);
    }

}
