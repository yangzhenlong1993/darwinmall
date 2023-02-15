package com.zhenlong.darwinmall.order.to;

import com.zhenlong.darwinmall.order.entity.OrderEntity;
import com.zhenlong.darwinmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;//订单计算的应付价格
    private BigDecimal fare;//运费
}
