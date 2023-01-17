package com.zhenlong.darwinmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 */
@Data
public class SearchParam {
    private String keyword;//页面传递过来的全文匹配关键字
    private Long catalog3Id;//三级分类id

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;//排序条件
    /**
     * 还有很多的过滤条件
     * hasStock(是否有货),skuPrice区间,brandId,catalog3Id,attrs
     */
    private Integer hasStock;//默认有库存
    private String skuPrice;//按照价格区间
    private List<Long> brandId;//按照品牌进行查询
    private List<String> attrs;//按照属性进行筛选
    private Integer pageNum = 1;//页码
    private String queryString;
}
