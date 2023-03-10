package com.zhenlong.darwinmall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.darwinmall.warehouse.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 21:07:54
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

