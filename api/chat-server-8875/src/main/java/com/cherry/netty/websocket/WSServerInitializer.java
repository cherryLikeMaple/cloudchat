package com.cherry.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;

import java.util.concurrent.TimeUnit;

/**
 * @author cherry
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    private final AuthHandler authHandler;
    private final ChatHandler chatHandler;
    private final HeartbeatHandler heartbeatHandler;

    public WSServerInitializer(AuthHandler authHandler, ChatHandler chatHandler, HeartbeatHandler heartbeatHandler) {
        this.authHandler = authHandler;
        this.chatHandler = chatHandler;
        this.heartbeatHandler = heartbeatHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();

        // 1. HTTP 编解码 (websocket是基于http升级的)
        p.addLast(new HttpServerCodec());
        // 聚合成 FullHttpRequest
        p.addLast(new HttpObjectAggregator(64 * 1024));
        // 添加对大数据流的支持.
        p.addLast(new ChunkedWriteHandler());
        // 2. 升级协议
        p.addLast(new WebSocketServerProtocolHandler("/ws"));
        
        p.addLast(authHandler);
        // 3. 心跳检测 
        // 如果 1 min 内没有向服务器发送读写心跳(ALL), 则主动断开连接
        // 如果是读空闲或写空闲, 不做任何处理
        p.addLast(new IdleStateHandler(8, 10, 12, TimeUnit.SECONDS));
        // 自定义助手类
        p.addLast(heartbeatHandler);
        p.addLast(chatHandler);
    }
}
