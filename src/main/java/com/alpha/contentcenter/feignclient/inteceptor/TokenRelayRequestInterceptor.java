package com.alpha.contentcenter.feignclient.inteceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class TokenRelayRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1. 获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("X-Token");
        // 2. 将token传递
        if (!StringUtils.isBlank(token)) {
            log.info("Token:{}", token);
            requestTemplate.header("X-Token", token);
        }
        log.warn("Token is blank");
    }
}
