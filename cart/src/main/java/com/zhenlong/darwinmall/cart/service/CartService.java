package com.zhenlong.darwinmall.cart.service;

import com.zhenlong.darwinmall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
