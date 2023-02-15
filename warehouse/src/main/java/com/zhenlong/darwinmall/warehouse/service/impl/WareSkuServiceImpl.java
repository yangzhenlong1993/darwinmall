package com.zhenlong.darwinmall.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.Query;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.warehouse.dao.WareSkuDao;
import com.zhenlong.darwinmall.warehouse.entity.WareSkuEntity;
import com.zhenlong.darwinmall.warehouse.feign.ProductFeignService;
import com.zhenlong.darwinmall.warehouse.service.WareSkuService;
import com.zhenlong.darwinmall.warehouse.vo.LockStockResult;
import com.zhenlong.darwinmall.warehouse.vo.SkuHasStockVo;
import com.zhenlong.darwinmall.warehouse.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        if(wareSkuEntities ==null||wareSkuEntities.size()==0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku name，如果失败，整个事务无需回滚
            //自己catch异常，可以让异常出现以后不回滚整个事务
            //TODO 还可以用什么方法不回滚整个事务？分布式事务

            try{
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode()==0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }

            wareSkuDao.insert(wareSkuEntity);
        } else{
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

    @Override
    public List<LockStockResult> orderLockStock(WareSkuLockVo vo) {
        return null;
    }

}