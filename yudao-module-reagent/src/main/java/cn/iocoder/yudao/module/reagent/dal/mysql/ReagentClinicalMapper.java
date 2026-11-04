package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentClinicalPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 临床方案（本地同步表）Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentClinicalMapper extends BaseMapperX<ReagentClinicalDO> {

    default PageResult<ReagentClinicalDO> selectPage(ReagentClinicalPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentClinicalDO>()
                .likeIfPresent(ReagentClinicalDO::getName, reqVO.getName())
                .likeIfPresent(ReagentClinicalDO::getNumber, reqVO.getNumber())
                .eqIfPresent(ReagentClinicalDO::getSponsorId, reqVO.getSponsorId())
                .eqIfPresent(ReagentClinicalDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentClinicalDO::getSyncTime)
                .orderByAsc(ReagentClinicalDO::getNumber)
                .orderByAsc(ReagentClinicalDO::getId));
    }

    /** 下拉选项：按名称/编号模糊搜索，最多 50 条 */
    default List<ReagentClinicalDO> selectOptions(String keyword) {
        return selectList(new LambdaQueryWrapperX<ReagentClinicalDO>()
                .eq(ReagentClinicalDO::getStatus, 0)
                .and(StrUtil.isNotBlank(keyword), w -> w
                        .like(ReagentClinicalDO::getName, keyword)
                        .or().like(ReagentClinicalDO::getNumber, keyword))
                .orderByAsc(ReagentClinicalDO::getNumber)
                .last("LIMIT 50"));
    }

    /** 批量 upsert（按 id 幂等；is_auto_receive 不进更新，保留本地维护值） */
    int batchUpsertClinical(@Param("list") List<ReagentClinicalDO> list);

    /** 全量同步后：停用本次批次未出现的正常行（源表已删除 → status=1） */
    int disableMissingBatch(@Param("batch") Long batch);

}
