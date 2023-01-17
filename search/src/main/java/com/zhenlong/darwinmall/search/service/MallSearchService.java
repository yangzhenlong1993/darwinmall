package com.zhenlong.darwinmall.search.service;

import com.zhenlong.darwinmall.search.vo.SearchParam;
import com.zhenlong.darwinmall.search.vo.SearchResult;

public interface MallSearchService {

    /**
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果
     */
    SearchResult search(SearchParam param);
}
