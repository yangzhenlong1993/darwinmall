package com.zhenlong.darwinmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class DarwinMallFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //使用contextHolder拿到当前调用线程的上下文
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();//feign请求丢失请求头的请求
                    if (request != null) {
                        //同步请求头数据，主要是cookie信息
                        String cookie = request.getHeader("Cookie");
                        //给新的feign请求同步了老请求的cookie
                        template.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
