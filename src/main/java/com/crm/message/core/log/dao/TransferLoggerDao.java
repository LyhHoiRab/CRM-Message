package com.crm.message.core.log.dao;

import com.crm.api.core.log.entity.TransferLogger;
import com.crm.message.core.log.dao.mapper.TransferLoggerMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.IDGenerator;

import java.util.Date;

@Repository
public class TransferLoggerDao{

    private Logger logger = LoggerFactory.getLogger(TransferLoggerDao.class);

    @Autowired
    private TransferLoggerMapper mapper;

    public void save(TransferLogger log){
        try{
            Assert.notNull(log, "转账记录信息不能为空");
            Assert.notNull(log.getType(), "转账类型不能为空");
            Assert.hasText(log.getWechatId(), "微信ID不能为空");
            Assert.hasText(log.getWxid(), "微信好友wxid不能为空");
            Assert.hasText(log.getMessageId(), "微信聊天记录ID不能为空");

            log.setId(IDGenerator.uuid32());
            log.setCreateTime(new Date());
            mapper.save(log);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
