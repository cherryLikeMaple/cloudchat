package com.cherry.server;

import com.cherry.handler.AuthHandler;
import com.cherry.handler.ChatHandler;
import com.cherry.handler.HeartbeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring 启动后自动启动 Netty WebSocket 服务器
 *
 * @author cherry
 */
@Component
public class NettyWebSocketServer implements CommandLineRunner {

    @Resource
    private AuthHandler authHandler;

    @Resource
    private ChatHandler chatHandler;

    @Resource
    private HeartbeatHandler heartbeatHandler;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    @Override
    public void run(String... args) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4);

        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new WSServerInitializer(authHandler, chatHandler, heartbeatHandler));

        ChannelFuture channelFuture = server.bind(8875).sync();
        System.out.println("WebSocket 服务器启动成功，端口：8875");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("关闭 Netty WebSocket 服务器...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
