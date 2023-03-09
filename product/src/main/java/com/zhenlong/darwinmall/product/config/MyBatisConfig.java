package com.zhenlong.darwinmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * config mybatis pagination interceptor
 */
@Configuration
@EnableTransactionManagement//enable transaction management
@MapperScan("com.zhenlong.darwinmall.product.dao")
public class MyBatisConfig {
    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setOverflow(true);
        //set the limitation for each page
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
