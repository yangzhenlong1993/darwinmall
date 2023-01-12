package com.zhenlong.darwinmall.search.service;

import com.zhenlong.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusOnSale(List<SkuEsModel> skuEsModels) throws IOException;
}
