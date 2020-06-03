package com.learning.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author Lawrence
 * @date 2020/6/3
 */
public class NioServer {
    public static void main(String[] args) throws Exception {
        // 创建 ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 创建 Selector
        Selector selector = Selector.open();
        // 绑定一个端口，在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 把 serverSocketChannel 注册到 selector，关心事件为 accept
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("selectionKey 的注册个数：" + selector.keys().size());
        // 等待客户端连接
        while (true) {
            // 等待 1 秒，没有事件发生，继续
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待了 1 秒， 无连接。。。");
                continue;
            }
            // > 0 : 获取到有事件发生的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("有事件的 selectionKey 的数量：" + selectionKeys.size());
            for (SelectionKey selectionKey : selectionKeys) {
                // 根据 key 的事件做相应的处理
                if (selectionKey.isAcceptable()) {
                    // 有新的客户端连接: 生产一个 socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    // 将 socketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    System.out.println("客户端连接成功！SocketChannel: " + socketChannel.hashCode());
                    // 同样也要注册到选择器， 关注read，关联一个 buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("selectionKey 的注册个数：" + selector.keys().size());
                }

                if (selectionKey.isReadable()) {
                    // 通过 key 获取 channel
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 获取到 该 channel 关联的 buffer：
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(buffer);
                    System.out.println("来自客户端的信息：\n" + new String(buffer.array()));
                    socketChannel.close();
                }

                // 手动从集合中移除当前的 selectionKey， 防止重复操作：
                selectionKeys.remove(selectionKey);
            }

        }
    }
}
