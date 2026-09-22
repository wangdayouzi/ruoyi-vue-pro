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
        return selectPage(reqVO, new LambdaQueryWrapperX<SampleLoanDO>()
                .likeIfPresent(SampleLoanDO::getBasNo, reqVO.getBasNo())
                .eqIfPresent(SampleLoanDO::getRequesterId, reqVO.getRequesterId())
                .eqIfPresent(SampleLoanDO::getStatus, reqVO.getStatus())
                .orderByDesc(SampleLoanDO::getId));
    }

    default SampleLoanDO selectBorrowingByBasNo(String basNo) {
        return selectOne(new LambdaQueryWrapperX<SampleLoanDO>()
                .eq(SampleLoanDO::getBasNo, basNo)
                .eq(SampleLoanDO::getStatus, 1)
                .last("LIMIT 1"));
    }

    default List<SampleLoanDO> selectBorrowingList(LocalDateTime beginTime) {
        return selectList(new LambdaQueryWrapperX<SampleLoanDO>()
                .eq(SampleLoanDO::getStatus, 1)
                .ge(SampleLoanDO::getCreateTime, beginTime)
                .orderByDesc(SampleLoanDO::getCreateTime));
    }
}
