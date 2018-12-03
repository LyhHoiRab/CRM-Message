package com.crm.message.core.group.utils.handler;

import com.crm.api.core.group.consts.GroupType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupTypeHandler extends BaseTypeHandler<GroupType> implements TypeHandler<GroupType>{

    public void setNonNullParameter(PreparedStatement ps, int i, GroupType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public GroupType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return GroupType.getById(rs.getInt(columnName));
    }

    public GroupType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return GroupType.getById(rs.getInt(columnIndex));
    }

    public GroupType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return GroupType.getById(cs.getInt(columnIndex));
    }
}
