package com.crm.message.core.wechat.dao;

import com.crm.api.core.wechat.consts.WechatMessageStatus;
import com.crm.api.core.wechat.consts.WechatMessageType;
import com.crm.api.core.wechat.entity.WechatFriend;
import com.crm.api.core.wechat.entity.WechatMessage;
import com.crm.message.core.wechat.dao.mapper.WechatMessageMapper;
import com.crm.message.core.wechat.utils.WechatMessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.security.request.Page;
import org.wah.doraemon.security.request.PageRequest;
import org.wah.doraemon.utils.DateUtils;
import org.wah.doraemon.utils.IDGenerator;
import org.wah.doraemon.utils.mybatis.Criteria;
import org.wah.doraemon.utils.mybatis.Restrictions;

import java.util.Date;
import java.util.List;

@Repository
public class WechatMessageDao{

    private Logger logger = LoggerFactory.getLogger(WechatMessageDao.class);

    @Autowired
    private WechatMessageMapper mapper;

    public void saveOrUpdate(WechatMessage message){
        try{
            Assert.notNull(message, "微信信息不能为空");

            if(StringUtils.isBlank(message.getId())){
                Assert.hasText(message.getWxid(), "微信信息wxid不能为空");
                Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
                Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

                message.setId(IDGenerator.uuid32());
                message.setExtract(WechatMessageUtils.extract(message));
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

    public void saveList(List<WechatMessage> messages){
        try{
            Assert.notEmpty(messages, "微信消息列表不能为空");

            final Date now = new Date();

            for(WechatMessage message : messages){
                Assert.hasText(message.getWxid(), "微信信息wxid不能为空");
                Assert.hasText(message.getWechatId(), "所属微信ID不能为空");
                Assert.notNull(message.getConversationTime(), "微信发送时间不能为空");

                message.setId(IDGenerator.uuid32());
                message.setExtract(WechatMessageUtils.extract(message));
                message.setCreateTime(now);
            }

            mapper.saveList(messages);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public WechatMessage getById(String id){
        try{
            Assert.hasText(id, "微信消息ID不能为空");

            Criteria criteria = new Criteria();
            criteria.and(Restrictions.eq("m.id", id));

            return mapper.get(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public Page<WechatMessage> page(PageRequest pageRequest, String accountId, String wechatId, String wxid,
                                    WechatMessageType type, WechatMessageStatus status, String id, Date minConversationTime,
                                    Date maxConversationTime, String content){
        try{
            Assert.notNull(pageRequest, "分页信息不能为空");

            Criteria criteria = new Criteria();
            criteria.limit(Restrictions.limit(pageRequest.getOffset(), pageRequest.getPageSize()));

            criteria.sort(Restrictions.desc("m.conversationTime"));

            if(StringUtils.isNotBlank(accountId)){
                criteria.and(Restrictions.eq("u.accountId", accountId));
            }
            if(StringUtils.isNotBlank(wechatId)){
                criteria.and(Restrictions.eq("m.wechatId", wechatId));
            }
            if(StringUtils.isNotBlank(wxid)){
                criteria.and(Restrictions.eq("m.wxid", wxid));
            }
            if(type != null){
                criteria.and(Restrictions.eq("m.type", type.getId()));
            }
            if(status != null){
                criteria.and(Restrictions.eq("m.status", status.getId()));
            }
            if(minConversationTime != null){
                minConversationTime = DateUtils.firstTimeOfDate(minConversationTime);

                criteria.and(Restrictions.ge("m.conversationTime", minConversationTime));
            }
            if(maxConversationTime != null){
                maxConversationTime = DateUtils.lastTimeOfDate(maxConversationTime);

                criteria.and(Restrictions.le("m.conversationTime", maxConversationTime));
            }
            if(StringUtils.isNotBlank(content)){
                criteria.and(Restrictions.like("m.extract", content));
            }
            if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(wxid) && StringUtils.isNotBlank(wechatId)){
                Criteria criteriaForId = new Criteria();
                criteriaForId.and(Restrictions.eq("m.id", id));

                WechatMessage message = mapper.get(criteriaForId);
                if(message != null){
                    Criteria criteriaForOffset = new Criteria();
                    criteriaForOffset.sort(Restrictions.desc("m.conversationTime"));
                    criteriaForOffset.and(Restrictions.eq("m.wxid", wxid));
                    criteriaForOffset.and(Restrictions.eq("m.wechatId", wechatId));
                    criteriaForOffset.and(Restrictions.ge("m.conversationTime", message.getConversationTime()));
                    //查询索引位置
                    Long offset = mapper.count(criteriaForOffset);

                    //重置分页信息
                    pageRequest.setOffset(offset == 0 ? 0 : offset - 1);
                    criteria.limit(Restrictions.limit(pageRequest.getOffset(), pageRequest.getPageSize()));
                }
            }

            List<WechatMessage> list = mapper.find(criteria);
            Long total = mapper.count(criteria);

            pageRequest.setLimit(pageRequest.getOffset() + list.size());

            return new Page<WechatMessage>(list, total, pageRequest);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public WechatMessage lastMessage(WechatFriend wechatFriend) {
        Assert.notNull(wechatFriend, "微信好友信息不能为空");
        String wechatId = wechatFriend.getWechatId();
        Assert.hasText(wechatId, "微信好友所属微信id不能为空");
        String wxid = wechatFriend.getWxid();
        Assert.hasText(wxid, "微信好友微信id不能为空");
        Criteria criteria = new Criteria();
        criteria.and(Restrictions.eq("m.wechatId", wechatId));
        criteria.and(Restrictions.eq("m.wxid", wxid));
        criteria.sort(Restrictions.desc("m.conversationTime"));
        List<WechatMessage> wechatMessages = mapper.find(criteria);
        if(wechatMessages != null && wechatMessages.size() > 0){
            return wechatMessages.get(0);
        }else {
            return null;
        }
    }

    public List<WechatMessage> findByWechatIdAndMsgId(String wechatId, String msgId){
        try{
            Assert.hasText(wechatId, "所属微信ID不能为空");
            Assert.hasText(msgId, "消息msgId不能为空");

            Criteria criteria = new Criteria();
            criteria.sort(Restrictions.desc("m.conversationTime"));
            criteria.and(Restrictions.eq("m.wechatId", wechatId));
            criteria.and(Restrictions.eq("m.msgId", msgId));

            return mapper.find(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public void deleteByWechatId(String wechatId){
        try{
            Assert.hasText(wechatId, "微信ID不能为空");

            Criteria criteria = new Criteria();
            criteria.and(Restrictions.eq("wechatId", wechatId));

            mapper.delete(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public Long count(WechatMessageType type, WechatMessageStatus status, Date minConversationTime,
                      Date maxConversationTime, List<String> wechatIds, Boolean isSend){
        try{
            Criteria criteria = new Criteria();

            if(type != null){
                criteria.and(Restrictions.eq("m.type", type.getId()));
            }
            if(status != null){
                criteria.and(Restrictions.eq("m.status", status.getId()));
            }
            if(minConversationTime != null){
                minConversationTime = DateUtils.firstTimeOfDate(minConversationTime);

                criteria.and(Restrictions.ge("m.conversationTime", minConversationTime));
            }
            if(maxConversationTime != null){
                maxConversationTime = DateUtils.lastTimeOfDate(maxConversationTime);

                criteria.and(Restrictions.le("m.conversationTime", maxConversationTime));
            }
            if(wechatIds != null && !wechatIds.isEmpty()){
                criteria.and(Restrictions.in("f.wechatId", wechatIds));
            }
            if(isSend != null && isSend){
                criteria.and(Restrictions.or(Restrictions.eq("m.status", WechatMessageStatus.SENDING.getId()),
                                             Restrictions.eq("m.status", WechatMessageStatus.SEND_SUCCESS.getId())));
            }

            return mapper.count(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
