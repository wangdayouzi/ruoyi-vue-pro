package cn.iocoder.yudao.module.reagent.service;

import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalUpdateReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentClinicalMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.REAGENT_CLINICAL_NOT_EXISTS;

/**
 * 临床方案（老样本系统同步）Service 实现
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentClinicalServiceImpl implements ReagentClinicalService {

    @Resource
    private ReagentClinicalMapper reagentClinicalMapper;

    @Override
    public PageResult<ReagentClinicalDO> getClinicalPage(ReagentClinicalPageReqVO reqVO) {
        return reagentClinicalMapper.selectPage(reqVO);
    }

    @Override
    public List<ReagentClinicalDO> listOptions(String keyword) {
        return reagentClinicalMapper.selectOptions(keyword);
    }

    @Override
    @LogRecord(type = "试剂管理-临床方案", subType = "更新自动接收",
            bizNo = "{{#reqVO.id}}", success = "自动接收：{{#previousAutoReceive}} → {{#reqVO.isAutoReceive}}")
    public void updateClinicalLocalField(ReagentClinicalUpdateReqVO reqVO) {
        ReagentClinicalDO clinical = reagentClinicalMapper.selectById(reqVO.getId());
        if (clinical == null) {
            throw exception(REAGENT_CLINICAL_NOT_EXISTS);
        }
        LogRecordContext.putVariable("previousAutoReceive", clinical.getIsAutoReceive());
        ReagentClinicalDO updateObj = new ReagentClinicalDO();
        updateObj.setId(reqVO.getId());
        updateObj.setIsAutoReceive(reqVO.getIsAutoReceive());
        reagentClinicalMapper.updateById(updateObj);
        log.info("[reagent-clinical] 更新本地字段 {}：isAutoReceive={}", reqVO.getId(), reqVO.getIsAutoReceive());
    }

}
