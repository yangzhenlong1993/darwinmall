package com.zhenlong.darwinmall.search.controller;

import com.zhenlong.common.exception.BizCodeEnum;
import com.zhenlong.common.to.es.SkuEsModel;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusOnSale(@RequestBody List<SkuEsModel> skuEsModels) {
       boolean b = false;
        try{
           b =  productSaveService.productStatusOnSale(skuEsModels);
        }catch (Exception e){
            log.error("elasticsearch商品上架错误 {}", e);
            return R.error(BizCodeEnum.PRODUCT_PUT_ON_SALE_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_PUT_ON_SALE_EXCEPTION.getMsg());
        }

        if(!b){
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_PUT_ON_SALE_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_PUT_ON_SALE_EXCEPTION.getMsg());
        }

    }
}
