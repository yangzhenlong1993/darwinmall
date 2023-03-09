package com.zhenlong.darwinmall.order.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyRabbitConfig {
    private RabbitTemplate rabbitTemplate;

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * customize RabbitTemplate
     * 1、 callback when service receive the message
     * 1、spring.rabbitmq.publisher-confirms: true
     * 2. set the confirmation of callback
     * 2、callback when message arrive the queue correctly
     * 1、spring.rabbitmq.publisher-returns: true
     * spring.rabbitmq.template.mandatory: true
     * 2、set ReturnCallback
     * 3、consuming end confirm (make sure every single message is consumed correctly，and then tell the broker to delete the message in the queue)
     */
    // @PostConstruct  //after creating the object of MyRabbitConfig, run this method
    public void initRabbitTemplate() {

        /**
         * 1、as long as the message is received by the Broker, then ack=true
         * correlationData: current message unique correlated data (this is the unique id of the message)
         * ack：receive the message or not?
         * cause：failure reason
         */
        //set confirmation callback
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            /**
             * 1. set up the mechanism of message confirmation（publisher，consumer（manually ack））
             * 2. create records for each sent message in the database, periodically resend the failed message
             */
            System.out.println("confirm...correlationData[" + correlationData + "]==>ack:[" + ack + "]==>cause:[" + cause + "]");
        });


        /**
         * Once the message cannot arrive the queue, run this callback
         * message：the info about the failed message
         * replyCode：reply code
         * replyText：reply text
         * exchange：当时这个消息发给哪个交换机 which exchange the message intend to
         * routingKey：which routing key was using
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("Fail Message[" + message + "]==>replyCode[" + replyCode + "]" +
                    "==>replyText[" + replyText + "]==>exchange[" + exchange + "]==>routingKey[" + routingKey + "]");
        });
    }
}
