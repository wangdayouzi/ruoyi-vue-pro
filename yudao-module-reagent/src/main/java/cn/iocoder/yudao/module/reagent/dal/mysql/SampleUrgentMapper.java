package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDateTime;

@Mapper
public interface SampleUrgentMapper extends BaseMapperX<SampleUrgentDO> {
    default SampleUrgentDO lock(Long id) {
        return selectOne(new LambdaQueryWrapperX<SampleUrgentDO>().eq(SampleUrgentDO::getId,id).last("FOR UPDATE"));
    }
    default PageResult<SampleUrgentDO> page(SampleUrgentPageReqVO req, Long userId) {
        var q = new LambdaQueryWrapperX<SampleUrgentDO>();
        if (req.getKeyword()!=null && !req.getKeyword().isBlank()) {
            q.and(w -> w.like(SampleUrgentDO::getProjectNo,req.getKeyword())
                    .or().like(SampleUrgentDO::getBasNos,req.getKeyword())
                    .or().like(SampleUrgentDO::getSampleScope,req.getKeyword())
                    .or().like(SampleUrgentDO::getRemark,req.getKeyword())
                    .or().like(SampleUrgentDO::getLogisticsNo,req.getKeyword()));
        }
        q.likeIfPresent(SampleUrgentDO::getContacts,req.getContacts())
                .eqIfPresent(SampleUrgentDO::getStatus,req.getStatus());
        if (Boolean.TRUE.equals(req.getExceptionOnly())) q.ne(SampleUrgentDO::getExceptionType,"NONE");
        if ("MINE".equals(req.getView())) q.and(w->w.apply("appointment_user_ids::jsonb @> {0}::jsonb","["+userId+"]")
                .or().nested(v->v.apply("appointment_user_ids::jsonb = '[]'::jsonb").eq(SampleUrgentDO::getRequesterId,userId)));
        if ("SUBMITTED".equals(req.getView())) q.eq(SampleUrgentDO::getRequesterId,userId);
        if ("HANDLING".equals(req.getView())) q.eq(SampleUrgentDO::getHandlerId,userId).in(SampleUrgentDO::getStatus,0,1,2);
        if ("TODAY".equals(req.getView())) q.ge(SampleUrgentDO::getDeadline,LocalDateTime.now().toLocalDate().atStartOfDay())
                .lt(SampleUrgentDO::getDeadline,LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()).in(SampleUrgentDO::getStatus,0,1,2);
        if ("OVERDUE".equals(req.getView())) q.lt(SampleUrgentDO::getDeadline,LocalDateTime.now()).in(SampleUrgentDO::getStatus,0,1,2);
        // 逾期且尚未结束的需求优先；各组内按纯数字登记编号倒序，保持分页排序稳定。
        return selectPage(req,q.last("ORDER BY CASE WHEN status IN (0,1,2) AND deadline < CURRENT_TIMESTAMP THEN 0 ELSE 1 END ASC, id DESC"));
    }
}
