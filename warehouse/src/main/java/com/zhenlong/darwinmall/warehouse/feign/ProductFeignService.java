package com.zhenlong.darwinmall.warehouse.feign;

import com.zhenlong.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product")
public interface ProductFeignService {

    /**
     * 可以有两种请求路径 /product/skuinfo/info/{skuId}
     * api/product/skuinfo/info/{skuId} 如果写成这种形式，可以让请求过网关，@FeignClient的参数就必须是网关的名字
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
