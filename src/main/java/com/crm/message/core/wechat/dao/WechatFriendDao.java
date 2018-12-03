package com.crm.message.core.wechat.dao;

import com.crm.api.core.wechat.entity.WechatFriend;
import com.crm.message.core.wechat.dao.mapper.WechatFriendMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.mybatis.Criteria;
import org.wah.doraemon.utils.mybatis.Restrictions;

import java.util.Date;

@Repository
public class WechatFriendDao{

    private Logger logger = LoggerFactory.getLogger(WechatFriendDao.class);

    @Autowired
    private WechatFriendMapper mapper;

    public void update(WechatFriend friend){
        try{
            Assert.notNull(friend, "微信好友信息不能为空");
            Assert.hasText(friend.getId(), "微信好友ID不能为空");

            friend.setUpdateTime(new Date());
            mapper.update(friend);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public WechatFriend getByWechatIdAndWxid(String wechatId, String wxid){
        try{
            Assert.hasText(wechatId, "所属微信ID不能为空");
            Assert.hasText(wxid, "微信好友wxid不能为空");

            Criteria criteria = new Criteria();
            criteria.and(Restrictions.eq("wechatId", wechatId));
            criteria.and(Restrictions.eq("wxid", wxid));

            return mapper.get(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
