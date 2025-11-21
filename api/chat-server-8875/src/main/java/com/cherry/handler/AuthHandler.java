package com.cherry.handler;

import cn.hutool.json.JSONUtil;
import com.cherry.constant.RedisKeys;
import com.cherry.protocol.dto.WsAuthReq;
import com.cherry.session.WsChannelManager;
import com.cherry.session.WsSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket 鉴权 Handler
 * 负责：
 * 1. 接收第一条 AUTH 消息
 * 2. 校验 token -> userId
 * 3. 调用 WsChannelManager.bindSession 绑定多端会话
 *
 * @author cherry
 */
@Component
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        String text = msg.text();
        System.out.println("AuthHandler 收到消息 = " + text);

        // 1. 如果已经有 session 了，说明鉴权通过了，直接交给后面的 Handler（ChatHandler）
        WsSession existing = WsChannelManager.getSession(channel);
        if (existing != null) {
            // 非首次消息，直接往后传
            ctx.fireChannelRead(msg.retain());
            return;
        }

        // 2. 解析为 WsAuthReq
        WsAuthReq authReq;
        try {
            authReq = JSONUtil.toBean(text, WsAuthReq.class);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("鉴权消息格式错误"));
            channel.close();
            return;
        }

        if (authReq.getToken() == null) {
            channel.writeAndFlush(new TextWebSocketFrame("缺少 token"));
            channel.close();
            return;
        }

        // 3. 根据 token 查 userId（示例：从 Redis 中获取）
        // 你根据自己项目的登录逻辑来改这里即可
        Long userId;
        try {
            String uid = (String) redisTemplate.opsForValue().get(RedisKeys.LOGIN_TOKEN + authReq.getToken());
            if (uid == null) {
                channel.writeAndFlush(new TextWebSocketFrame("token 无效，请重新登录"));
                channel.close();
                return;
            }
            userId = Long.valueOf(uid);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("鉴权失败，请稍后重试"));
            channel.close();
            return;
        }

        // 4. 绑定会话（关键：这里真正调用 bindSession）

        WsChannelManager.bindSession(channel, userId, authReq.getClientType().getCode(), authReq.getDeviceId(), authReq.getToken());

        // 5. 给前端一个简单确认
        channel.writeAndFlush(new TextWebSocketFrame("AUTH_OK"));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 断开连接时，移除会话
        WsChannelManager.removeSessionByChannel(ctx.channel());
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("AuthHandler 异常：" + cause.getMessage());
        ctx.close();
    }
}
