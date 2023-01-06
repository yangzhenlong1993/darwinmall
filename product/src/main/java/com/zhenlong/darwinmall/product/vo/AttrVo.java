package com.zhenlong.darwinmall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.zhenlong.darwinmall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrVo extends AttrEntity {
    /**
     * 分组ID
     */
    private Long attrGroupId;
}
