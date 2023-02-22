package com.zhenlong.darwinmall.warehouse.listener;

import com.rabbitmq.client.Channel;
import com.zhenlong.common.to.mq.StockLockedTo;
import com.zhenlong.darwinmall.warehouse.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 库存自动解锁
     * 库存解锁的场景
     * * 1. 下订单成功，但是订单过期没有支付，被系统自动取消，被用户手动取消，都要解锁库存
     * * 2. 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁
     * 锁库存失败
     * 只要解锁库存的消息失败，一定要告诉服务器解锁失败，启动手动ack机制
     *
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
