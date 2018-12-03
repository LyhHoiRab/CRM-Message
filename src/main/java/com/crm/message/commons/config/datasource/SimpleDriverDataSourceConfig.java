package com.crm.message.commons.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@PropertySource("classpath:dataSource.properties")
@Profile("test")
public class SimpleDriverDataSourceConfig{

    @Value("${jdbc.driverClassName}")
    private Class<? extends Driver> driverClassName;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource datasource = new SimpleDriverDataSource();

        //配置
        datasource.setDriverClass(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        return datasource;
    }
}
