package com.crm.message.core.wechat.utils.handler;

import com.crm.api.core.wechat.consts.WechatStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WechatStatusHandler extends BaseTypeHandler<WechatStatus> implements TypeHandler<WechatStatus>{

    public void setNonNullParameter(PreparedStatement ps, int i, WechatStatus parameter, JdbcType jdbcType) throws SQLException{
        ps.setInt(i, parameter.getId());
    }

    public WechatStatus getNullableResult(ResultSet rs, String columnName) throws SQLException{
        return WechatStatus.getById(rs.getInt(columnName));
    }

    public WechatStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
        return WechatStatus.getById(rs.getInt(columnIndex));
    }

    public WechatStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException{
        return WechatStatus.getById(cs.getInt(columnIndex));
    }
}
