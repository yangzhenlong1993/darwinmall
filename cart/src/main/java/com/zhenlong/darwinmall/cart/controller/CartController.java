package com.zhenlong.darwinmall.cart.controller;

import com.zhenlong.darwinmall.cart.interceptor.CartInterceptor;
import com.zhenlong.darwinmall.cart.service.CartService;
import com.zhenlong.darwinmall.cart.vo.CartItem;
import com.zhenlong.darwinmall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        model.addAttribute("item", cartItem);
        return "success";
    }
}