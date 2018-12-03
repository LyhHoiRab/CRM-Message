package com.crm.message.core.wechat.dao.mapper;

import com.crm.api.core.wechat.entity.ChatroomMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.utils.mybatis.Criteria;

import java.util.List;

@Repository
public interface ChatroomMessageMapper{

    void save(ChatroomMessage message);

    void update(ChatroomMessage message);

    List<ChatroomMessage> find(@Param("params") Criteria criteria);
}
