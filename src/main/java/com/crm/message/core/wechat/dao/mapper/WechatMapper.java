package com.crm.message.core.wechat.dao.mapper;

import com.crm.api.core.wechat.entity.Wechat;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.utils.mybatis.Criteria;

@Repository
public interface WechatMapper{

    Wechat get(@Param("params") Criteria criteria);
}
