package com.learning.nio;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering：【分散】 将数据写入到 buffer 时，采用 buffer 数组，依次写入
 * Gathering： 从 buffer 中读取数据时，采用 buffer 数组， 依次读
 *
 * @author Lawrence
 * @date 2020/6/3
 */
public class ScatteringAndGatheringTest {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(6666);

        // 绑定端口到 socket，启动：
        serverSocketChannel.socket().bind(inetSocketAddress);
        System.out.println("start.");

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        // 等客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("accepted.");
        int messageLength = 8;

        while (true) {
            int byteRead = 0;

            while (byteRead < messageLength) {
                long read = socketChannel.read(byteBuffers);
                byteRead += read;
                System.out.println("byteRead = " + byteRead);
                Arrays.stream(byteBuffers).map(buffer -> "position = " + buffer.position() + ", limit = " + buffer.limit()).forEach(System.out::println);
            }

            // 将所有 buffer 翻转
            Arrays.asList(byteBuffers).forEach(Buffer::flip);

            // 将数据读出显示到客户端
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long write = socketChannel.write(byteBuffers);
                byteWrite += write;
            }

            Arrays.asList(byteBuffers).forEach(ByteBuffer::clear);
            System.out.println("byteRead: " + byteRead + " byteWrite: " + byteWrite + " messageLength: " + messageLength);
        }

    }
}
