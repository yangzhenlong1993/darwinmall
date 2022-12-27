package com.zhenlong.darwinmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1. integrate MyBatis Plus
 *  1) import dependencies
 *  2) configure MyBatis Plus
 *      a. configure data source: mysql driver; configure data source info in yml file
 *      b. configure mybatis-plus: @MapperScan; sql Mapping location
 *
 */

@MapperScan("com.zhenlong.darwinmall.product.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}