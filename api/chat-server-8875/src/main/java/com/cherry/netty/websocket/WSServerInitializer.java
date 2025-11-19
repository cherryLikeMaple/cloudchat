package com.cherry.netty.websocket;

import com.cherry.netty.http.HttpHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author cherry
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {
    
    
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
        
        // 自定义助手类
        p.addLast(new ChatHandler());
    }
}
