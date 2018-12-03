package com.crm.message.core.wechat.dao;

import com.crm.api.core.wechat.entity.Wechat;
import com.crm.message.core.wechat.dao.mapper.WechatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.mybatis.Criteria;
import org.wah.doraemon.utils.mybatis.Restrictions;

@Repository
public class WechatDao{

    private Logger logger = LoggerFactory.getLogger(WechatDao.class);

    @Autowired
    private WechatMapper mapper;

    public Wechat getById(String id){
        try{
            Assert.hasText(id, "微信ID不能为空");

            Criteria criteria = new Criteria();
            criteria.and(Restrictions.eq("id", id));

            return mapper.get(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
