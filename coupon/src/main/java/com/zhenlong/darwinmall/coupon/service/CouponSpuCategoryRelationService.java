package com.zhenlong.darwinmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.coupon.entity.CouponSpuCategoryRelationEntity;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:18:12
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

