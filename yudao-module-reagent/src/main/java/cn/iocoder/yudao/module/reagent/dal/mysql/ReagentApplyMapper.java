package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentApplyPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

/**
 * 试剂申请主表 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ReagentApplyMapper extends BaseMapperX<ReagentApplyDO> {

    default PageResult<ReagentApplyDO> selectPage(ReagentApplyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReagentApplyDO>()
                .likeIfPresent(ReagentApplyDO::getApplyNo, reqVO.getApplyNo())
                .likeIfPresent(ReagentApplyDO::getReceiverUnit, reqVO.getReceiverUnit())
                .exists(StringUtils.hasText(reqVO.getTrackingNumber()),
                        "SELECT 1 FROM reagent_shipment shipment "
                                + "WHERE shipment.apply_id = reagent_apply.id "
                                + "AND shipment.deleted = 0 "
                                + "AND shipment.tracking_number = {0}", reqVO.getTrackingNumber())
                .exists(StringUtils.hasText(reqVO.getReagentName()),
                        "SELECT 1 FROM reagent_apply_item item "
                                + "WHERE item.apply_id = reagent_apply.id "
                                + "AND item.deleted = 0 "
                                + "AND item.reagent_name LIKE CONCAT('%', {0}, '%')", reqVO.getReagentName())
                .eqIfPresent(ReagentApplyDO::getStatus, reqVO.getStatus())
                .orderByDesc(ReagentApplyDO::getId));
    }

    default ReagentApplyDO selectByApplyNo(String applyNo) {
        return selectOne(ReagentApplyDO::getApplyNo, applyNo);
    }

    default ReagentApplyDO selectByProcessInstanceId(String processInstanceId) {
        return selectOne(ReagentApplyDO::getProcessInstanceId, processInstanceId);
    }

}
