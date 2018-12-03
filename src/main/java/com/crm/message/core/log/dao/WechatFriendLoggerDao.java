package com.crm.message.core.log.dao;

import com.crm.api.core.log.entity.WechatFriendLogger;
import com.crm.message.core.log.dao.mapper.WechatFriendLoggerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.IDGenerator;

import java.util.Date;

@Repository
public class WechatFriendLoggerDao{

    private Logger logger = LoggerFactory.getLogger(WechatFriendLoggerDao.class);

    @Autowired
    private WechatFriendLoggerMapper mapper;

    public void save(WechatFriendLogger log){
        try{
            Assert.notNull(log, "微信好友记录信息不能为空");
            Assert.hasText(log.getWechatId(), "微信ID不能为空");
            Assert.notNull(log.getType(), "微信好友记录类型不能为空");

            log.setId(IDGenerator.uuid32());
            log.setCreateTime(new Date());
            mapper.save(log);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
