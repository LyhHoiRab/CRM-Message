package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.WechatMessageStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WechatMessageStatusHandler extends BaseTypeHandler<WechatMessageStatus> implements TypeHandler<WechatMessageStatus>{

    public void setNonNullParameter(PreparedStatement ps, int i, WechatMessageStatus parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WechatMessageStatus getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WechatMessageStatus.getById(rs.getInt(columnName));
    }

    public WechatMessageStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WechatMessageStatus.getById(rs.getInt(columnIndex));
    }

    public WechatMessageStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WechatMessageStatus.getById(cs.getInt(columnIndex));
    }
}
