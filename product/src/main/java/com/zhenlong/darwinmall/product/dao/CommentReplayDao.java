package com.zhenlong.darwinmall.product.dao;

import com.zhenlong.darwinmall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:06:55
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
