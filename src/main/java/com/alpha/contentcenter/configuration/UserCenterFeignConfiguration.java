package com.alpha.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

//使用java code方式定义feign的日志级别
public class UserCenterFeignConfiguration {

    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
