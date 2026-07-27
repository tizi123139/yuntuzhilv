package com.travel.backtravel.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis-Plus 类型处理器：将 Integer 星级 ↔ 数据库中文字段自动转换
 *
 * Java  Integer: 2/3/4/5
 * DB   VARCHAR: 二星/三星/四星/五星
 */
@MappedTypes(Integer.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StarTypeHandler extends BaseTypeHandler<Integer> {

    private static final Map<Integer, String> TO_DB = new HashMap<>();
    private static final Map<String, Integer> TO_JAVA = new HashMap<>();

    static {
        TO_DB.put(2, "二星");
        TO_DB.put(3, "三星");
        TO_DB.put(4, "四星");
        TO_DB.put(5, "五星");

        for (Map.Entry<Integer, String> entry : TO_DB.entrySet()) {
            TO_JAVA.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, TO_DB.getOrDefault(parameter, "三星"));
    }

    @Override
    public Integer getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String val = rs.getString(columnName);
        return val != null ? TO_JAVA.getOrDefault(val, 3) : null;
    }

    @Override
    public Integer getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String val = rs.getString(columnIndex);
        return val != null ? TO_JAVA.getOrDefault(val, 3) : null;
    }

    @Override
    public Integer getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String val = cs.getString(columnIndex);
        return val != null ? TO_JAVA.getOrDefault(val, 3) : null;
    }
}
