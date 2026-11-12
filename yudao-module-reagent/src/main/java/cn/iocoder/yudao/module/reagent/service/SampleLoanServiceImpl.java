package cn.iocoder.yudao.module.reagent.service;

import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanCreateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanOptionRespVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanSelfUpdateReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleLoanUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleLoanDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleLoanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendReqDTO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
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

    @Resource
    private DingTalkNotificationQueue notificationQueue;

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
    @LogRecord(type = "试剂管理-领用", subType = "登记领用",
            bizNo = "{{#_ret}}", success = "登记领用，业务编号 {{#_ret}}，BAS号 {{#createReqVO.basNo}}，领用类型 {{#createReqVO.materialType}}，站点 {{#createReqVO.site}}，楼层 {{#createReqVO.location}}，需求人编号 {{#createReqVO.requesterId}}")
    public Long createSampleLoan(SampleLoanCreateReqVO createReqVO) {
        validateLocation(createReqVO.getSite(), createReqVO.getLocation());
        Long userId = Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录");
        AdminUserRespDTO user = Objects.requireNonNull(adminUserApi.getUser(userId), "当前用户不存在");
        String userName = StrUtil.blankToDefault(user.getNickname(), userId.toString());
        Long requesterId = createReqVO.getRequesterId();
        AdminUserRespDTO requester = userId.equals(requesterId) ? user : adminUserApi.validateUser(requesterId);
        SampleLoanDO sampleLoan = new SampleLoanDO();
        sampleLoan.setBasNo(StrUtil.trim(createReqVO.getBasNo()))
                .setMaterialType(createReqVO.getMaterialType())
                .setSite(createReqVO.getSite())
                .setLocation(createReqVO.getLocation())
                .setRequesterId(requesterId)
                .setRequester(StrUtil.blankToDefault(requester.getNickname(), requesterId.toString()))
                .setSubmitterId(userId)
                .setSubmitter(userName)
                .setSampleInfo(StrUtil.trim(createReqVO.getSampleInfo()))
                .setRemark(StrUtil.trim(createReqVO.getRemark()))
                .setStatus(STATUS_BORROWING);
        sampleLoanMapper.insert(sampleLoan);
        enqueueCreatedNotification(sampleLoan);
        return sampleLoan.getId();
    }

    private void enqueueCreatedNotification(SampleLoanDO sampleLoan) {
        String businessId = "sample-loan:" + sampleLoan.getId();
        DingTalkMessageSendReqDTO request = new DingTalkMessageSendReqDTO();
        request.setBusinessId(businessId);
        request.setRequestId(businessId + ":created");
        request.setTemplateCode("sample_loan_created");
        request.setUserIds(List.of(sampleLoan.getRequesterId()));
        String siteName = getSiteOptions().stream()
                .filter(option -> option.getValue().equals(sampleLoan.getSite()))
                .map(SampleLoanOptionRespVO::getLabel)
                .findFirst().orElse(sampleLoan.getSite());
        request.setParams(Map.of(
                "basNo", sampleLoan.getBasNo(),
                "materialType", "REAGENT".equals(sampleLoan.getMaterialType()) ? "试剂" : "样品",
                "site", siteName,
                "location", sampleLoan.getLocation(),
                "requester", sampleLoan.getRequester(),
                "submitter", sampleLoan.getSubmitter()));
        notificationQueue.enqueue(request, LocalDateTime.now(), null, null);
    }

    @Override
    public PageResult<SampleLoanDO> getSelfSampleLoanPage(SampleLoanPageReqVO pageReqVO) {
        pageReqVO.setRequesterId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录"));
        return sampleLoanMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "修改领用",
            bizNo = "{{#updateReqVO.id}}", success = "修改领用，业务编号 {{#updateReqVO.id}}，{{#loanChanges}}")
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
        updateObj.setCreateTime(updateReqVO.getCreateTime());
        updateObj.setReturnTime(updateReqVO.getReturnTime());
        updateLoan(existing, updateObj);
        recordLoanChanges(existing, updateObj);
    }

    private void recordLoanChanges(SampleLoanDO before, SampleLoanDO update) {
        List<String> changes = new ArrayList<>();
        addChange(changes, "BAS号", before.getBasNo(), update.getBasNo());
        addChange(changes, "领用类型", before.getMaterialType(), update.getMaterialType());
        addChange(changes, "站点", before.getSite(), update.getSite());
        addChange(changes, "楼层", before.getLocation(), update.getLocation());
        addChange(changes, "需求人编号", before.getRequesterId(), update.getRequesterId());
        addChange(changes, "需求人", before.getRequester(), update.getRequester());
        addChange(changes, "提单人编号", before.getSubmitterId(), update.getSubmitterId());
        addChange(changes, "提单人", before.getSubmitter(), update.getSubmitter());
        addChange(changes, "物料信息", before.getSampleInfo(), update.getSampleInfo());
        addChange(changes, "备注", before.getRemark(), update.getRemark());
        addChange(changes, "领用时间", before.getCreateTime(), update.getCreateTime());
        addChange(changes, "归还时间", before.getReturnTime(), update.getReturnTime());
        LogRecordContext.putVariable("loanChanges", changes.isEmpty() ? "字段值未变化" : String.join("；", changes));
    }

    private void addChange(List<String> changes, String label, Object before, Object after) {
        // 更新对象中的 null 字段由 Mapper 忽略。
        if (after != null && !Objects.equals(before, after)) {
            changes.add(label + "【" + logValue(before) + "】→【" + logValue(after) + "】");
        }
    }

    private String logValue(Object value) {
        String text = Objects.toString(value, "");
        return text.length() <= 80 ? text : text.substring(0, 79) + "…";
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
    @LogRecord(type = "试剂管理-领用", subType = "归还",
            bizNo = "{{#id}}", success = "归还，业务编号 {{#id}}，状态：领用中 → 已归还，归还时间 {{#loanReturnTime}}")
    public void returnSelfSampleLoan(Long id) {
        getOwnSampleLoan(id);
        returnSampleLoan(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "标记无需归还",
            bizNo = "{{#id}}", success = "标记无需归还，业务编号 {{#id}}，状态：领用中 → 无需归还")
    public void noReturnSelfSampleLoan(Long id) {
        getOwnSampleLoan(id);
        noReturnSampleLoan(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "删除领用",
            bizNo = "{{#id}}", success = "删除领用，业务编号 {{#id}}，BAS号 {{#deletedLoan.basNo}}，领用类型 {{#deletedLoan.materialType}}")
    public void deleteSelfSampleLoan(Long id) {
        LogRecordContext.putVariable("deletedLoan", getOwnSampleLoan(id));
        sampleLoanMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "修改领用",
            bizNo = "{{#updateReqVO.id}}", success = "修改领用，业务编号 {{#updateReqVO.id}}，{{#loanChanges}}")
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
        updateLoan(sampleLoan, updateObj);
        recordLoanChanges(sampleLoan, updateObj);
    }

    private void updateLoan(SampleLoanDO existing, SampleLoanDO update) {
        if (update.getCreateTime() == null && update.getReturnTime() == null) {
            sampleLoanMapper.updateById(update);
            return;
        }
        if (update.getReturnTime() != null && !Integer.valueOf(STATUS_RETURNED).equals(existing.getStatus())) {
            throw invalidParamException("只有已归还的记录可以修改归还时间");
        }
        LocalDateTime createTime = update.getCreateTime() != null ? update.getCreateTime() : existing.getCreateTime();
        LocalDateTime returnTime = update.getReturnTime() != null ? update.getReturnTime() : existing.getReturnTime();
        if (createTime != null && returnTime != null && returnTime.isBefore(createTime)) {
            throw invalidParamException("归还时间不能早于领用时间");
        }
        int updated = sampleLoanMapper.update(update, new LambdaUpdateWrapper<SampleLoanDO>()
                .eq(SampleLoanDO::getId, existing.getId())
                .eq(SampleLoanDO::getStatus, existing.getStatus())
                .eq(existing.getUpdateTime() != null, SampleLoanDO::getUpdateTime, existing.getUpdateTime())
                .isNull(existing.getUpdateTime() == null, SampleLoanDO::getUpdateTime));
        if (updated == 0) {
            throw invalidParamException("领用记录已变更，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "删除领用",
            bizNo = "{{#id}}", success = "删除领用，业务编号 {{#id}}，BAS号 {{#deletedLoan.basNo}}，领用类型 {{#deletedLoan.materialType}}")
    public void deleteSampleLoan(Long id) {
        SampleLoanDO sampleLoan = sampleLoanMapper.selectById(id);
        if (sampleLoan == null) {
            throw exception(SAMPLE_LOAN_NOT_EXISTS);
        }
        LogRecordContext.putVariable("deletedLoan", sampleLoan);
        sampleLoanMapper.deleteById(id);
    }

    @Override
    public boolean hasBorrowingSampleLoan(String basNo, String site, String location, Long requesterId, Long excludeId) {
        return sampleLoanMapper.selectBorrowingByKey(
                StrUtil.trim(basNo), StrUtil.trim(site), StrUtil.trim(location), requesterId, excludeId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "归还",
            bizNo = "{{#id}}", success = "归还，业务编号 {{#id}}，状态：领用中 → 已归还，归还时间 {{#loanReturnTime}}")
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
        Long userId = Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId(), "请先登录");
        AdminUserRespDTO user = Objects.requireNonNull(adminUserApi.getUser(userId), "当前用户不存在");
        updateObj.setReturner(StrUtil.blankToDefault(user.getNickname(), userId.toString()));
        LogRecordContext.putVariable("loanReturnTime", updateObj.getReturnTime());
        updateBorrowingStatus(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = "试剂管理-领用", subType = "标记无需归还",
            bizNo = "{{#id}}", success = "标记无需归还，业务编号 {{#id}}，状态：领用中 → 无需归还")
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
        updateBorrowingStatus(updateObj);
    }

    private void updateBorrowingStatus(SampleLoanDO updateObj) {
        int updated = sampleLoanMapper.update(updateObj, new LambdaUpdateWrapper<SampleLoanDO>()
                .eq(SampleLoanDO::getId, updateObj.getId())
                .eq(SampleLoanDO::getStatus, STATUS_BORROWING));
        if (updated == 0) {
            throw exception(SAMPLE_LOAN_NOT_BORROWING);
        }
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
