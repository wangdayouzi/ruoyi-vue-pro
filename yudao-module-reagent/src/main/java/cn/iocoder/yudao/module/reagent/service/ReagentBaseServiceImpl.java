package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseLotDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseLotMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;

/**
 * 试剂基础数据 Service 实现
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ReagentBaseServiceImpl implements ReagentBaseService {

    @Resource
    private ReagentBaseMapper reagentBaseMapper;

    @Resource
    private ReagentBaseLotMapper reagentBaseLotMapper;

    // ==================== 试剂主表 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReagentBase(ReagentBaseSaveReqVO createReqVO) {
        validateBasIdUnique(null, createReqVO.getBasId());

        ReagentBaseDO reagent = BeanUtils.toBean(createReqVO, ReagentBaseDO.class);
        reagentBaseMapper.insert(reagent);
        return reagent.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReagentBase(ReagentBaseSaveReqVO updateReqVO) {
        validateReagentBaseExists(updateReqVO.getId());
        validateBasIdUnique(updateReqVO.getId(), updateReqVO.getBasId());

        ReagentBaseDO updateObj = BeanUtils.toBean(updateReqVO, ReagentBaseDO.class);
        reagentBaseMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReagentBase(Long id) {
        validateReagentBaseExists(id);
        reagentBaseMapper.deleteById(id);
        // 级联删除批号（逻辑删除）
        reagentBaseLotMapper.delete(ReagentBaseLotDO::getBaseId, id);
    }

    @Override
    public ReagentBaseDO getReagentBase(Long id) {
        return validateReagentBaseExists(id);
    }

    @Override
    public PageResult<ReagentBaseDO> getReagentBasePage(ReagentBasePageReqVO pageReqVO) {
        return reagentBaseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ReagentBaseRespVO> getReagentBaseSimpleList() {
        List<ReagentBaseDO> list = reagentBaseMapper.selectList(
                new LambdaQueryWrapperX<ReagentBaseDO>()
                        .eq(ReagentBaseDO::getStatus, 0));
        return BeanUtils.toBean(list, ReagentBaseRespVO.class);
    }

    // ==================== 试剂批号 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReagentBaseLot(ReagentBaseLotSaveReqVO createReqVO) {
        validateReagentBaseExists(createReqVO.getBaseId());
        validateLotNoUnique(createReqVO.getBaseId(), null, createReqVO.getLotNo());

        ReagentBaseLotDO lot = BeanUtils.toBean(createReqVO, ReagentBaseLotDO.class);
        reagentBaseLotMapper.insert(lot);
        return lot.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReagentBaseLot(ReagentBaseLotSaveReqVO updateReqVO) {
        validateLotExists(updateReqVO.getId());
        validateLotNoUnique(updateReqVO.getBaseId(), updateReqVO.getId(), updateReqVO.getLotNo());

        ReagentBaseLotDO updateObj = BeanUtils.toBean(updateReqVO, ReagentBaseLotDO.class);
        reagentBaseLotMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReagentBaseLot(Long id) {
        validateLotExists(id);
        reagentBaseLotMapper.deleteById(id);
    }

    @Override
    public List<ReagentBaseLotDO> getLotListByBaseId(Long baseId) {
        return reagentBaseLotMapper.selectListByBaseId(baseId);
    }

    @Override
    public List<ReagentBaseLotDO> getLotListByBasId(String basId) {
        // 先根据 basId 找到 reagent_base，再查批号
        ReagentBaseDO base = reagentBaseMapper.selectByBasId(basId);
        if (base == null) {
            return List.of();
        }
        return reagentBaseLotMapper.selectListByBaseId(base.getId());
    }

    // ==================== 私有校验方法 ====================

    private ReagentBaseDO validateReagentBaseExists(Long id) {
        ReagentBaseDO reagent = reagentBaseMapper.selectById(id);
        if (reagent == null) {
            throw exception(REAGENT_BASE_NOT_EXISTS);
        }
        return reagent;
    }

    private void validateBasIdUnique(Long id, String basId) {
        ReagentBaseDO existing = reagentBaseMapper.selectByBasId(basId);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(REAGENT_BASE_BAS_ID_DUPLICATE);
        }
    }

    private ReagentBaseLotDO validateLotExists(Long id) {
        ReagentBaseLotDO lot = reagentBaseLotMapper.selectById(id);
        if (lot == null) {
            throw exception(REAGENT_LOT_NOT_EXISTS);
        }
        return lot;
    }

    private void validateLotNoUnique(Long baseId, Long lotId, String lotNo) {
        ReagentBaseLotDO existing = reagentBaseLotMapper.selectByBaseIdAndLotNo(baseId, lotNo);
        if (existing != null && !existing.getId().equals(lotId)) {
            throw exception(REAGENT_LOT_NO_DUPLICATE);
        }
    }

}
