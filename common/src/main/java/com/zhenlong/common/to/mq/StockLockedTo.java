package com.zhenlong.common.to.mq;

import lombok.Data;

@Data
public class StockLockedTo {
    private Long id;//库存工作单id
    private StockDetailTo detailTo;//工作单详情的所有ID
}
