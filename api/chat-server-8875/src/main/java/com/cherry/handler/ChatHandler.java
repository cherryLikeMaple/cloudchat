package com.cherry.handler;

import cn.hutool.json.JSONUtil;
import com.cherry.config.RabbitMQConfig;
import com.cherry.dist.ManageUtils;
import com.cherry.session.WsChannelManager;
import com.cherry.session.WsSession;
import com.cherry.ws.MsgType;
import com.cherry.ws.WsChatSendReq;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author cherry
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        System.out.println("RabbitTemplate = " + rabbitTemplate);
    }

    /**
     * 断开连接时候
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        WsChannelManager.removeSessionByChannel(channel);
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Channel channel = ctx.channel();
        String text = msg.text();
        System.out.println("收到原始消息 = " + text);

        // 1.鉴权
        WsSession session = WsChannelManager.getSession(channel);
        if (session == null) {
            channel.writeAndFlush(new TextWebSocketFrame("未授权连接，拒绝处理消息"));
            return;
        }

        Long senderId = session.getUserId();

        // 2. 解析消息
        WsChatSendReq req;

        try {
            req = JSONUtil.toBean(text, WsChatSendReq.class);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("消息格式错误，请重试"));
            return;
        }

        req.setSenderId(senderId);
        req.setChatTime(now);
        // 4. 根据 msgType 分发
        MsgType msgType = req.getMsgType();
        if (msgType == null) {
            channel.writeAndFlush(new TextWebSocketFrame("msgType 不能为空"));
            return;
        }

        String msgJson = JSONUtil.toJsonStr(req);
        // 此处使用rabbitmq 实现异步解耦 产生消息给后端,完成消息的保存.
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CHAT_MESSAGE,
                RabbitMQConfig.ROUTING_KEY_CHAT_MESSAGE,
                msgJson);

        switch (msgType) {
            case TEXT:
                ManageUtils.send(req, senderId, channel);
                break;
            case IMAGE:
                handleMedia(req, senderId, channel);
                break;
            case VIDEO:
                handleMedia(req, senderId, channel);
                break;
            case VOICE:
                // TODO: 实现语音消息
                channel.writeAndFlush(new TextWebSocketFrame("语音消息暂未实现"));
                break;
            default:
                channel.writeAndFlush(new TextWebSocketFrame("不支持的消息类型：" + msgType));
        }


    }

    private void handleMedia(WsChatSendReq req, Long senderId, Channel channel) {

        if (req.getMediaUrl() == null) {
            channel.writeAndFlush(new TextWebSocketFrame("传入的url为空"));
        }
        if (req.getMediaHeight() == null) {
            channel.writeAndFlush(new TextWebSocketFrame("传入的height为空"));
        }
        if (req.getMediaWidth() == null) {
            channel.writeAndFlush(new TextWebSocketFrame("传入的width为空"));
        }

        ManageUtils.send(req, senderId, channel);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常：" + cause.getMessage());
        ctx.close();
        // netty会自动处理断开的channel.
    }
}
