package com.zhenlong.darwinmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.cart.feign.ProductFeignService;
import com.zhenlong.darwinmall.cart.interceptor.CartInterceptor;
import com.zhenlong.darwinmall.cart.service.CartService;
import com.zhenlong.darwinmall.cart.vo.CartItem;
import com.zhenlong.darwinmall.cart.vo.SkuInfoVo;
import com.zhenlong.darwinmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "darwinmall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = new CartItem();
        //远程查询当前要添加的商品信息
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            R r = productFeignService.getSkuInfo(skuId);
            SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            //商品添加到购物车
            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(skuInfo.getPrice());
        }, executor);

        //远程查询sku的组合信息
        CompletableFuture<Void> getSkuSaleAttrValuesTask = CompletableFuture.runAsync(() -> {
            List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(values);
        }, executor);

        CompletableFuture.allOf(skuInfoTask, getSkuSaleAttrValuesTask).get();

        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
        return cartItem;
    }

    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //1.判断是否登录
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
