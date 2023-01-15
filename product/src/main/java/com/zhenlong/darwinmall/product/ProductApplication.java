package com.zhenlong.darwinmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1. integrate MyBatis Plus
 *  1) import dependencies
 *  2) configure MyBatis Plus
 *      a. configure data source: mysql driver; configure data source info in yml file
 *      b. configure mybatis-plus: @MapperScan; sql Mapping location
 *
 * 2.逻辑删除
 *  1) 在application.yml中配置全局逻辑删除规则
 *
 * 3. JSR303校验
 *  1）给bean添加校验注解:参考javax.validation.constraints并定义属性message
 *  2) 然后去需要校验的参数上加入@Valid注解
 *  3) 给校验的bean后紧跟一个BindingResult，就可以获取校验结果
 *  4) 分组校验功能
 *      1. 	@NotBlank(message = "brand name cannot be empty", groups = {AddGroup.class, UpdateGroup.class})
 *      给校验注解标注什么情况需要进行校验
 *      2. 在brandController中的方法参数上加上@Validated注解并指定class类型
 *      3. 默认没有指定分组的校验注解，在分组校验的情况下不生效，只有在不分组校验生效，即使用普通@Valid注解
 *      4.自定义校验
 *          1. 自己编写一个自定义的校验注解
 *          2. 编写一个自定义的校验器, 实现ConstraintValidator接口
 *          3. 关联自定义的校验器和自定义的校验注解
 *
 * 4. 统一的异常处理
 * @ControllerAdvice
 *  1) 编写异常处理类，使用@RestControllerAdvice
 *  2） 使用@ExceptionHandler标注方法可以处理的异常
 *
 * 5. Redis整合
 * 1）引入data-redis-starter
 * 2）简单配置redis的host信息
 * 3）使用springboot自动配置好的StringRedisTemplate来操作redis
 *
 * 6. 整合springcache简化缓存开发
 *  1. 引入依赖，spring boot starter cache 和 spring boot starter data redis
 *  2，写配置
 *      1. 自动配置 CacheAutoConfiguration会导入redisCacheConfiguration
 *      自动配好了RedisCacheManager
 *      2.配置使用redis作为缓存
 *      spring.cache.type=redis
 *      3. 测试使用缓存
 *          1. 开启缓存 @EnableCaching
 */

@EnableFeignClients(basePackages = "com.zhenlong.darwinmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.zhenlong.darwinmall.product.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
