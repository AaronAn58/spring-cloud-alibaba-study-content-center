package com.alpha.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
//@Component
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String s) {
        String[] split = s.split("/");
        String s1 = Arrays.stream(split)
                .map(string -> {
                    if (NumberUtils.isNumber(string)) {
                        return "{number}";
                    }
                    return string;
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");

        log.info(s1);

        return null;
    }

    public static void main(String[] args) {
        MyUrlCleaner myUrlCleaner = new MyUrlCleaner();
        myUrlCleaner.clean("127.0.0.1:8010/share/1");
    }
}
