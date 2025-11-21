package com.cherry.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cherry
 */
@Configuration
public class RabbitMQConfig {


    public static final String EXCHANGE_CHAT_MESSAGE = "cloudchat.message.exchange";
    public static final String QUEUE_CHAT_MESSAGE = "cloudchat.message.queue";
    public static final String ROUTING_KEY_CHAT_MESSAGE = "cloudchat.message.#";


    @Bean(EXCHANGE_CHAT_MESSAGE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_CHAT_MESSAGE).durable(true).build();
    }

    @Bean(QUEUE_CHAT_MESSAGE)
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_CHAT_MESSAGE).build();
    }

    // 定义队列绑定到交换即的关系
    @Bean
    public Binding binding(@Qualifier(EXCHANGE_CHAT_MESSAGE) Exchange exchange, @Qualifier(QUEUE_CHAT_MESSAGE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("cloudchat.#").noargs();
    }
}
