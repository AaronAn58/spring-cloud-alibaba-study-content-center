package com.alpha.contentcenter.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionErrorHandle {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorCode> error(SecurityException e) {
        log.warn("token不合法", e);
        return new ResponseEntity<>(
                ErrorCode.builder()
                        .msg("token不合法")
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .build(), HttpStatus.UNAUTHORIZED
        );
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static
    class ErrorCode {
        private Integer code;

        private String msg;
    }
}
