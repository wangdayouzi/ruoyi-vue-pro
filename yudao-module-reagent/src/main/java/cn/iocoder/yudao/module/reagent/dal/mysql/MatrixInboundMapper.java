package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.MatrixInboundPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixBasNumber;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Mapper
public interface MatrixInboundMapper extends BaseMapperX<MatrixInboundDO> {
    @Select("""
            SELECT bas_number FROM (
                SELECT DISTINCT CASE WHEN site = 'SHANGHAI' THEN 'SH' || bas_no ELSE bas_no END AS bas_number
                FROM reagent_matrix_inbound
                WHERE tenant_id = #{tenantId} AND NOT deleted
            ) numbers
            WHERE strpos(lower(bas_number), lower(#{keyword})) > 0 OR #{keyword} = ''
            ORDER BY bas_number
            LIMIT 50
            """)
    @InterceptorIgnore(tenantLine = "true")
    List<String> selectBasOptions(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    @Select("""
            SELECT site, matrix_kind, bas_no FROM reagent_matrix_bas_history
            WHERE tenant_id = #{tenantId}
              AND (#{site} = '' OR site = #{site})
              AND (#{kind} = '' OR matrix_kind = #{kind})
              AND strpos(CASE WHEN site = 'SHANGHAI' THEN 'SH' || bas_no ELSE bas_no END, #{keyword}) > 0
            ORDER BY bas_no
            LIMIT 50001
            """)
    @InterceptorIgnore(tenantLine = "true")
    List<MatrixBasNumber> selectHistoricalBas(@Param("tenantId") Long tenantId, @Param("site") String site,
                                              @Param("kind") String kind, @Param("keyword") String keyword);

    default MatrixInboundDO selectByTenantId(Long tenantId, Long id) {
        return selectOne(new LambdaQueryWrapperX<MatrixInboundDO>().eq(MatrixInboundDO::getTenantId, tenantId)
                .eq(MatrixInboundDO::getId, id));
    }

    default MatrixInboundDO lock(Long tenantId, Long id) {
        return selectOne(new LambdaQueryWrapperX<MatrixInboundDO>().eq(MatrixInboundDO::getTenantId, tenantId)
                .eq(MatrixInboundDO::getId, id).last("FOR UPDATE"));
    }

    @Select("SELECT pg_advisory_xact_lock(hashtextextended(#{key},0))::text")
    @InterceptorIgnore(tenantLine = "true")
    String lockRequest(@Param("key") String key);

    @Select("""
            SELECT id,tenant_id,site,matrix_kind,bas_no,applicant_id,applicant_name,request_id,
                   created_at,appointment_date,lims_designed,lims_inbound,exception_remark,
                   registered_by,registered_name,registered_at,version,urgent,inbound_status,
                   appointment_user_ids,deleted,updated_by,updated_at
            FROM reagent_matrix_inbound
            WHERE tenant_id=#{tenantId} AND applicant_id=#{applicantId}
            AND request_id=CAST(#{requestId} AS uuid)
            """)
    @ResultMap("mybatis-plus_MatrixInboundDO")
    @InterceptorIgnore(tenantLine = "true")
    MatrixInboundDO selectByRequest(@Param("tenantId") Long tenantId, @Param("applicantId") Long applicantId,
                                   @Param("requestId") String requestId);

    default int updateCurrent(MatrixInboundDO row, Integer expectedVersion) {
        return update(row, new LambdaUpdateWrapper<MatrixInboundDO>()
                .eq(MatrixInboundDO::getTenantId, row.getTenantId()).eq(MatrixInboundDO::getId, row.getId())
                .eq(MatrixInboundDO::getVersion, expectedVersion));
    }

    @Update("""
            UPDATE reagent_matrix_inbound SET deleted=true,updated_by=#{operatorId},updated_at=#{now},version=version+1
            WHERE tenant_id=#{tenantId} AND id=#{id} AND version=#{version} AND NOT deleted AND inbound_status IN (0,1)
            """)
    @InterceptorIgnore(tenantLine = "true")
    int deleteCurrent(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("version") Integer version,
                      @Param("operatorId") Long operatorId, @Param("now") LocalDateTime now);

    default PageResult<MatrixInboundDO> page(MatrixInboundPageReqVO filter, Long tenantId, Long userId, LocalDate today) {
        return selectPage(filter, query(filter, tenantId, userId, today, false));
    }

    default List<MatrixInboundDO> export(MatrixInboundPageReqVO filter, Long tenantId, Long userId,
                                       LocalDate today, boolean basOrder) {
        return selectList(query(filter, tenantId, userId, today, basOrder).last("LIMIT 50001"));
    }

    default LambdaQueryWrapper<MatrixInboundDO> query(MatrixInboundPageReqVO filter, Long tenantId,
                                                     Long userId, LocalDate today, boolean basOrder) {
        var wrapper = new QueryWrapper<MatrixInboundDO>();
        var query = wrapper.lambda().eq(MatrixInboundDO::getTenantId, tenantId);
        if ("mine".equals(filter.getView())) query.and(part -> part.eq(MatrixInboundDO::getApplicantId, userId)
                .or().apply("appointment_user_ids @> {0}::jsonb", JsonUtils.toJsonString(List.of(userId))));
        if ("today".equals(filter.getView())) query.ge(MatrixInboundDO::getAppointmentDate, today.atStartOfDay())
                .lt(MatrixInboundDO::getAppointmentDate, today.plusDays(1).atStartOfDay());
        if ("urgent".equals(filter.getView())) query.eq(MatrixInboundDO::getUrgent, true);
        query.eq(filter.getSite() != null && !filter.getSite().isEmpty(), MatrixInboundDO::getSite, filter.getSite())
                .eq(filter.getMatrixKind() != null && !filter.getMatrixKind().isEmpty(), MatrixInboundDO::getMatrixKind, filter.getMatrixKind());
        if (filter.getApplicantName() != null && !filter.getApplicantName().isBlank())
            query.apply("strpos(lower(applicant_name),lower({0}))>0", filter.getApplicantName().trim());
        if (filter.getBasNo() != null && !filter.getBasNo().isBlank()) {
            String number = filter.getBasNo().trim().toUpperCase(Locale.ROOT);
            if (number.startsWith("SH")) {
                query.eq(MatrixInboundDO::getSite, "SHANGHAI");
                number = number.substring(2);
            }
            query.apply("strpos(bas_no,{0})>0", number);
        }
        if (filter.getFrom() != null) query.ge(MatrixInboundDO::getAppointmentDate, filter.getFrom().atStartOfDay());
        if (filter.getTo() != null) query.lt(MatrixInboundDO::getAppointmentDate, filter.getTo().plusDays(1).atStartOfDay());
        if (filter.getResult() != null) {
            switch (filter.getResult()) {
                case "unregistered" -> query.eq(MatrixInboundDO::getInboundStatus, 0);
                case "pending" -> query.eq(MatrixInboundDO::getInboundStatus, 1);
                case "partial" -> query.eq(MatrixInboundDO::getInboundStatus, 2);
                case "yes" -> query.eq(MatrixInboundDO::getInboundStatus, 3);
                default -> { }
            }
        }
        if (basOrder) {
            query.orderByAsc(MatrixInboundDO::getBasNo, MatrixInboundDO::getId);
            return query;
        }
        if (!"asc".equals(filter.getDateOrder()) && !"desc".equals(filter.getDateOrder())) {
            wrapper.orderByAsc("CASE WHEN urgent AND inbound_status IN (0, 1) THEN 0 ELSE 1 END");
        }
        if ("asc".equals(filter.getDateOrder())) query.orderByAsc(MatrixInboundDO::getAppointmentDate);
        if ("desc".equals(filter.getDateOrder())) query.orderByDesc(MatrixInboundDO::getAppointmentDate);
        query.orderByDesc(MatrixInboundDO::getCreatedAt, MatrixInboundDO::getId);
        return query;
    }

}
