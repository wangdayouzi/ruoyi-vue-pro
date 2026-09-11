package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSaveReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSimplePageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseFlatMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.REAGENT_BASE_NOT_EXISTS;
import static cn.iocoder.yudao.module.reagent.enums.LogRecordConstants.*;

/**
 * 老ERP同步 试剂基础数据(扁平) Service 实现
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentBaseFlatServiceImpl implements ReagentBaseFlatService {

    @Resource
    private ReagentBaseFlatMapper reagentBaseFlatMapper;

    @Override
    public PageResult<ReagentBaseFlatDO> getReagentBaseFlatPage(ReagentBaseFlatPageReqVO pageReqVO) {
        return reagentBaseFlatMapper.selectPage(pageReqVO);
    }

    @Override
    @LogRecord(type = REAGENT_BASE_FLAT_TYPE, subType = REAGENT_BASE_FLAT_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = REAGENT_BASE_FLAT_UPDATE_SUCCESS)
    public void updateReagentBaseFlat(ReagentBaseFlatSaveReqVO updateReqVO) {
        ReagentBaseFlatDO oldBaseFlat = reagentBaseFlatMapper.selectById(updateReqVO.getId());
        if (oldBaseFlat == null) {
            throw exception(REAGENT_BASE_NOT_EXISTS);
        }
        // 只更新非空可维护字段（MyBatis-Plus 默认 NOT_NULL 策略，同步来源字段不会被动）
        ReagentBaseFlatDO updateObj = BeanUtils.toBean(updateReqVO, ReagentBaseFlatDO.class);
        reagentBaseFlatMapper.updateById(updateObj);
        log.info("[reagent-base-flat] 更新 {}：名称={}, 供应商={}, 货号={}", updateReqVO.getId(),
                updateReqVO.getReagentName(), updateReqVO.getVendor(), updateReqVO.getCatNo());
        LogRecordContext.putVariable("baseFlat", oldBaseFlat);
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT,
                BeanUtils.toBean(oldBaseFlat, ReagentBaseFlatSaveReqVO.class));
    }

    @Override
    public PageResult<ReagentBaseFlatDO> getReagentBaseFlatSimplePage(ReagentBaseFlatSimplePageReqVO reqVO) {
        return reagentBaseFlatMapper.selectSimplePage(reqVO);
    }

    @Override
    @LogRecord(type = REAGENT_BASE_FLAT_TYPE, subType = REAGENT_BASE_FLAT_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = REAGENT_BASE_FLAT_DELETE_SUCCESS)
    public void deleteReagentBaseFlat(Long id) {
        ReagentBaseFlatDO baseFlat = reagentBaseFlatMapper.selectById(id);
        if (baseFlat == null) {
            throw exception(REAGENT_BASE_NOT_EXISTS);
        }
        reagentBaseFlatMapper.deleteByIdPhysical(id);
        log.info("[reagent-base-flat] 删除 {}：id={}", id);
        LogRecordContext.putVariable("baseFlat", baseFlat);
    }

}
