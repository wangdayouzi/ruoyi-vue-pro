package cn.iocoder.yudao.module.reagent.dal.mysql;

import org.springframework.util.StringUtils;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Mapper
public interface SampleUrgentMapper extends BaseMapperX<SampleUrgentDO> {
    /** 自定义查询包含软删除记录，避免删除某天的单据后重新生成。 */
    @Select("SELECT COUNT(*) FROM sample_urgent WHERE recurring_plan_id = #{planId} AND recurring_date = #{date}")
    long countPlanDay(@Param("planId") Long planId, @Param("date") LocalDate date);
    default SampleUrgentDO lock(Long id) {
        return selectOne(new LambdaQueryWrapperX<SampleUrgentDO>().eq(SampleUrgentDO::getId,id).last("FOR UPDATE"));
    }
    default PageResult<SampleUrgentDO> page(SampleUrgentPageReqVO req, Long userId) {
        var q = new LambdaQueryWrapperX<SampleUrgentDO>();
        if (req.getKeyword()!=null && !req.getKeyword().isBlank()) {
            q.and(w -> w.apply("project_no ILIKE {0}", "%" + req.getKeyword() + "%")
                    .or().apply("bas_nos ILIKE {0}", "%" + req.getKeyword() + "%")
                    .or().apply("sample_scope ILIKE {0}", "%" + req.getKeyword() + "%")
                    .or().apply("remark ILIKE {0}", "%" + req.getKeyword() + "%")
                    .or().apply("logistics_no ILIKE {0}", "%" + req.getKeyword() + "%"));
        }
        q.apply(StringUtils.hasText(req.getContacts()), "contacts ILIKE {0}", "%" + req.getContacts() + "%")
                .eqIfPresent(SampleUrgentDO::getStatus,req.getStatus());
        q.eq("NINGBO".equals(req.getInboundSite()) || "SHANGHAI".equals(req.getInboundSite()),
                SampleUrgentDO::getInboundSite, req.getInboundSite());
        q.betweenIfPresent(SampleUrgentDO::getDeadline, req.getDeadline())
                .betweenIfPresent(SampleUrgentDO::getCreateTime, req.getCreateTime())
                .eqIfPresent(SampleUrgentDO::getRecurringPlanId, req.getRecurringPlanId());
        if (Boolean.TRUE.equals(req.getExceptionOnly())) q.ne(SampleUrgentDO::getExceptionType,"NONE");
        if ("MINE".equals(req.getView())) q.and(w->w.apply("appointment_user_ids::jsonb @> {0}::jsonb","["+userId+"]")
                .or().nested(v->v.apply("appointment_user_ids::jsonb = '[]'::jsonb").eq(SampleUrgentDO::getRequesterId,userId)));
        if ("SUBMITTED".equals(req.getView())) q.eq(SampleUrgentDO::getRequesterId,userId);
        if ("HANDLING".equals(req.getView())) q.eq(SampleUrgentDO::getHandlerId,userId).in(SampleUrgentDO::getStatus,0,1,2);
        if ("TODAY".equals(req.getView())) q.ge(SampleUrgentDO::getDeadline,LocalDateTime.now().toLocalDate().atStartOfDay())
                .lt(SampleUrgentDO::getDeadline,LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()).in(SampleUrgentDO::getStatus,0,1,2);
        if ("OVERDUE".equals(req.getView())) q.lt(SampleUrgentDO::getDeadline,LocalDateTime.now()).in(SampleUrgentDO::getStatus,0,1,2);
        // 显式时间排序覆盖默认逾期优先，同一时间按编号倒序保持分页稳定。
        if (("deadline".equals(req.getSortField()) || "createTime".equals(req.getSortField()))
                && ("asc".equals(req.getSortOrder()) || "desc".equals(req.getSortOrder()))) {
            boolean ascending = "asc".equals(req.getSortOrder());
            if ("deadline".equals(req.getSortField())) q.orderBy(true, ascending, SampleUrgentDO::getDeadline);
            else q.orderBy(true, ascending, SampleUrgentDO::getCreateTime);
            return selectPage(req, q.orderByDesc(SampleUrgentDO::getId));
        }
        // 未选择排序时，逾期且尚未结束的需求优先，各组内按登记编号倒序。
        return selectPage(req,q.last("ORDER BY CASE WHEN status IN (0,1,2) AND deadline < CURRENT_TIMESTAMP THEN 0 ELSE 1 END ASC, id DESC"));
    }
}
