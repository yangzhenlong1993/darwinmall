package com.zhenlong.darwinmall.product.feign;

import com.zhenlong.common.to.SkuReductionTo;
import com.zhenlong.common.to.SpuBoundsTo;
import com.zhenlong.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("coupon")
public interface CouponFeignService {

    /**
     * 1. CouponFeignService.saveSpuBounds(spuBoundsTo)
     *  1.1 将这个对象（spuBoundsTo）转换为json @RequestBody 注解
     *  1.2 找到coupon 服务，给/coupon/spubounds/save发送请求，将上一步转的json放在请求体的位置，发送请求
     *  1.3 对方服务收到请求， 接受请求体里的json数据 并用@RequestBody 注解将json转为对象，发送的对象名和接受的对象名是一一对应的关系，就可以转换成功
     *
     *  只要json数据模型是兼容的，双方服务无需使用同一个TO
     * @param spuBoundsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
