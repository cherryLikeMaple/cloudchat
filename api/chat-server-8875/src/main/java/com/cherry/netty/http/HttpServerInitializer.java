package com.cherry.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 *
 * @author cherry
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        
        // 得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        
        // 设置handler
        
        // 请求到服务的, 我们需要进行解码.
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("HttpHandler", new HttpHandler());
    }
}
