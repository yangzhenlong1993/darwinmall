package com.zhenlong.darwinmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 这是mybatis的配置类
 */
@Configuration
@EnableTransactionManagement//开启事务管理
@MapperScan("com.zhenlong.darwinmall.product.dao")
public class MyBatisConfig {
    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //设置请求的页面大于最大页后操作，true跳回到首页，false继续请求
        paginationInterceptor.setOverflow(true);
        //设置最大单页限制数量，默认500条,-1不受限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
