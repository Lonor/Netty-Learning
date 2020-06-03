package com.learning.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Lawrence
 * @date 2020/6/3
 */
public class NioClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞");
            }
        }
        // 连接成功：
        String message = "hello~";
        // 将字节数组包装到缓冲区中
        ByteBuffer wrapBuffer = ByteBuffer.wrap(message.getBytes());
        // 发送实际就是把 buffer 写进 channel：
        socketChannel.write(wrapBuffer);
        socketChannel.close();
    }
}
