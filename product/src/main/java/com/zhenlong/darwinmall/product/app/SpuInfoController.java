package com.zhenlong.darwinmall.product.app;

import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.product.entity.SpuInfoEntity;
import com.zhenlong.darwinmall.product.service.SpuInfoService;
import com.zhenlong.darwinmall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * spu信息
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:57:54
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * Spu上架功能
     */
    @PostMapping("/{spuId}/up")
    public R SpuPutOnSale(@PathVariable(value = "spuId") Long spuId) {
        spuInfoService.putOnSale(spuId);

        return R.ok();
    }

    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId) {
        SpuInfoEntity entity = spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(entity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
		//spuInfoService.save(vo);
        spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
