package com.zhenlong.darwinmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.product.entity.SkuSaleAttrValueEntity;
import com.zhenlong.darwinmall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:06:55
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsString(Long skuId);
}

