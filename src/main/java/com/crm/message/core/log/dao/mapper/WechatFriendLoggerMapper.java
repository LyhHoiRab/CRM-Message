package com.crm.message.core.log.dao.mapper;

import com.crm.api.core.log.entity.WechatFriendLogger;
import org.springframework.stereotype.Repository;

@Repository
public interface WechatFriendLoggerMapper{

    void save(WechatFriendLogger logger);
}
