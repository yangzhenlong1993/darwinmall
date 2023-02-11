package com.zhenlong.darwinmall.order.vo;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要用到的数据
 */

public class OrderConfirmVo {
    //收货地址, ums_member_receive_address表
    @Setter
    @Getter
    List<MemberAddressVo> address;
    //所有选中的购物项
    @Setter
    @Getter
    List<OrderItemVo> items;
    //TODO 发票信息...

    //优惠券信息....
    @Setter
    @Getter
    Integer integration;
    //防重令牌
    @Getter
    @Setter
    String orderToken;
    BigDecimal total;//订单总额

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    BigDecimal payPrice;//应付价格

    public BigDecimal getPayPrice() {
        //TODO 减去优惠信息
        return getTotal();
    }
}
