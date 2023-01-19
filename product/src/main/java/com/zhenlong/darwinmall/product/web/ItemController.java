package com.zhenlong.darwinmall.product.web;

import com.zhenlong.darwinmall.product.service.SkuInfoService;
import com.zhenlong.darwinmall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("当前要查询"+skuId+"商品");
       SkuItemVo skuItemVo= skuInfoService.item(skuId);
       model.addAttribute("item",skuItemVo);
        return "item";
    }
}
