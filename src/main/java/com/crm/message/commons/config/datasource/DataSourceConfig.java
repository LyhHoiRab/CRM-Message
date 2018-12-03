package com.crm.message.commons.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:dataSource.properties")
@Profile("development")
public class DataSourceConfig{

    @Value("${jdbc.driverClassName}")
    private String driverClassName;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Value("${druid.defaultAutoCommit}")
    private Boolean defaultAutoCommit;

    @Bean
    public DataSource dataSource(){
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();

        //配置
        datasource.setDriverClassName(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDefaultAutoCommit(defaultAutoCommit);

        return datasource;
    }
}
