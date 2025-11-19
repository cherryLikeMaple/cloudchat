package com.cherry.netty;

import com.cherry.netty.http.HttpServerInitializer;
import com.cherry.netty.websocket.WSServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty 服务启动类
 * @author cherry
 */
public class ChatServer {
    public static void main(String[] args) throws InterruptedException {
        // 定义主从线程组
        NioEventLoopGroup bossGroup  = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup  = new NioEventLoopGroup(2);
        
        try {
            // 构建netty服务器
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    // 设置Nio的双向通道.
                    .channel(NioServerSocketChannel.class)
                    // 设置处理器, 用于处理workerGroup的内容
//                    .childHandler(new HttpServerInitializer())
                    .childHandler(new WSServerInitializer());

            // 启动server, 绑定端口号. 
            ChannelFuture channelFuture = server.bind(8875).sync();
            // 监听关闭的channel.
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭线程池组.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
