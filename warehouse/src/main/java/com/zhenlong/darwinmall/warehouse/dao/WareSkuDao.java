package com.zhenlong.darwinmall.warehouse.dao;

import com.zhenlong.darwinmall.warehouse.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 21:07:54
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
