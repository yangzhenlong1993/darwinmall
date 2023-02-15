package com.zhenlong.darwinmall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.warehouse.entity.WareInfoEntity;
import com.zhenlong.darwinmall.warehouse.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 21:07:54
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户的收获地址进行计算运费
     *
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

