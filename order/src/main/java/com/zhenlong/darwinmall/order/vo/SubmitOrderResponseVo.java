package com.zhenlong.darwinmall.order.vo;

import com.zhenlong.darwinmall.order.entity.OrderEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//错误状态码，0为成功
}
