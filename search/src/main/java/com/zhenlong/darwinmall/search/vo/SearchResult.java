package com.zhenlong.darwinmall.search.vo;

import com.zhenlong.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    //查询到的所有sku信息
    private List<SkuEsModel> products;
    /*
    以下是分页信息
     */
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码
    private List<Integer> pageNavs;
    private List<BrandVo> brands;//当前查询到的结果，所有涉及到的品牌
    private List<CatalogVo> catalogs;//查询到涉及到的所有分类信息
    private List<AttrVo> attrs;//当前结果查询到的结果，所有涉及到的所有属性

    //================以上是返回给页面的信息=======================
    //面包屑导航
    private List<NavVo> navs;

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

}
