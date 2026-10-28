package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanOptionRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfUpdateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleLoanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;

@Service
@Validated
public class SampleLoanServiceImpl implements SampleLoanService {

    public static final int STATUS_BORROWING = 1;
    public static final int STATUS_RETURNED = 2;
    public static final int STATUS_NO_RETURN = 3;

    @Resource
    private SampleLoanMapper sampleLoanMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DictDataApi dictDataApi;

    private List<SampleLoanOptionRespVO> enabledOptions(String dictType) {
        return dictDataApi.getDictDataList(dictType).stream()
                .filter(item -> Objects.equals(item.getStatus(), 0))
                .map(item -> new SampleLoanOptionRespVO(item.getLabel(), item.getValue()))
                .toList();
    }

    @Override
    public List<SampleLoanOptionRespVO> getSiteOptions() {
        return enabledOptions("sample_loan_site");
    }

    @Override
    public List<SampleLoanOptionRespVO> getLocationOptions(String site) {
        if (StrUtil.isBlank(site) || getSiteOptions().stream().noneMatch(item -> item.getValue().equals(site))) {
            return List.of();
        }
        return enabledOptions("sample_loan_floor_" + site.toLowerCase(java.util.Locale.ROOT));
    }

    private void validateLocation(String site, String location) {
        if (getLocationOptions(site).stream().noneMatch(item -> item.getValue().equals(location))) {
            throw exception(SAMPLE_LOAN_LOCATION_INVALID);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSampleLoan(SampleLoanCreateReqVO createReqVO) {
        validateLocation(createReqVO.getSite(), createReqVO.getLocation());
        String basNo = StrUtil.trim(createReqVO.getBasNo());
        SampleLoanDO sampleLoan = BeanUtils.toBean(createReqVO, SampleLoanDO.class);
        sampleLoan.setBasNo(basNo)
                .setMaterialType(StrUtil.trim(createReqVO.getMaterialType()))
                .setSite(StrUtil.trim(createReqVO.getSite()))
                .setLocation(StrUtil.trim(createReqVO.getLocation()))
                .setRequester(StrUtil.trim(createReqVO.getRequester()))
                .setSubmitter(StrUtil.trim(createReqVO.getSubmitter()))
                .setStatus(STATUS_BORROWING);
        sampleLoanMapper.insert(sampleLoan);
        return sampleLoan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SampleLoanDO createSelfSampleLoan(SampleLoanSelfCreateReqVO createReqVO) {
        validateLocation(createReqVO.getSite(), createReqVO.getLocation());
        Long userId = Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录");
        AdminUserRespDTO user = Objects.requireNonNull(adminUserApi.getUser(userId), "当前用户不存在");
        String userName = StrUtil.blankToDefault(user.getNickname(), userId.toString());
        SampleLoanDO sampleLoan = new SampleLoanDO();
        sampleLoan.setBasNo(StrUtil.trim(createReqVO.getBasNo()))
                .setMaterialType(createReqVO.getMaterialType())
                .setSite(createReqVO.getSite())
                .setLocation(createReqVO.getLocation())
                .setRequesterId(userId)
                .setRequester(userName)
                .setSubmitterId(userId)
                .setSubmitter(userName)
                .setSampleInfo(StrUtil.trim(createReqVO.getSampleInfo()))
                .setRemark(StrUtil.trim(createReqVO.getRemark()))
                .setStatus(STATUS_BORROWING);
        sampleLoanMapper.insert(sampleLoan);
        return sampleLoan;
    }

    @Override
    public PageResult<SampleLoanDO> getSelfSampleLoanPage(SampleLoanPageReqVO pageReqVO) {
        pageReqVO.setRequesterId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录"));
        return sampleLoanMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSelfSampleLoan(SampleLoanSelfUpdateReqVO updateReqVO) {
        validateLocation(updateReqVO.getSite(), updateReqVO.getLocation());
        Long userId = Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录");
        SampleLoanDO existing = sampleLoanMapper.selectById(updateReqVO.getId());
        if (existing == null || !userId.equals(existing.getRequesterId())) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        SampleLoanDO updateObj = new SampleLoanDO();
        updateObj.setId(existing.getId());
        updateObj.setBasNo(StrUtil.trim(updateReqVO.getBasNo()));
        updateObj.setMaterialType(updateReqVO.getMaterialType());
        updateObj.setSite(updateReqVO.getSite());
        updateObj.setLocation(updateReqVO.getLocation());
        updateObj.setSampleInfo(StrUtil.nullToEmpty(StrUtil.trim(updateReqVO.getSampleInfo())));
        updateObj.setRemark(StrUtil.nullToEmpty(StrUtil.trim(updateReqVO.getRemark())));
        sampleLoanMapper.updateById(updateObj);
    }

    private SampleLoanDO getOwnSampleLoan(Long id) {
        Long userId = Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录");
        SampleLoanDO record = sampleLoanMapper.selectById(id);
        if (record == null || !userId.equals(record.getRequesterId())) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnSelfSampleLoan(Long id) {
        getOwnSampleLoan(id);
        returnSampleLoan(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void noReturnSelfSampleLoan(Long id) {
        getOwnSampleLoan(id);
        noReturnSampleLoan(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSelfSampleLoan(Long id) {
        getOwnSampleLoan(id);
        sampleLoanMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSampleLoan(SampleLoanUpdateReqVO updateReqVO) {
        validateLocation(updateReqVO.getSite(), updateReqVO.getLocation());
        SampleLoanDO sampleLoan = sampleLoanMapper.selectById(updateReqVO.getId());
        if (sampleLoan == null) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        String basNo = StrUtil.trim(updateReqVO.getBasNo());
        SampleLoanDO updateObj = BeanUtils.toBean(updateReqVO, SampleLoanDO.class);
        updateObj.setBasNo(basNo)
                .setMaterialType(StrUtil.trim(updateReqVO.getMaterialType()))
                .setSite(StrUtil.trim(updateReqVO.getSite()))
                .setLocation(StrUtil.trim(updateReqVO.getLocation()))
                .setRequester(StrUtil.trim(updateReqVO.getRequester()))
                .setSubmitter(StrUtil.trim(updateReqVO.getSubmitter()));
        sampleLoanMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSampleLoan(Long id) {
        if (sampleLoanMapper.selectById(id) == null) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        sampleLoanMapper.deleteById(id);
    }

    @Override
    public boolean hasBorrowingSampleLoan(String basNo, String site, String location, Long requesterId, Long excludeId) {
        return sampleLoanMapper.selectBorrowingByKey(
                StrUtil.trim(basNo), StrUtil.trim(site), StrUtil.trim(location), requesterId, excludeId) != null;
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
    @Transactional(rollbackFor = Exception.class)
    public void noReturnSampleLoan(Long id) {
        SampleLoanDO sampleLoan = sampleLoanMapper.selectById(id);
        if (sampleLoan == null) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        if (!Integer.valueOf(STATUS_BORROWING).equals(sampleLoan.getStatus())) {
            throw exception(SAMPLE_LOAN_NOT_BORROWING);
        }
        SampleLoanDO updateObj = new SampleLoanDO();
        updateObj.setId(id);
        updateObj.setStatus(STATUS_NO_RETURN);
        sampleLoanMapper.updateById(updateObj);
    }

    @Override
    public PageResult<SampleLoanDO> getSampleLoanPage(SampleLoanPageReqVO pageReqVO) {
        return sampleLoanMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SampleLoanDO> getBorrowingSampleLoanList(String location) {
        validateLocation("NINGBO", location);
        return sampleLoanMapper.selectBorrowingList(location, LocalDateTime.now().minusHours(24));
    }
}
