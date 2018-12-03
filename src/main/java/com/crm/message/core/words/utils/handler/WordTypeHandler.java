package com.crm.message.core.words.utils.handler;

import com.crm.api.core.words.consts.WordType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WordTypeHandler extends BaseTypeHandler<WordType> implements TypeHandler<WordType>{

    public void setNonNullParameter(PreparedStatement ps, int i, WordType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WordType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WordType.getById(rs.getInt(columnName));
    }

    public WordType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WordType.getById(rs.getInt(columnIndex));
    }

    public WordType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WordType.getById(cs.getInt(columnIndex));
    }
}
