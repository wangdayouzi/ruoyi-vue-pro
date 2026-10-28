package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfUpdateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanOptionRespVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;

import java.util.List;

public interface SampleLoanService {
    Long createSampleLoan(SampleLoanCreateReqVO createReqVO);
    SampleLoanDO createSelfSampleLoan(SampleLoanSelfCreateReqVO createReqVO);
    void updateSelfSampleLoan(SampleLoanSelfUpdateReqVO updateReqVO);
    void returnSelfSampleLoan(Long id);
    void noReturnSelfSampleLoan(Long id);
    void deleteSelfSampleLoan(Long id);
    PageResult<SampleLoanDO> getSelfSampleLoanPage(SampleLoanPageReqVO pageReqVO);
    void updateSampleLoan(SampleLoanUpdateReqVO updateReqVO);
    void deleteSampleLoan(Long id);
    boolean hasBorrowingSampleLoan(String basNo, String site, String location, Long requesterId, Long excludeId);
    void noReturnSampleLoan(Long id);
    void returnSampleLoan(Long id);
    PageResult<SampleLoanDO> getSampleLoanPage(SampleLoanPageReqVO pageReqVO);
    List<SampleLoanDO> getBorrowingSampleLoanList(String location);
    List<SampleLoanOptionRespVO> getSiteOptions();
    List<SampleLoanOptionRespVO> getLocationOptions(String site);
}
