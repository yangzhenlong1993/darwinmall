package com.zhenlong.darwinmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.Query;
import com.zhenlong.common.utils.R;
import com.zhenlong.common.vo.MemberRespVo;
import com.zhenlong.darwinmall.order.constant.OrderConstant;
import com.zhenlong.darwinmall.order.dao.OrderDao;
import com.zhenlong.darwinmall.order.entity.OrderEntity;
import com.zhenlong.darwinmall.order.entity.OrderItemEntity;
import com.zhenlong.darwinmall.order.feign.CartFeignService;
import com.zhenlong.darwinmall.order.feign.MemberFeignService;
import com.zhenlong.darwinmall.order.feign.ProductFeignService;
import com.zhenlong.darwinmall.order.feign.WmsFeignService;
import com.zhenlong.darwinmall.order.interceptor.LoginUserInterceptor;
import com.zhenlong.darwinmall.order.orderEnum.OrderStatusEnum;
import com.zhenlong.darwinmall.order.service.OrderItemService;
import com.zhenlong.darwinmall.order.service.OrderService;
import com.zhenlong.darwinmall.order.to.OrderCreateTo;
import com.zhenlong.darwinmall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    StringRedisTemplate redisTemplate;

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();

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
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skusHasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        }, executor);

        //查询用户积分
        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);
        //TODO 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return orderConfirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        orderSubmitVoThreadLocal.set(vo);
        //去服务器创建订单，验令牌，验价格，锁库存....
        //1.验证令牌 令牌的对比和删除必须保持原子性
        //0令牌删除失败。 1令牌校验并且删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        String orderToken = vo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L) {
            responseVo.setCode(1);
            //令牌验证失败
            return responseVo;
        } else {
            //令牌验证成功
            responseVo.setCode(0);
            //创建订单，订单项信息
            OrderCreateTo order = createOrder();
            //2.验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比成功
                //3.保存订单
                saveOrder(order);
                //4.库存锁定，只要有异常回滚订单数据
                //订单号，所有订单项(skuId, skuName, num)
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(orderItemVos);
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //库存锁定成功
                } else {
                    //库存锁定失败
                }
                return responseVo;
            } else {
                //金额对比失败
                responseVo.setCode(2);
                return responseVo;
            }
        }

    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1.生成一个订单号
        String orderSn = IdWorker.getTimeId();
        //创建订单号
        OrderEntity orderEntity = buildOrder(orderSn);
        //2,获取到所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        //3.计算价格，积分等相关
        computePrice(orderEntity, itemEntities);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal coupon = BigDecimal.ZERO;
        BigDecimal integration = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        BigDecimal gift = BigDecimal.ZERO;
        BigDecimal growth = BigDecimal.ZERO;
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        //1.订单价格相关
        orderEntity.setTotalAmount(total);
        //设置应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        //设置积分信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);//未删除
    }

    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(memberRespVo.getId());
        //获取收货地址信息
        OrderSubmitVo submitVo = orderSubmitVoThreadLocal.get();
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResponse = fare.getData(new TypeReference<FareVo>() {
        });
        //设置运费信息
        entity.setFreightAmount(fareResponse.getFare());
        //设置收货人信息
        entity.setReceiverCity(fareResponse.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResponse.getAddress().getDetailAddress());
        entity.setReceiverName(fareResponse.getAddress().getName());
        entity.setReceiverPhone(fareResponse.getAddress().getPhone());
        entity.setReceiverPostCode(fareResponse.getAddress().getPostCode());
        entity.setReceiverProvince(fareResponse.getAddress().getProvince());
        entity.setReceiverRegion(fareResponse.getAddress().getRegion());

        //设置订单相关的状态信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);
        return entity;
    }

    /**
     * 构建所有订单数据
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确定每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建每一个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1.订单信息
        //2.商品的spu信息
        Long skuId = cartItem.getSkuId();
        R spuInfoBySkuId = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = spuInfoBySkuId.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        //3.商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4.TODO 优惠信息
        //5.信息积分
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        //6.订单项价格信息
        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setCouponAmount(BigDecimal.ZERO);
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //当前订单项的实际金额 原价减去各种优惠价格
        BigDecimal originalPrice = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal finalPrice = originalPrice.subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(finalPrice);
        return itemEntity;
    }

}