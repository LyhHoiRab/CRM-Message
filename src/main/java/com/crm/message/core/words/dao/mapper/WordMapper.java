package com.crm.message.core.words.dao.mapper;

import com.crm.api.core.words.entity.Word;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.wah.doraemon.utils.mybatis.Criteria;

import java.util.List;

@Repository
public interface WordMapper{

    List<Word> find(@Param("params") Criteria criteria);
}
