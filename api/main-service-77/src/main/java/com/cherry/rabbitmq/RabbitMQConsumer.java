package com.cherry.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.cherry.config.RabbitMQConfig;
import com.cherry.ws.WsChatSendReq;
import com.cherry.service.ChatMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 */
@Component
@Slf4j
public class RabbitMQConsumer {

    @Resource
    private ChatMessageService chatMessageService;

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_CHAT_MESSAGE})
    public void receiveText(String msgJson) {
        try {
            WsChatSendReq wsChatSendReq = JSONUtil.toBean(msgJson, WsChatSendReq.class);
            chatMessageService.saveMsg(chatMessageService.dtoToEntity(wsChatSendReq));
            log.info("消费成功：{}", wsChatSendReq);
        } catch (Exception e) {
            log.error("消费消息失败，msg={}", msgJson, e);
            // 这里抛异常，消息会重新入队；如果你想丢弃可以不抛
            throw e;
        }
    }
}
