package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.WechatFriendExpandType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WechatFriendExpandTypeHandler extends BaseTypeHandler<WechatFriendExpandType> implements TypeHandler<WechatFriendExpandType>{

    public void setNonNullParameter(PreparedStatement ps, int i, WechatFriendExpandType parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WechatFriendExpandType getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WechatFriendExpandType.getById(rs.getInt(columnName));
    }

    public WechatFriendExpandType getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WechatFriendExpandType.getById(rs.getInt(columnIndex));
    }

    public WechatFriendExpandType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WechatFriendExpandType.getById(cs.getInt(columnIndex));
    }
}
