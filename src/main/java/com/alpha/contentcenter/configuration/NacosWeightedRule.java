package com.alpha.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.beans.factory.annotation.Autowired;

//public class NacosWeightedRule implements IRule {
//}
public class NacosWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;


    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件并初始化
    }

    @Override
    public Server choose(Object key) {
        // 实现基于权重的负载均衡算法。
        Instance instance = null;
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();

            // 想要请求的微服务名称
            String name = loadBalancer.getName();

            // 拿到服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // nacos client自动通过基于权重的负载均衡算法，给我们选择一个实例
            instance = namingService.selectOneHealthyInstance(name);
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }

    }
}

