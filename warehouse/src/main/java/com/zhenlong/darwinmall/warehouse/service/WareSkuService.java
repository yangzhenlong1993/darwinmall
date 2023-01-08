package com.zhenlong.darwinmall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.warehouse.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 21:07:54
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

