package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentInboundFlatDTO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemTypeTreeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试剂同步 正式库源 Mapper（读 mes_pm_inbound_line / mes_md_item_type，master 数据源）
 *
 * @author yudao
 */
@Mapper
public interface ReagentFlatSourceMapper {

    /** 正式库入库单明细（join 主表取 入库单号/供应商/仓库 + 采购订单剩余；lastSyncTime 非空=增量水位） */
    List<ReagentInboundFlatDTO> selectInboundLineForFlat(@Param("lastSyncTime") LocalDateTime lastSyncTime);

    /** 物料分类树（mes_md_item_type，供"10 子树"过滤） */
    List<ReagentItemTypeTreeDTO> selectItemTypeTree();

}
