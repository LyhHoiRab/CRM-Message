package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.AddFromType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddFromTypeHandler extends BaseTypeHandler<AddFromType> implements TypeHandler<AddFromType>{

    public void setNonNullParameter(PreparedStatement ps, int i, AddFromType parameter, JdbcType jdbcType) throws SQLException{
        ps.setString(i, parameter.getId());
    }

    public AddFromType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return AddFromType.getById(rs.getString(columnName));
    }

    public AddFromType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return AddFromType.getById(rs.getString(columnIndex));
    }

    public AddFromType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return AddFromType.getById(cs.getString(columnIndex));
    }
}
