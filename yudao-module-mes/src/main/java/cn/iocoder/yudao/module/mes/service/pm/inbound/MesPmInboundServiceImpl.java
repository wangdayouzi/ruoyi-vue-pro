package cn.iocoder.yudao.module.mes.service.pm.inbound;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundRespVO;
import cn.iocoder.yudao.module.mes.dal.mysql.pm.inbound.MesPmInboundMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 老ERP采购入库单 Service 实现
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class MesPmInboundServiceImpl implements MesPmInboundService {

    @Resource
    private MesPmInboundMapper mesPmInboundMapper;

    @Override
    public PageResult<MesPmInboundRespVO> getDetailPage(MesPmInboundPageReqVO pageReqVO) {
        Page<MesPmInboundRespVO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<MesPmInboundRespVO> result = mesPmInboundMapper.selectPageDetail(page, pageReqVO);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

}
