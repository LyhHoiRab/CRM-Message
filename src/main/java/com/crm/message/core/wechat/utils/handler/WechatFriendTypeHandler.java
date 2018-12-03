package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.WechatFriendType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WechatFriendTypeHandler extends BaseTypeHandler<WechatFriendType> implements TypeHandler<WechatFriendType>{

    public void setNonNullParameter(PreparedStatement ps, int i, WechatFriendType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WechatFriendType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WechatFriendType.getById(rs.getInt(columnName));
    }

    public WechatFriendType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WechatFriendType.getById(rs.getInt(columnIndex));
    }

    public WechatFriendType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WechatFriendType.getById(cs.getInt(columnIndex));
    }
}
