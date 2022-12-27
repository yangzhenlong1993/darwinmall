package com.zhenlong.darwinmall.order.dao;

import com.zhenlong.darwinmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:59:45
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
