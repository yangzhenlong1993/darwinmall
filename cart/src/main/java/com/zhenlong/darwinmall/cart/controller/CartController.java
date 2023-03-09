package com.zhenlong.darwinmall.cart.controller;

import com.zhenlong.darwinmall.cart.service.CartService;
import com.zhenlong.darwinmall.cart.vo.Cart;
import com.zhenlong.darwinmall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * get current user cart items
     *
     * @return
     */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems() {
        return cartService.getUserCartItems();
    }

    /**
     * delete the selected item
     *
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.darwinmall.com/cart.html";
    }

    /**
     * change the count of the selected item
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.darwinmall.com/cart.html";
    }

    /**
     * check the item
     *
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.darwinmall.com/cart.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * add item to the cart
     * ra.addFlashAttribute()  add data into the session, but only for single use
     * ra.addAttribute()
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes ra) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        ra.addAttribute("skuId", skuId);
        return "redirect:http://cart.darwinmall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面，再次查询购物车数据即可
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }
}
