package com.crm.message.core.words.dao;

import com.crm.api.core.words.consts.WordRecordState;
import com.crm.api.core.words.entity.WordRecord;
import com.crm.message.core.words.dao.mapper.WordRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.IDGenerator;

import java.util.Date;

@Repository
public class WordRecordDao{

    private Logger logger = LoggerFactory.getLogger(WordRecordDao.class);

    @Autowired
    private WordRecordMapper mapper;

    public void save(WordRecord record){
        try{
            Assert.notNull(record, "敏感词记录信息不能为空");
            Assert.hasText(record.getMessageId(), "敏感词聊天记录ID不能为空");
            Assert.hasText(record.getWords(), "敏感词触发内容不能为空");

            record.setId(IDGenerator.uuid32());
            record.setState(WordRecordState.UNHANDLE);
            record.setCreateTime(new Date());
            mapper.save(record);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
