package com.zhenlong.darwinmall.member.dao;

import com.zhenlong.darwinmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 20:38:12
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
