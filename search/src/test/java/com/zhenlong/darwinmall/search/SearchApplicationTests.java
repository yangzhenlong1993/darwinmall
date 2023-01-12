package com.zhenlong.darwinmall.search;

import com.alibaba.fastjson.JSON;
import com.zhenlong.darwinmall.search.config.DarwinMallElasticSearchConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

    /**
     * 分析：什么数据该存入ES数据
     * 方案1：优势：方便检索
     *  {
     *      skuId:1
     *      spuId:11
     *      skuTitle:华为mate
     *      price:998
     *      saleCount:99
     *      attrs:[
     *          {size: 5`},
     *          {CPU:高通945},
     *          {resolution: full HD}
     *      ]
     *  }
     *
     *  冗余：假设100w条spu信息
     *  100w*20kb = 2Gb
     *
     *  方案2
     *  只保存关键属性在sku索引中
     *  sku索引{
     *      skuId:1
     *      spuId:11
     *  }
     *  然后再从attr索引中存储
     *  attr索引{
     *      .....
     *  }
     *
     *  方案2问题分析
     *  搜索"小米"
     *  冗余太大，时间浪费
     *
     */
    @Autowired
    private RestHighLevelClient client;
    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    /**
     * 测试存储数据到ES
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("1");
        //indexRequest.source("userName","zhangsan","age",18,"gender","male");
        User user = new User();
        user.setUserName("zhangsan");
        user.setAge(18);
        user.setGender("male");
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON);

        //执行操作
        IndexResponse index = client.index(indexRequest, DarwinMallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //指定检索哪个索引
        searchRequest.indices("bank");
        //指定DSL，即检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //构造检索条件1
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //按照年龄的值分布聚合
        TermsAggregationBuilder ageAggBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
        //计算平均薪资
        AvgAggregationBuilder balanceAvgBuilder = AggregationBuilders.avg("balanceAvg").field("balance");

        searchSourceBuilder.aggregation(ageAggBuilder);
        searchSourceBuilder.aggregation(balanceAvgBuilder);

        System.out.println("检索条件"+searchSourceBuilder.toString());
        //searchSourceBuilder.from();
        //searchSourceBuilder.size();
        //searchSourceBuilder.aggregation();
        searchRequest.source(searchSourceBuilder);
        //执行检索
        SearchResponse searchResponse = client.search(searchRequest, DarwinMallElasticSearchConfig.COMMON_OPTIONS);
        //分析结果
        //获取所有的查到的数据hits
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit: searchHits) {
           // searchHit.getIndex();
            String string = searchHit.getSourceAsString();
            //可以用json工具转换为对象
        }
        //获取查到数据的aggregation信息
        Aggregations aggregations = searchResponse.getAggregations();
        for (Aggregation aggregation: aggregations.asList()) {
            System.out.println("当前聚合的名字是"+aggregation.getName());

        }

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println(balanceAvg.getValue());
    }

}
