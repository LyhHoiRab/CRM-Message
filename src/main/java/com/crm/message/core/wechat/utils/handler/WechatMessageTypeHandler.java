package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.WechatMessageType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WechatMessageTypeHandler extends BaseTypeHandler<WechatMessageType> implements TypeHandler<WechatMessageType>{

    public void setNonNullParameter(PreparedStatement ps, int i, WechatMessageType parameter, JdbcType jdbcType) throws SQLException{
        ps.setString(i, parameter.getId());
    }

    public WechatMessageType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WechatMessageType.getById(rs.getString(columnName));
    }

    public WechatMessageType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WechatMessageType.getById(rs.getString(columnIndex));
    }

    public WechatMessageType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WechatMessageType.getById(cs.getString(columnIndex));
    }
}
