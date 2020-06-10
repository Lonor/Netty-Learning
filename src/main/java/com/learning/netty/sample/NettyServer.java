package com.learning.netty.sample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
        // bossGroup 只是处理连接请求，真正的客户端业务处理交给 workerGroup 完成
        // 两个都是无限循环
        // bossGroup workerGroup 含有的自线程 NioEventLoop 的个数默认是 CPU 核心数量的 2 倍，可在构造中指定数量
        // 比如服务端机器现在是 12 核心，那其中 NioEventLoop 就是 24 个（EventExecutor）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
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
                            System.out.println("用户 socketChannel hashcode = " + ch.hashCode());
                            // 可使用一个集合来管理这些 socketChannel
                            // 在推送消息时，可将业务加入到各个 channel 对应的 NIOEventLoop 的 taskQueue / scheduleTaskQueue
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务端 ok");

            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            // 给 future 注册监听器：
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("监听 6668 端口成功！");
                } else {
                    System.out.println("监听 6668 端口失败！");
                }
            });
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
