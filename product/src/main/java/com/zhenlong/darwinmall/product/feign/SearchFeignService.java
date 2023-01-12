package com.zhenlong.darwinmall.product.feign;

import com.zhenlong.common.to.es.SkuEsModel;
import com.zhenlong.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    public R productStatusOnSale(@RequestBody List<SkuEsModel> skuEsModels);
}
