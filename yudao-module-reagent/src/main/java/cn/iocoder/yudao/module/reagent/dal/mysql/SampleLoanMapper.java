package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SampleLoanMapper extends BaseMapperX<SampleLoanDO> {

    default PageResult<SampleLoanDO> selectPage(SampleLoanPageReqVO reqVO) {
        LambdaQueryWrapperX<SampleLoanDO> query = new LambdaQueryWrapperX<SampleLoanDO>()
                .likeIfPresent(SampleLoanDO::getBasNo, reqVO.getBasNo());
        if ("NINGBO".equals(reqVO.getSite())) {
            query.and(wrapper -> wrapper.eq(SampleLoanDO::getSite, "NINGBO")
                    .or().isNull(SampleLoanDO::getSite));
        } else {
            query.eqIfPresent(SampleLoanDO::getSite, reqVO.getSite());
        }
        return selectPage(reqVO, query
                .eqIfPresent(SampleLoanDO::getLocation, reqVO.getLocation())
                .eqIfPresent(SampleLoanDO::getRequesterId, reqVO.getRequesterId())
                .eqIfPresent(SampleLoanDO::getStatus, reqVO.getStatus())
                .orderByDesc(SampleLoanDO::getId));
    }

    default SampleLoanDO selectBorrowingByKey(String basNo, String site, String location, Long requesterId, Long excludeId) {
        LambdaQueryWrapperX<SampleLoanDO> query = new LambdaQueryWrapperX<SampleLoanDO>()
                .eq(SampleLoanDO::getBasNo, basNo);
        if ("NINGBO".equals(site)) {
            query.and(wrapper -> wrapper.eq(SampleLoanDO::getSite, "NINGBO")
                    .or().isNull(SampleLoanDO::getSite));
        } else {
            query.eqIfPresent(SampleLoanDO::getSite, site);
        }
        return selectOne(query
                .eqIfPresent(SampleLoanDO::getLocation, location)
                .eq(SampleLoanDO::getRequesterId, requesterId)
                .eq(SampleLoanDO::getStatus, 1)
                .neIfPresent(SampleLoanDO::getId, excludeId)
                .last("LIMIT 1"));
    }

    default List<SampleLoanDO> selectBorrowingList(String location, LocalDateTime beginTime) {
        return selectList(new LambdaQueryWrapperX<SampleLoanDO>()
                .and(wrapper -> wrapper.eq(SampleLoanDO::getSite, "NINGBO")
                        .or().isNull(SampleLoanDO::getSite))
                .eq(SampleLoanDO::getLocation, location)
                .eq(SampleLoanDO::getStatus, 1)
                .ge(SampleLoanDO::getCreateTime, beginTime)
                .orderByDesc(SampleLoanDO::getCreateTime));
    }
}
