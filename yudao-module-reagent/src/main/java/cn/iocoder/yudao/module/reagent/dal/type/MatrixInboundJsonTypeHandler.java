package cn.iocoder.yudao.module.reagent.dal.type;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;
import java.util.List;

/** PostgreSQL JSONB 人员编号数组；以 OTHER 绑定，避免将 VARCHAR 直接写入 JSONB 列。 */
public class MatrixInboundJsonTypeHandler extends BaseTypeHandler<List<Long>> {
    @Override
    public void setNonNullParameter(PreparedStatement statement, int index, List<Long> value, JdbcType jdbcType) throws SQLException {
        statement.setObject(index, JsonUtils.toJsonString(value), Types.OTHER);
    }
    @Override
    public List<Long> getNullableResult(ResultSet result, String column) throws SQLException {
        return parse(result.getString(column));
    }
    @Override
    public List<Long> getNullableResult(ResultSet result, int column) throws SQLException {
        return parse(result.getString(column));
    }
    @Override
    public List<Long> getNullableResult(CallableStatement statement, int column) throws SQLException {
        return parse(statement.getString(column));
    }
    private List<Long> parse(String value) {
        return value == null ? List.of() : JsonUtils.parseArray(value, Long.class);
    }
}
