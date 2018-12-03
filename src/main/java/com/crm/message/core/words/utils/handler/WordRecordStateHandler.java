package com.crm.message.core.words.utils.handler;

import com.crm.api.core.words.consts.WordRecordState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WordRecordStateHandler extends BaseTypeHandler<WordRecordState> implements TypeHandler<WordRecordState>{

    public void setNonNullParameter(PreparedStatement ps, int i, WordRecordState parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WordRecordState getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WordRecordState.getById(rs.getInt(columnName));
    }

    public WordRecordState getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WordRecordState.getById(rs.getInt(columnIndex));
    }

    public WordRecordState getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WordRecordState.getById(cs.getInt(columnIndex));
    }
}
