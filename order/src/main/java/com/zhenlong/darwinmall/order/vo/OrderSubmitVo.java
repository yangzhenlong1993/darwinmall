package com.zhenlong.darwinmall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 */
@Data
@ToString
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    //无需提交需要购买的商品，去购物车cart服务再获取一遍
    //TODO 优惠，发票

    private String orderToken;
    private BigDecimal payPrice;
    private String note;

    //用户相关信息都在cookie里，并可以从redis中取得session信息
}
