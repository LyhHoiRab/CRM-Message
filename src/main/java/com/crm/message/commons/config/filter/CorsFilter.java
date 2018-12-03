package com.crm.message.commons.config.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ServletComponentScan
@Order(value = 0)
@WebFilter(urlPatterns = "/*",
           filterName = "corsFilter",
           initParams = {@WebInitParam(name = "origin",      value = "*"),
                         @WebInitParam(name = "headers",     value = "*"),
                         @WebInitParam(name = "methods",     value = "*"),
                         @WebInitParam(name = "credentials", value = "true")})
public class CorsFilter implements Filter{

    private String origin;
    private String headers;
    private String methods;
    private String credentials;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
        origin      = filterConfig.getInitParameter("origin");
        headers     = filterConfig.getInitParameter("headers");
        methods     = filterConfig.getInitParameter("methods");
        credentials = filterConfig.getInitParameter("credentials");

        if(StringUtils.isBlank(origin)){
            origin = "*";
        }
        if(StringUtils.isBlank(headers)){
            headers = "*";
        }
        if(StringUtils.isBlank(methods)){
            methods = "*";
        }
        if(StringUtils.isBlank(credentials)){
            credentials = "true";
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
        HttpServletRequest request  = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //所有域名
        response.setHeader("Access-Control-Allow-Origin", origin);
        //所有请求头
        response.setHeader("Access-Control-Allow-Headers", headers);
        //所有请求类型
        response.setHeader("Access-Control-Allow-Methods", methods);
        //允许cookies
        response.setHeader("Access-Control-Allow-Credentials", credentials);

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy(){

    }
}
