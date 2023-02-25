package com.zhenlong.darwinmall.order;


import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 引入amqp场景 starter
 * 给容器中自动配置了RabbitTemplate, AmqpAdmin, CachingConnectionFactory, RabbitMessagingTemplate
 *
 * @EnableRabbit 给配置文件中配置spring.rabbit。。。
 * 监听消息，使用@RabbitListner
 *
 * 本地事务失效问题
 * 同一对象内事务方法互调默认失效，原因是绕过了代理对象，事务使用代理对象来控制的
 * 解决：使用代理对象来调用事务方法
 *  1.引入aop-starter，引入了aspectj
 *  2. 开启aspectj动态代理，开启aspectj动态代理功能，以后所有的动态代理都是aspectj创建的，即使没有接口也可以创建动态代理
 *  3. 对外暴露代理对象
 *  (exposeProxy = true)
 *  4.用代理对象本类互调 OrderServiceImpl serviceProxy = (OrderServiceImpl) AopContext.currentProxy();
 *
 *  Seata控制分布式事务
 *  1. 每一个微服务先必须创建undo_log
 *  2. 安装事务协调器，seata-server
 *  3. 整合
 *      1. 导入依赖spring-cloud-starter-alibaba-seata
 *      2. docker 部署，记得挂载自定义配置文件的位置
 *      3. 所有想要用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
 *      4, 每个微服务，都必须导入file.conf和registry.conf
 */
@EnableAspectJAutoProxy(exposeProxy = true)
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
