package com.learning.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author Lawrence
 * @date 2020/6/10
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向管道添加处理器
        ChannelPipeline pipeline = ch.pipeline();
        // Netty 提供了处理 HTTP 的编码解码器：
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        // 可以添加自定义的处理器
        pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());

    }
}
