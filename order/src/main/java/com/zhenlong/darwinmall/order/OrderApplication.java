package com.zhenlong.darwinmall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 引入amqp场景 starter
 * 给容器中自动配置了RabbitTemplate, AmqpAdmin, CachingConnectionFactory, RabbitMessagingTemplate
 *
 * @EnableRabbit 给配置文件中配置spring.rabbit。。。
 * 监听消息，使用@RabbitListner
 */
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
