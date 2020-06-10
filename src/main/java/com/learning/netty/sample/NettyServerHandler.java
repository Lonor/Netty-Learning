package com.learning.netty.sample;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Lawrence
 * @date 2020/6/4
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据
     *
     * @param ctx 上下文，包含 pipeline（包含 handler）、channel、地址
     * @param msg 客户端的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果这里有一个非常耗时的业务，可以让它异步执行、提交 channel 对应的 NioEventLoop 的 taskQueue 中
        Thread.sleep(10000);
        // 解决方案一： 自定义普通任务 -> 提交的 taskQueue
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello 客户端 2", CharsetUtil.UTF_8));
            } catch (Exception e) {
                System.out.println("发生异常" + e.getMessage());
            }
        });
        // 这里要注意， taskQueue 是队列，FIFO，再次提交异步任务是用的同一个线程跑的：
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(20000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello 客户端 3", CharsetUtil.UTF_8));
            } catch (Exception e) {
                System.out.println("发生异常" + e.getMessage());
            }
        });

        // 用户自定义定时任务： 提交到 scheduleTaskQueue 中
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(5000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello 客户端 4 - schedule", CharsetUtil.UTF_8));
            } catch (Exception e) {
                System.out.println("发生异常" + e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);

        System.out.println("go on ...");

        // System.out.println("服务器读取线程 " + Thread.currentThread().getName());
        // System.out.println("ctx = " + ctx);
        // // 将 msg 转成 ByteBuffer
        // ByteBuf buf = (ByteBuf) msg;
        // Channel channel = ctx.channel();
        // ChannelPipeline pipeline = ctx.pipeline();
        // System.out.println("客户端消息：" + buf.toString(CharsetUtil.UTF_8));
        // System.out.println("客户端地址：" + channel.remoteAddress());
    }

    /**
     * 读取完毕
     *
     * @param ctx 上下文
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将数据写到缓存并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello 客户端！", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常，一般关闭通道
     *
     * @param ctx 上下文
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
