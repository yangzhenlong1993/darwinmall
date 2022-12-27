package com.zhenlong.darwinmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhenlong.darwinmall.product.entity.BrandEntity;
import com.zhenlong.darwinmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript("This is phone brand");
//        brandEntity.setName("Iphone");
//        brandService.save(brandEntity);
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("fixed brand");
//        brandService.updateById(brandEntity);

        List<BrandEntity> brandId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        brandId.forEach(System.out::println);
        System.out.println("update successfully");
    }

}
