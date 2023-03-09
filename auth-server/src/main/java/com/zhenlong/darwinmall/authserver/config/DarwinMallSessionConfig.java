package com.zhenlong.darwinmall.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class DarwinMallSessionConfig {

    /**
     * create a bean for serialize the cookie
     *
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("darwinmall.com");
        cookieSerializer.setCookieName("DARWIN_MALL_SESSION");

        return cookieSerializer;
    }

    /**
     * create a bean for serialize the redis data
     *
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
