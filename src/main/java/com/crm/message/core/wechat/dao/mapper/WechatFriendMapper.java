package com.crm.message.core.wechat.dao.mapper;

import com.crm.api.core.wechat.entity.WechatFriend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.utils.mybatis.Criteria;

@Repository
public interface WechatFriendMapper{

    void update(WechatFriend friend);

    WechatFriend get(@Param("params") Criteria criteria);
}
