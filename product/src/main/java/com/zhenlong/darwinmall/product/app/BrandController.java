package com.zhenlong.darwinmall.product.app;

import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.R;
import com.zhenlong.common.validator.group.AddGroup;
import com.zhenlong.common.validator.group.UpdateGroup;
import com.zhenlong.common.validator.group.UpdateStatusGroup;
import com.zhenlong.darwinmall.product.entity.BrandEntity;
import com.zhenlong.darwinmall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:57:54
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * list
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {AddGroup.class}) @RequestBody BrandEntity brand/**BindingResult result*/) {
//        if (result.hasErrors()) {
//            Map<String, String> map = new HashMap<>();
//            result.getFieldErrors().forEach((item) -> {
//                //获取错误提示消息
//                String message = item.getDefaultMessage();
//                //获取错误的属性的名字
//                String field = item.getField();
//                map.put(field, message);
//            });
//            return R.error(400, "the submitted data is illegal").put("data", map);
//        } else {
//
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {UpdateGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);

        return R.ok();
    }
    /**
     * 仅修改状态
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(value = {UpdateStatusGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
