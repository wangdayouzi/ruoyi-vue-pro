package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;

import java.util.List;

public interface SampleLoanService {
    Long createSampleLoan(SampleLoanCreateReqVO createReqVO);
    void updateSampleLoan(SampleLoanUpdateReqVO updateReqVO);
    void deleteSampleLoan(Long id);
    boolean hasBorrowingSampleLoan(String basNo, String location, Long requesterId, Long excludeId);
    void noReturnSampleLoan(Long id);
    void returnSampleLoan(Long id);
    PageResult<SampleLoanDO> getSampleLoanPage(SampleLoanPageReqVO pageReqVO);
    List<SampleLoanDO> getBorrowingSampleLoanList();
}
