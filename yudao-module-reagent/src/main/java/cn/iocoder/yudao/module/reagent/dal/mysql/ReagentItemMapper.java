package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目号（本地同步表）Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentItemMapper extends BaseMapperX<ReagentItemDO> {

    default PageResult<ReagentItemDO> selectPage(ReagentItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentItemDO>()
                .likeIfPresent(ReagentItemDO::getBaseId, reqVO.getBaseId())
                .eqIfPresent(ReagentItemDO::getClinicalId, reqVO.getClinicalId())
                .likeIfPresent(ReagentItemDO::getClinicalNumber, reqVO.getClinicalNumber())
                .likeIfPresent(ReagentItemDO::getClinicalName, reqVO.getClinicalName())
                .eqIfPresent(ReagentItemDO::getCityId, reqVO.getCityId())
                .eqIfPresent(ReagentItemDO::getReviewStatus, reqVO.getReviewStatus())
                .eqIfPresent(ReagentItemDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentItemDO::getSyncTime)
                .orderByAsc(ReagentItemDO::getBaseId)
                .orderByAsc(ReagentItemDO::getId));
    }

    /** 下拉选项：项目号/方案名称/方案编号模糊搜索，可按临床方案ID过滤，最多 50 条 */
    default List<ReagentItemDO> selectOptions(Long clinicalId, String keyword) {
        return selectList(new LambdaQueryWrapperX<ReagentItemDO>()
                .eq(ReagentItemDO::getStatus, 0)
                .eqIfPresent(ReagentItemDO::getClinicalId, clinicalId)
                .and(StrUtil.isNotBlank(keyword), w -> w
                        .like(ReagentItemDO::getBaseId, keyword)
                        .or().like(ReagentItemDO::getClinicalName, keyword)
                        .or().like(ReagentItemDO::getClinicalNumber, keyword))
                .orderByAsc(ReagentItemDO::getBaseId)
                .last("LIMIT 50"));
    }

    /** 批量 upsert（按 id 幂等；同步更新方案名称/编号） */
    int batchUpsertItem(@Param("list") List<ReagentItemDO> list);

    /** 全量同步后：停用本次批次未出现的正常行（源表已删除 → status=1） */
    int disableMissingBatch(@Param("batch") Long batch);

}
