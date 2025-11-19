package com.cherry.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author cherry
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 保存所有连接的 Channel (在线用户)
    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 有新的客户端连接时, 调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 通知其他人：有人上线
        CHANNELS.writeAndFlush(
                new TextWebSocketFrame("[系统消息] 用户 " + incoming.id().asShortText() + " 加入聊天")
        );
        // 把当前 channel 加到管理组
        CHANNELS.add(incoming);
        System.out.println("客户端连接：" + incoming.remoteAddress());
    }

    /**
     * 断开连接时候
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel leaving = ctx.channel();
        CHANNELS.writeAndFlush(
                new TextWebSocketFrame("[系统消息] 用户 " + leaving.id().asShortText() + " 离开聊天")
        );
        System.out.println("客户端断开：" + leaving.remoteAddress());
        // Netty 会自动从 ChannelGroup 中移除，无需手动
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        // 客户端传过来的数据
        String text = msg.text();
        Channel channel = ctx.channel();
        String longText = channel.id().asLongText();
        String shortText = channel.id().asShortText();

        channel.writeAndFlush(new TextWebSocketFrame(longText));
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
