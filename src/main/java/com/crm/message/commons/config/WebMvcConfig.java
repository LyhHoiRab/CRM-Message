package com.crm.message.commons.config;

import com.crm.message.commons.config.converter.JsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{

    @Override
    public void addInterceptors(InterceptorRegistry registry){

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
        //自定义converter
        JsonHttpMessageConverter jsonConverter = new JsonHttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML));

        //String converter
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));

        converters.add(jsonConverter);
        converters.add(stringConverter);
    }
}
