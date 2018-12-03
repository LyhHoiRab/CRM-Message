package com.crm.message.core.words.dao;

import com.crm.api.core.words.consts.WordType;
import com.crm.api.core.words.entity.Word;
import com.crm.message.core.words.dao.mapper.WordMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.security.exception.DataAccessException;
import org.wah.doraemon.utils.mybatis.Criteria;
import org.wah.doraemon.utils.mybatis.Restrictions;

import java.util.List;

@Repository
public class WordDao{

    private Logger logger = LoggerFactory.getLogger(WordDao.class);

    @Autowired
    private WordMapper mapper;

    public List<Word> find(String companyId, String wechatGroupId, String wechatId, WordType type, Boolean global){
        try{
            Criteria criteria = new Criteria();

            if(StringUtils.isNotBlank(companyId)){
                criteria.and(Restrictions.eq("w.companyId", companyId));
            }
            if(StringUtils.isNotBlank(wechatGroupId)){
                criteria.and(Restrictions.eq("wg.wechatGroupId", wechatGroupId));
            }
            if(StringUtils.isNotBlank(wechatId)){
                criteria.and(Restrictions.eq("wx.id", wechatId));
            }
            if(type != null){
                criteria.and(Restrictions.eq("w.type", type.getId()));
            }
            if(global != null){
                criteria.and(Restrictions.eq("w.global", global));
            }

            return mapper.find(criteria);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new DataAccessException(e.getMessage(), e);
        }
    }
}
