package com.crm.message.core.wechat.dao.mapper;

import com.crm.api.core.wechat.entity.WechatMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.utils.mybatis.Criteria;

import java.util.List;

@Repository
public interface WechatMessageMapper{

    void save(WechatMessage message);

    void saveList(@Param("messages") List<WechatMessage> messages);

    void update(WechatMessage message);

    WechatMessage get(@Param("params") Criteria criteria);

    List<WechatMessage> find(@Param("params") Criteria criteria);

    Long count(@Param("params") Criteria criteria);

    void delete(@Param("params") Criteria criteria);
}
