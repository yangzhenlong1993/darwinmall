package com.zhenlong.darwinmall.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenlong.common.exception.NoStockException;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.Query;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.warehouse.dao.WareSkuDao;
import com.zhenlong.darwinmall.warehouse.entity.WareSkuEntity;
import com.zhenlong.darwinmall.warehouse.feign.ProductFeignService;
import com.zhenlong.darwinmall.warehouse.service.WareSkuService;
import com.zhenlong.darwinmall.warehouse.vo.OrderItemVo;
import com.zhenlong.darwinmall.warehouse.vo.SkuHasStockVo;
import com.zhenlong.darwinmall.warehouse.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.hasText(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.hasText(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1. 判断如果还没有这个库存记录，那就是新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku name，如果失败，整个事务无需回滚
            //自己catch异常，可以让异常出现以后不回滚整个事务
            //TODO 还可以用什么方法不回滚整个事务？分布式事务

            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> hasStockList = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return hasStockList;
    }

    /**
     * 默认运行时异常都会回滚
     *
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //1.按照下单的收获地址，找到一个就近仓库，锁定仓库
        //2.找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> wareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listByWareIdHasStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        //3.锁定库存
        for (SkuWareHasStock wareHasStock : wareHasStocks) {
            Boolean skuStocked = false;
            Long skuId = wareHasStock.getSkuId();
            List<Long> wareIds = wareHasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, wareHasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }
            if (skuStocked == false) {
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }
        //3.肯定全部都是锁定成功
        return true;

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }

}