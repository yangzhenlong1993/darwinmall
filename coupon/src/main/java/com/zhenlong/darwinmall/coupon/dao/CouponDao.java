package com.zhenlong.darwinmall.coupon.dao;

import com.zhenlong.darwinmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:18:12
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
