package cn.iocoder.yudao.module.reagent.dal.mysql;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface MatrixInboundCounterMapper {
    @Insert("""
            INSERT INTO reagent_matrix_inbound_counter(tenant_id,site,issue_year,matrix_kind,last_no)
            VALUES(#{tenantId},#{site},#{year},#{kind},0)
            ON CONFLICT(tenant_id,site,issue_year,matrix_kind) DO NOTHING
            """)
    void ensureCounter(@Param("tenantId") Long tenantId, @Param("site") String site,
                       @Param("year") int year, @Param("kind") String kind);

    @Select("""
            SELECT last_no FROM reagent_matrix_inbound_counter
            WHERE tenant_id=#{tenantId} AND site=#{site} AND issue_year=#{year} AND matrix_kind=#{kind} FOR UPDATE
            """)
    Integer lockCounter(@Param("tenantId") Long tenantId, @Param("site") String site,
                                      @Param("year") int year, @Param("kind") String kind);

    @Update("""
            UPDATE reagent_matrix_inbound_counter SET last_no=last_no+1
            WHERE tenant_id=#{tenantId} AND site=#{site} AND issue_year=#{year} AND matrix_kind=#{kind}
              AND last_no=#{expected} AND last_no<9999
            """)
    int advanceCounter(@Param("tenantId") Long tenantId, @Param("site") String site,
                       @Param("year") int year, @Param("kind") String kind, @Param("expected") int expected);
}
