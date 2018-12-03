package com.crm.message.core.group.utils.handler;

import com.crm.api.core.group.consts.AllocationType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllocationTypeHandler extends BaseTypeHandler<AllocationType> implements TypeHandler<AllocationType>{

    public void setNonNullParameter(PreparedStatement ps, int i, AllocationType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public AllocationType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return AllocationType.getById(rs.getInt(columnName));
    }

    public AllocationType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return AllocationType.getById(rs.getInt(columnIndex));
    }

    public AllocationType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return AllocationType.getById(cs.getInt(columnIndex));
    }
}
