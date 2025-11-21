package com.cherry.handler;

import cn.hutool.json.JSONUtil;
import com.cherry.protocol.dto.HeartReq;
import com.cherry.session.WsChannelManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 */
@Component
@ChannelHandler.Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String text = frame.text();


        HeartReq heartReq;
        try {
            heartReq = JSONUtil.toBean(text, HeartReq.class);
        } catch (Exception e) {
            // 解析失败，说明不是心跳消息，交给后续 handler 处理
            ctx.fireChannelRead(frame.retain());
            return;
        }

        if (heartReq.getType() == null || !"HEART".equalsIgnoreCase(heartReq.getType())) {
            ctx.fireChannelRead(frame.retain());
            return;
        }

        System.out.println("收到心跳包" + heartReq);

        ctx.channel().writeAndFlush(new TextWebSocketFrame("PONG"));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲，关闭连接: " + ctx.channel());
                WsChannelManager.removeSessionByChannel(ctx.channel());
                ctx.close();
            }
            return;
        }
        super.userEventTriggered(ctx, evt);
    }


}
