package ribbonconfiguration;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 为什么需要另起一个包，RibbonConfiguration不能被启动类扫描出来，Ribbon和spring-boot启动类为两个context
// 其中，spring-boot为主上下文，Ribbon为子上下文，父上下文和子上下文同时被扫描会导致事务不生效
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // 在此处配置所需要的负载均衡算法
        return new RandomRule();
    }

    @Bean
    // 自定义配置项
    public IPing ping() {
        return new PingUrl();
    }
}
