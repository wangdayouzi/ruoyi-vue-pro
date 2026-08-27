package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 老ERP同步 试剂基础数据(扁平) Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentBaseFlatMapper extends BaseMapperX<ReagentBaseFlatDO> {

    default PageResult<ReagentBaseFlatDO> selectPage(ReagentBaseFlatPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .likeIfPresent(ReagentBaseFlatDO::getBasId, reqVO.getBasId())
                .likeIfPresent(ReagentBaseFlatDO::getReagentCode, reqVO.getReagentCode())
                .likeIfPresent(ReagentBaseFlatDO::getReagentName, reqVO.getReagentName())
                .likeIfPresent(ReagentBaseFlatDO::getCatNo, reqVO.getCatNo())
                .likeIfPresent(ReagentBaseFlatDO::getItemCategory, reqVO.getItemCategory())
                .eqIfPresent(ReagentBaseFlatDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentBaseFlatDO::getSyncTime)
                .orderByDesc(ReagentBaseFlatDO::getId));
    }

    /** 分页简易搜索（申请单选批号用；匹配 入库单号/试剂编号/试剂名称/货号） */
    default PageResult<ReagentBaseFlatDO> selectSimplePage(PageParam reqVO, String keyword) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .and(StrUtil.isNotBlank(keyword), w -> w
                        .like(ReagentBaseFlatDO::getBasId, keyword)
                        .or().like(ReagentBaseFlatDO::getReagentCode, keyword)
                        .or().like(ReagentBaseFlatDO::getReagentName, keyword)
                        .or().like(ReagentBaseFlatDO::getCatNo, keyword))
                .orderByDesc(ReagentBaseFlatDO::getSyncTime)
                .orderByDesc(ReagentBaseFlatDO::getId));
    }

    /** 批量 upsert（同步用，按 src_line_id 幂等） */
    int batchUpsertFlat(@Param("list") List<ReagentBaseFlatDO> list);

    /** 物理删除（同步数据；重新同步可恢复） */
    int deleteByIdPhysical(@Param("id") Long id);

    /** 按 试剂编号+批号 查扁平表行（打印补全 供应商/温度/规格 用，取一条） */
    default ReagentBaseFlatDO selectByReagentCodeAndLotNo(String reagentCode, String lotNo) {
        return selectOne(new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .eq(ReagentBaseFlatDO::getReagentCode, reagentCode)
                .eq(StrUtil.isNotBlank(lotNo), ReagentBaseFlatDO::getLotNo, lotNo)
                .last("LIMIT 1"));
    }

    /** 最新同步时间（增量水位；表空返回 null） */
    default LocalDateTime selectMaxSyncTime() {
        List<ReagentBaseFlatDO> list = selectList(new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .select(ReagentBaseFlatDO::getSyncTime)
                .orderByDesc(ReagentBaseFlatDO::getSyncTime)
                .last("LIMIT 1"));
        return CollUtil.isNotEmpty(list) ? list.get(0).getSyncTime() : null;
    }

    /** 停用非本次同步批次的正常行（全量同步：源缺失/作废 → 标停用） */
    int disableMissingBatch(@Param("batch") Long batch);

}
