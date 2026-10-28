package com.damai.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @description: 通用配置
 **/

public class DaMaiCommonAutoConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustom(){
        return new JacksonCustom();
    }
}
