package com.zhenlong.darwinmall.cart.feign;

import com.zhenlong.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("product")
public interface ProductFeignService {
    /**
     * remotely get sku info
     *
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * remotely get sku sale attribute values
     *
     * @param skuId
     * @return
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    /**
     * remotely get latest price info
     *
     * @param skuId
     * @return
     */
    @GetMapping("/product/skuinfo/{skuId}/price")
    BigDecimal getLatestPrice(@PathVariable("skuId") Long skuId);
}
