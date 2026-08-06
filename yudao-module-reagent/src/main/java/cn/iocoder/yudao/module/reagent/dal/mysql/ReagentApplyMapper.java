package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentApplyPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试剂申请主表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentApplyMapper extends BaseMapperX<ReagentApplyDO> {

    default PageResult<ReagentApplyDO> selectPage(ReagentApplyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentApplyDO>()
                .likeIfPresent(ReagentApplyDO::getApplyNo, reqVO.getApplyNo())
                .likeIfPresent(ReagentApplyDO::getReceiverUnit, reqVO.getReceiverUnit())
                .eqIfPresent(ReagentApplyDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentApplyDO::getId));
    }

    default ReagentApplyDO selectByApplyNo(String applyNo) {
        return selectOne(ReagentApplyDO::getApplyNo, applyNo);
    }

}
