package com.crm.message.core.queue.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.crm.api.core.queue.service.QueueService;
import com.crm.api.core.wechat.entity.WechatMessage;
import com.crm.message.commons.security.exception.QueueServiceException;
import com.crm.message.core.queue.facade.QueueFacade;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.wah.doraemon.utils.GsonUtils;

import java.util.List;

@Service(interfaceClass = QueueService.class)
@Component
@Transactional(readOnly = true)
public class QueueServiceImpl implements QueueService{

    @Autowired
    private QueueFacade queueFacade;

    @Override
    @Transactional
    public void saveWechatMessage(String messageString){
        try{
            Assert.hasText(messageString, "微信消息内容不能为空");

            WechatMessage message = GsonUtils.deserialize(messageString, WechatMessage.class);

            Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
            Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

            queueFacade.saveWechatMessage(message);
        }catch(Exception e){
            throw new QueueServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveWechatMessages(String messagesString){
        try{
            Assert.hasText(messagesString, "微信消息内容不能为空");

            List<WechatMessage> messages = GsonUtils.deserialize(messagesString, new TypeToken<List<WechatMessage>>(){}.getType());

            if(messages != null && !messages.isEmpty()){
                for(WechatMessage message : messages){
                    Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
                    Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

                    queueFacade.saveWechatMessage(message);
                }
            }
        }catch(Exception e){
            throw new QueueServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveChatroomMessage(String messageString){
        try{
            Assert.hasText(messageString, "微信消息内容不能为空");

            WechatMessage message = GsonUtils.deserialize(messageString, WechatMessage.class);

            queueFacade.saveChatroomMessage(message);
        }catch(Exception e){
            throw new QueueServiceException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveChatroomMessages(String messagesString){
        try{
            List<WechatMessage> messages = GsonUtils.deserialize(messagesString, new TypeToken<List<WechatMessage>>(){}.getType());

            if(messages != null && !messages.isEmpty()){
                for(WechatMessage message : messages){
                    Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
                    Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

                    queueFacade.saveChatroomMessage(message);
                }
            }
        }catch(Exception e){
            throw new QueueServiceException(e.getMessage(), e);
        }
    }
}
