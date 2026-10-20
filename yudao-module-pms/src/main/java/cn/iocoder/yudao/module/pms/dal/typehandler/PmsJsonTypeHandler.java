package cn.iocoder.yudao.module.pms.dal.typehandler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Serializes PMS JSON fields as jsonb on PostgreSQL while retaining MySQL JSON support.
 */
public class PmsJsonTypeHandler extends JacksonTypeHandler {

    public PmsJsonTypeHandler(Class<?> type) {
        super(type);
    }

    public PmsJsonTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        if (!"PostgreSQL".equalsIgnoreCase(ps.getConnection().getMetaData().getDatabaseProductName())) {
            super.setNonNullParameter(ps, i, parameter, jdbcType);
            return;
        }
        PGobject jsonb = new PGobject();
        jsonb.setType("jsonb");
        jsonb.setValue(toJson(parameter));
        ps.setObject(i, jsonb);
    }
}
