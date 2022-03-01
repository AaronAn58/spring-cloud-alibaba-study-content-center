package com.alpha.contentcenter.auth;

import com.alpha.contentcenter.util.JwtOperator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(com.alpha.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            // 1. 获取请求
            checkToken();

            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new SecurityException("token不合法");
        }

    }

    private void checkToken() {
        HttpServletRequest request = getHttpServletRequest();

        String token = request.getHeader("X-Token");

        // 2. 校验token是否过期
        Boolean isValid = this.jwtOperator.validateToken(token);
        log.info("token status: {}", isValid);
        if (!isValid) {
            throw new SecurityException("token不合法");
        }

        request.setAttribute("id", "1");
        request.setAttribute("role", "admin");
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;

        HttpServletRequest request = attributes.getRequest();
        return request;
    }

    @Around("@annotation(com.alpha.contentcenter.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            // 1. 验证token是否合法
            this.checkToken();
            HttpServletRequest request = getHttpServletRequest();

            String role = (String) request.getAttribute("role");
            log.info("role:{}", role);
            // 2. 验证角色是否匹配

            // 可以在此行打断点，看实际获取到的类型
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);
            String authValue = annotation.value();
            log.info("authRole:{}", authValue);

            if (!Objects.equals(role, authValue)) {
                throw new SecurityException("用户无权访问");
            }
            return proceedingJoinPoint.proceed();
        }catch (IllegalArgumentException e){
            log.warn("这个不是权限校验问题");
            throw new IllegalArgumentException("这个不是权限校验问题");
        } catch (Throwable e) {
            throw new SecurityException("用户无权访问", e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static
    class ErrorCode {

        private String code;
        private String msg;
    }
}
