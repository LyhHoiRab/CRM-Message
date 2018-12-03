package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.PurposeType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PurposeTypeHandler extends BaseTypeHandler<PurposeType> implements TypeHandler<PurposeType>{

    public void setNonNullParameter(PreparedStatement ps, int i, PurposeType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public PurposeType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return PurposeType.getById(rs.getInt(columnName));
    }

    public PurposeType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return PurposeType.getById(rs.getInt(columnIndex));
    }

    public PurposeType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return PurposeType.getById(cs.getInt(columnIndex));
    }
}
