package com.crm.message.core.log.dao.mapper;

import com.crm.api.core.log.entity.TransferLogger;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferLoggerMapper{

    void save(TransferLogger logger);
}
