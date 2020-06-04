package com.learning.netty.sample;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
        System.out.println("ctx = " + ctx);
        // 将 msg 转成 ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
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
