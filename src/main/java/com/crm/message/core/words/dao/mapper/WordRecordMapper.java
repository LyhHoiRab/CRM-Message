package com.crm.message.core.words.dao.mapper;

import com.crm.api.core.words.entity.WordRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRecordMapper{

    void save(WordRecord record);
}
