package com.zhenlong.darwinmall.product.app;

import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.R;
import com.zhenlong.darwinmall.product.entity.CategoryEntity;
import com.zhenlong.darwinmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品三级分类
 *
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:57:54
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形列表解构组装起来
     * get all categories and sub categories, combine them in tree-like structure
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        return R.ok().put("data", categoryEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 删除
     * @RequestBody获取请求体，只有POST才有请求体，必须发送POST请求
     * SpringMVC会自动将请求体的数据（json）转为对应的对象
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        //1. 检查当前删除的菜单，是否被别的地方引用
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
