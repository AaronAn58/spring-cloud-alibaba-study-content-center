package com.alpha.contentcenter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alpha.contentcenter.feignclient.TestBaiduFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Service;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//用于配置动态刷新
@RefreshScope
public class TestController {

    private final DiscoveryClient discoveryClient;

    @GetMapping("/test2")
    public List<ServiceInstance> getInstances() {
        return this.discoveryClient.getInstances("user-center");
    }

    private final TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("/test3")
    public String baiduIndex() {
        return this.testBaiduFeignClient.index();
    }

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(
            @RequestParam(required = false) String a
    ) {
        // 用来指定资源名称
        String resourceName = "test-sentinel-api";
        // 第一个参数是资源名称，第二个参数是来源
        ContextUtil.enter(resourceName, "test-wfw");
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            if (StringUtil.isBlank(a)) {
                throw new IllegalArgumentException("参数a 不能为空");
            }
            return a;
        } catch (IllegalArgumentException e2) {
            // 对想要统计的异常进行统计
            Tracer.trace(e2);
            return "IllegalArgumentException";
        } catch (Exception e) {
            log.warn("被限流或者降级了");
            return "服务被限流";
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @Value("${your.configuration}")
    private String yourConfiguration;

    @GetMapping("test-config")
    public String testConfig() {
        return this.yourConfiguration;
    }


//    @GetMapping("/test-stream")
//    public String testStream() {
//        this.source.output().send(
//                MessageBuilder.withPayload("消息体")
//                        .build()
//        );
//        return null;
//    }

}
