package com.alpha.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

// 这两个注解为使用java code进行配置
//@Configuration
//@RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)
//@RibbonClients(defaultConfiguration = RibbonConfiguration.class) //该注解用于配置Ribbon的全局配置
public class UserCenterRibbonConfiguration {
}
