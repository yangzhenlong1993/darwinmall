package com.zhenlong.darwinmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhenlong.common.to.es.SkuEsModel;
import com.zhenlong.darwinmall.search.config.DarwinMallElasticSearchConfig;
import com.zhenlong.darwinmall.search.constant.EsConstant;
import com.zhenlong.darwinmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public boolean productStatusOnSale(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es
        //1.给es中建立索引,建立好映射关系
        //2.给ES中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : skuEsModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, DarwinMallElasticSearchConfig.COMMON_OPTIONS);
        //TODO 如果批量插入错误
        boolean b = bulk.hasFailures();
        List<String> errorId = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        return b;
    }
}
