package com.cherry.netty.websocket;

import com.cherry.netty.websocket.manage.WsChannelManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 */
@Component
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            System.out.println("进入读空闲");
        } else if (event.state() == IdleState.WRITER_IDLE) {
            System.out.println("进入写空闲");
        } else if (event.state() == IdleState.ALL_IDLE) {
            System.out.println("channel 关闭前 clients数量为" + WsChannelManager.getSessionSize());
            ctx.channel().close();
            System.out.println("channel 关闭前 clients数量为" + WsChannelManager.getSessionSize());
        }
    }
}
