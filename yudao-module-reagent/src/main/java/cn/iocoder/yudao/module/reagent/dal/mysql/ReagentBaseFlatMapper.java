package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatPageReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentBaseFlatSimplePageReqVO;
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
                .likeIfPresent(ReagentBaseFlatDO::getWarehouse, reqVO.getWarehouse())
                .likeIfPresent(ReagentBaseFlatDO::getVendor, reqVO.getVendor())
                .likeIfPresent(ReagentBaseFlatDO::getBrand, reqVO.getBrand())
                .eqIfPresent(ReagentBaseFlatDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentBaseFlatDO::getSyncTime)
                .orderByDesc(ReagentBaseFlatDO::getId));
    }

    /** 分页简易搜索（申请单选批号用；关键词 + 仓库） */
    default PageResult<ReagentBaseFlatDO> selectSimplePage(ReagentBaseFlatSimplePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .likeIfPresent(ReagentBaseFlatDO::getWarehouse, reqVO.getWarehouse())
                .and(StrUtil.isNotBlank(reqVO.getKeyword()), w -> w
                        .like(ReagentBaseFlatDO::getBasId, reqVO.getKeyword())
                        .or().like(ReagentBaseFlatDO::getReagentCode, reqVO.getKeyword())
                        .or().like(ReagentBaseFlatDO::getReagentName, reqVO.getKeyword())
                        .or().like(ReagentBaseFlatDO::getCatNo, reqVO.getKeyword())
                        .or().like(ReagentBaseFlatDO::getBrand, reqVO.getKeyword()))
                .orderByDesc(ReagentBaseFlatDO::getSyncTime)
                .orderByDesc(ReagentBaseFlatDO::getId));
    }

    /** 批量 upsert（同步用，按 src_line_id 幂等） */
    int batchUpsertFlat(@Param("list") List<ReagentBaseFlatDO> list);

    /** 物理删除（同步数据；重新同步可恢复） */
    int deleteByIdPhysical(@Param("id") Long id);

    /** 按 试剂编号+批号 查扁平表行（打印补全 供应商/温度/规格 用，取一条）
     *  注意：不要用 selectOne(...).last("LIMIT 1")——PG 下 MyBatis-Plus 会走 selectCursor，
     *  游标未彻底关闭导致连接池复用时报 "statement 已经被关闭"（第二次调用必现 500）。
     *  改为 selectList（完整读取并关闭）后取第一条。 */
    default ReagentBaseFlatDO selectByReagentCodeAndLotNo(String reagentCode, String lotNo) {
        List<ReagentBaseFlatDO> list = selectList(new LambdaQueryWrapperX<ReagentBaseFlatDO>()
                .eq(ReagentBaseFlatDO::getReagentCode, reagentCode)
                .eq(StrUtil.isNotBlank(lotNo), ReagentBaseFlatDO::getLotNo, lotNo)
                .last("LIMIT 1"));
        return CollUtil.isEmpty(list) ? null : list.get(0);
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
