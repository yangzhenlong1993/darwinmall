package com.zhenlong.darwinmall.product.feign;

import com.zhenlong.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("warehouse")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds);
}
