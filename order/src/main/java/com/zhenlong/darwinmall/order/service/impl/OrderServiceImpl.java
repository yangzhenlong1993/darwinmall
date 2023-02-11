package com.zhenlong.darwinmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.Query;
import com.zhenlong.common.vo.MemberRespVo;
import com.zhenlong.darwinmall.order.dao.OrderDao;
import com.zhenlong.darwinmall.order.entity.OrderEntity;
import com.zhenlong.darwinmall.order.feign.CartFeignService;
import com.zhenlong.darwinmall.order.feign.MemberFeignService;
import com.zhenlong.darwinmall.order.interceptor.LoginUserInterceptor;
import com.zhenlong.darwinmall.order.service.OrderService;
import com.zhenlong.darwinmall.order.vo.MemberAddressVo;
import com.zhenlong.darwinmall.order.vo.OrderConfirmVo;
import com.zhenlong.darwinmall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //远程查询所有的收货地址列表
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(items);
            //Feign在远程调用之前要构造请求，调用很多的拦截器RequestInterceptor
            //远程查询购物车所有选中的购物项
        }, executor);

        //查询用户积分
        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);
        //TODO 防重令牌

        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return orderConfirmVo;
    }

}