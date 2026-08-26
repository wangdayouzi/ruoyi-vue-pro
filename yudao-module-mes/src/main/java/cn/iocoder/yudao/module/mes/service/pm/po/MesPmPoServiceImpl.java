package cn.iocoder.yudao.module.mes.service.pm.po;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.po.MesPmPoDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pm.po.MesPmPoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 老ERP采购订单数据 Service 实现
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class MesPmPoServiceImpl implements MesPmPoService {

    @Resource
    private MesPmPoMapper mesPmPoMapper;

    @Override
    public PageResult<MesPmPoRespVO> getPoPage(MesPmPoPageReqVO pageReqVO) {
        PageResult<MesPmPoDO> pageResult = mesPmPoMapper.selectPage(pageReqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), MesPmPoRespVO.class),
                pageResult.getTotal());
    }

}
