package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSaveReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseFlatMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public void updateReagentBaseFlat(ReagentBaseFlatSaveReqVO updateReqVO) {
        // 只更新非空可维护字段（MyBatis-Plus 默认 NOT_NULL 策略，同步来源字段不会被动）
        ReagentBaseFlatDO updateObj = BeanUtils.toBean(updateReqVO, ReagentBaseFlatDO.class);
        reagentBaseFlatMapper.updateById(updateObj);
        log.info("[reagent-base-flat] 更新 {}：名称={}, 供应商={}, 货号={}", updateReqVO.getId(),
                updateReqVO.getReagentName(), updateReqVO.getVendor(), updateReqVO.getCatNo());
    }

    @Override
    public PageResult<ReagentBaseFlatDO> getReagentBaseFlatSimplePage(PageParam pageReqVO, String keyword) {
        return reagentBaseFlatMapper.selectSimplePage(pageReqVO, keyword);
    }

    @Override
    public void deleteReagentBaseFlat(Long id) {
        reagentBaseFlatMapper.deleteByIdPhysical(id);
        log.info("[reagent-base-flat] 删除 {}：id={}", id);
    }

}
