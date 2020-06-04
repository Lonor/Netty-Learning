package com.learning.netty.sample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Lawrence
 * @date 2020/6/4
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建 Boss Group 、 Worker Group
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup) // 设置线程组
                    .channel(NioServerSocketChannel.class) // 使用 NioServerSocketChannel 作为服务端通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持活动连接的个数
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给 worker group 的 EventLoop 对应的 pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务端 ok");

            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
