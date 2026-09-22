package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleLoanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;

@Service
@Validated
public class SampleLoanServiceImpl implements SampleLoanService {

    public static final int STATUS_BORROWING = 1;
    public static final int STATUS_RETURNED = 2;

    @Resource
    private SampleLoanMapper sampleLoanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSampleLoan(SampleLoanCreateReqVO createReqVO) {
        String basNo = StrUtil.trim(createReqVO.getBasNo());
        if (sampleLoanMapper.selectBorrowingByBasNo(basNo) != null) {
            throw exception(SAMPLE_LOAN_ALREADY_BORROWING);
        }
        SampleLoanDO sampleLoan = BeanUtils.toBean(createReqVO, SampleLoanDO.class);
        sampleLoan.setBasNo(basNo)
                .setRequester(StrUtil.trim(createReqVO.getRequester()))
                .setSubmitter(StrUtil.trim(createReqVO.getSubmitter()))
                .setStatus(STATUS_BORROWING);
        sampleLoanMapper.insert(sampleLoan);
        return sampleLoan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnSampleLoan(Long id) {
        SampleLoanDO sampleLoan = sampleLoanMapper.selectById(id);
        if (sampleLoan == null) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        if (!Integer.valueOf(STATUS_BORROWING).equals(sampleLoan.getStatus())) {
            throw exception(SAMPLE_LOAN_NOT_BORROWING);
        }
        SampleLoanDO updateObj = new SampleLoanDO();
        updateObj.setId(id);
        updateObj.setStatus(STATUS_RETURNED);
        updateObj.setReturnTime(LocalDateTime.now());
        sampleLoanMapper.updateById(updateObj);
    }

    @Override
    public PageResult<SampleLoanDO> getSampleLoanPage(SampleLoanPageReqVO pageReqVO) {
        return sampleLoanMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SampleLoanDO> getBorrowingSampleLoanList() {
        return sampleLoanMapper.selectBorrowingList(LocalDateTime.now().minusHours(24));
    }
}
