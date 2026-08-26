package cn.iocoder.yudao.module.mes.dal.mysql.pm.po;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mes.controller.admin.pm.po.vo.MesPmPoPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pm.po.MesPmPoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老ERP同步-采购订单明细 Mapper
 *
 * @author yudao
 */
@Mapper
public interface MesPmPoMapper extends BaseMapperX<MesPmPoDO> {

    /** 批量 upsert 采购订单明细（按 line_id 冲突更新） */
    int batchUpsert(@Param("list") List<MesPmPoDO> list, @Param("syncBatch") Long syncBatch);

    /** 清空采购订单正式表（窗口快照，先清再写） */
    int deleteAll();

    /** 分页：按 PO号/物料/供应商/日期过滤（字段参考原出入库流水） */
    default PageResult<MesPmPoDO> selectPage(MesPmPoPageReqVO reqVO) {
        LambdaQueryWrapperX<MesPmPoDO> wrapper = new LambdaQueryWrapperX<MesPmPoDO>()
                .likeIfPresent(MesPmPoDO::getPoCode, reqVO.getPoCode())
                .likeIfPresent(MesPmPoDO::getVendorName, reqVO.getVendorName())
                .likeIfPresent(MesPmPoDO::getItemCategory, reqVO.getItemCategory())
                .betweenIfPresent(MesPmPoDO::getOrderDate, reqVO.getOrderDate());
        // 物料：编码或名称 模糊（OR）—— 只在有值时加，避免生成空 () 组
        if (StrUtil.isNotBlank(reqVO.getItemName())) {
            wrapper.and(w -> w.like(MesPmPoDO::getItemCode, reqVO.getItemName())
                    .or().like(MesPmPoDO::getItemName, reqVO.getItemName()));
        }
        wrapper.orderByDesc(MesPmPoDO::getOrderDate).orderByDesc(MesPmPoDO::getPoCode);
        return selectPage(reqVO, wrapper);
    }
}
