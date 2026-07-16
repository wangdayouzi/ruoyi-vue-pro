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
                .likeIfPresent(AmfBusinessDO::getMethodNo, reqVO.getMethodNo())
                .likeIfPresent(AmfBusinessDO::getMethodVersion, reqVO.getMethodVersion())
                .likeIfPresent(AmfBusinessDO::getMethodName, reqVO.getMethodName())
                .likeIfPresent(AmfBusinessDO::getTestArticle, reqVO.getTestArticle())
                .likeIfPresent(AmfBusinessDO::getMatrixType, reqVO.getMatrixType())
                .likeIfPresent(AmfBusinessDO::getSd, reqVO.getSd())
                .eqIfPresent(AmfBusinessDO::getEffectiveDate, reqVO.getEffectiveDate())
                .eqIfPresent(AmfBusinessDO::getStatus, reqVO.getStatus())
                .orderByDesc(AmfBusinessDO::getId));
    }

    default AmfBusinessDO selectByMethodNo(String methodNo) {
        return selectOne(AmfBusinessDO::getMethodNo, methodNo);
    }

}
