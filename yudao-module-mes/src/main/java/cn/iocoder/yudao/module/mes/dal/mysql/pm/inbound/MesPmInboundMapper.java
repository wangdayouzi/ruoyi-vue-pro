package cn.iocoder.yudao.module.mes.dal.mysql.pm.inbound;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pm.inbound.vo.MesPmInboundRespVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound.MesPmInboundDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound.MesPmInboundLineDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老ERP同步-采购入库单 Mapper
 *
 * @author yudao
 */
@Mapper
public interface MesPmInboundMapper extends BaseMapperX<MesPmInboundDO> {

    /** 批量 upsert 主表（按 src_receipt_id 冲突更新） */
    int batchUpsertMaster(@Param("list") List<MesPmInboundDO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 明细表（按 src_line_id 冲突更新） */
    int batchUpsertLine(@Param("list") List<MesPmInboundLineDO> list, @Param("syncBatch") Long syncBatch);

    /** 明细数据分页：明细表 JOIN 主表，扁平行（页面用） */
    IPage<MesPmInboundRespVO> selectPageDetail(Page<MesPmInboundRespVO> page,
                                               @Param("reqVO") MesPmInboundPageReqVO reqVO);
}
