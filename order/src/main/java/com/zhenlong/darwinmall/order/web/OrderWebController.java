package com.zhenlong.darwinmall.order.web;

import com.zhenlong.darwinmall.order.service.OrderService;
import com.zhenlong.darwinmall.order.vo.OrderConfirmVo;
import com.zhenlong.darwinmall.order.vo.OrderSubmitVo;
import com.zhenlong.darwinmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData", orderConfirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);


        //下单失败回到订单确认页重新确认订单信息
        System.out.println("订单提交的数据..." + vo.toString());
        if (responseVo.getCode() == 0) {
            //下单成功来到支付选项
            return "pay";
        } else {
            return "redirect:http://order.darwinmall.com/toTrade";
        }

    }
}
