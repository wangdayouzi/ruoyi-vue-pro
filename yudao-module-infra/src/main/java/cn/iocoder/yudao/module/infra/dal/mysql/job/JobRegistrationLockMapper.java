package cn.iocoder.yudao.module.infra.dal.mysql.job;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.*;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface JobRegistrationLockMapper {
    @Select("SELECT pg_advisory_xact_lock(hashtextextended(#{handlerName},0))::text")
    String lockHandler(@Param("handlerName") String handlerName);
}
