package com.alpha.contentcenter.auth;

import com.alpha.contentcenter.util.JwtOperator;
import lombok.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(com.alpha.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            // 1. 获取请求
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;

            HttpServletRequest request = attributes.getRequest();

            String token = request.getHeader("X-Token");

            // 2. 校验token是否过期
            Boolean isValid = this.jwtOperator.validateToken(token);

            if (!isValid) {
                throw new SecurityException("token不合法");
            }

            request.setAttribute("id", "1");

            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new SecurityException("token不合法");
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
