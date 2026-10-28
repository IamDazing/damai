package com.damai.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @description: 过滤器配置
 **/
public class FilterConfig {

    @Bean
    public OncePerRequestFilter requestParamContextFilter(){
        return new RequestParamContextFilter();
    }
}
