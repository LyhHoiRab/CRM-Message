package com.crm.message.commons.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@PropertySource("classpath:dataSource.properties")
@Profile("production")
public class DruidDataSourceConfig{

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
    @Value("${druid.maxActive}")
    private Integer maxActive;
    @Value("${druid.initialSize}")
    private Integer initialSize;
    @Value("${druid.maxWait}")
    private Integer maxWait;
    @Value("${druid.minIdle}")
    private Integer minIdle;
    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private Integer timeBetweenEvictionRunsMillis;
    @Value("${druid.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${druid.poolPreparedStatements}")
    private Boolean poolPreparedStatements;
    @Value("${druid.maxOpenPreparedStatements}")
    private Integer maxOpenPreparedStatements;
    @Value("${druid.connectionInitSqls}")
    private String connectionInitSqls;

    @Bean
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();

        //配置
        datasource.setDriverClassName(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        datasource.setDefaultAutoCommit(defaultAutoCommit);
        datasource.setMaxActive(maxActive);
        datasource.setInitialSize(initialSize);
        datasource.setMaxWait(maxWait);
        datasource.setMinIdle(minIdle);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        datasource.setConnectionInitSqls(Arrays.asList(connectionInitSqls));

        return datasource;
    }
}
