package com.alpha.contentcenter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TestRestTemplateTOkenRelayInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        // 1. 获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("X-Token");

        //
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("X-Token", token);
        // 责任链模式，如果不止一个拦截器，运用这行代码，就可以执行后面的拦截器
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
