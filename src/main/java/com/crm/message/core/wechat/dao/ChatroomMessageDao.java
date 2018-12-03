package com.crm.message.core.wechat.dao;

import com.crm.api.core.wechat.entity.ChatroomMessage;
import com.crm.message.core.wechat.dao.mapper.ChatroomMessageMapper;
import com.crm.message.core.wechat.utils.ChatroomMessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.IDGenerator;
import org.wah.doraemon.utils.mybatis.Criteria;
import org.wah.doraemon.utils.mybatis.Restrictions;

import java.util.Date;
import java.util.List;

@Repository
public class ChatroomMessageDao{

    private Logger logger = LoggerFactory.getLogger(ChatroomMessageDao.class);

    @Autowired
    private ChatroomMessageMapper mapper;

    public void saveOrUpdate(ChatroomMessage message){
        try{
            Assert.notNull(message, "群消息不能为空");

            if(StringUtils.isBlank(message.getId())){
                Assert.hasText(message.getRoomid(), "群roomid不能为空");
                Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
                Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

                message.setId(IDGenerator.uuid32());
                message.setExtract(ChatroomMessageUtils.extract(message));
                message.setCreateTime(new Date());
                mapper.save(message);
            }else{
                message.setUpdateTime(new Date());
                mapper.update(message);
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public List<ChatroomMessage> findByWechatIdAndRoomidAndMsgId(String wechatId, String roomid, String msgId){
        try{
            Assert.hasText(wechatId, "微信ID不能为空");
            Assert.hasText(roomid, "群roomid不能为空");
            Assert.hasText(msgId, "消息msgId不能为空");

            Criteria criteria = new Criteria();
            criteria.and(Restrictions.eq("m.wechatId", wechatId));
            criteria.and(Restrictions.eq("m.roomid", roomid));
            criteria.and(Restrictions.eq("m.msgId", msgId));

            return mapper.find(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
