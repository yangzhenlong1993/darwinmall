package com.zhenlong.darwinmall.product.vo;

import com.zhenlong.darwinmall.product.entity.SkuImagesEntity;
import com.zhenlong.darwinmall.product.entity.SkuInfoEntity;
import com.zhenlong.darwinmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class SkuItemVo {
    SkuInfoEntity info;
    Boolean hasStock=true;
    List<SkuImagesEntity> images;
    SpuInfoDescEntity desp;
    List<SkuItemSaleAttrVo> saleAttr;
    List<SpuItemAttrGroupAttrVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }

    @Data
    public static class SpuItemAttrGroupAttrVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }
}
