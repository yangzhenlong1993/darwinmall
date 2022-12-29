package com.zhenlong.darwinmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 如何使用nacos作为配置中心
 * 1. 引入依赖，包括bootstrap
 * 2. 创建一个bootstrap.properties，加入spring.application.name=coupon
 *                                   spring.cloud.nacos.config.server-addr=192.168.56.10:8848
 * 3. 需要给配置中心默认添加一个叫当前应用名+properties后缀的DATA ID
 * 4. 添加配置
 * 5. 动态获取配置，使用注解@RefreshScope 动态刷新配置 @Value("${xxx}") 动态获取值 如果配置中心和当前应用的配置文件中配置了相同的项目，优先使用配置中心的配置
 *
 * 细节：
 * 命名空间：默认的命名空间是public，默认新增的所有配置都在public空间,不同配置文件之间进行隔离管理
 *         开发，测试，生产：比如新建一个开发的命名空间，只负责管理命名的配置文件
 * 配置集: 所有配置的集合
 * 配置集ID： 类似文件名
 * 配置分组：默认所有的配置集都属于Default——group
 *
 * 在此项目中，每个微服务创建自己的命名空间，使用配置分组分环境，dev，test，prod
 *
 * 3.同时加载多个配置集
 * 微服务任何配置信息，任何配置文件都可以放在配置中心中
 * 只需要在boostrap.properties中说明加载配置中心中哪些配置文件即可
 * 以前springboot任何方法从配置文件中获取值，都能使用
 * 配置中心有值以配置中心为优先
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }

}
