package cn.iocoder.yudao.module.amf.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.amf.controller.admin.vo.AmfBusinessPageReqVO;
import cn.iocoder.yudao.module.amf.dal.dataobject.AmfBusinessDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分析方法文件 - 业务单据 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AmfBusinessMapper extends BaseMapperX<AmfBusinessDO> {

    default PageResult<AmfBusinessDO> selectPage(AmfBusinessPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AmfBusinessDO>()
                .likeIfPresent(AmfBusinessDO::getBasNo, reqVO.getBasNo())
                .likeIfPresent(AmfBusinessDO::getProtocolNo, reqVO.getProtocolNo())
                .likeIfPresent(AmfBusinessDO::getSponsor, reqVO.getSponsor())
                .likeIfPresent(AmfBusinessDO::getAnalysisMethod, reqVO.getAnalysisMethod())
                .eqIfPresent(AmfBusinessDO::getStatus, reqVO.getStatus())
                .orderByDesc(AmfBusinessDO::getId));
    }

    default AmfBusinessDO selectByBasNo(String basNo) {
        return selectOne(AmfBusinessDO::getBasNo, basNo);
    }

}
