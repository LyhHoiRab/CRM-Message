package com.crm.message;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan(basePackages = "com.crm.message.core.**.dao")
public class CrmMessageApplication extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(CrmMessageApplication.class);
    }

    public static void main(String[] args){
        SpringApplication.run(CrmMessageApplication.class,  args);
    }
}
