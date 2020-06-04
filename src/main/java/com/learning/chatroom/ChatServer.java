package com.learning.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Lawrence
 * @date 2020/6/3
 */
public class ChatServer {

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.listen();
    }

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private static final int PORT = 6666;

    /**
     * 初始化服务端
     */
    public ChatServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(PORT);
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端监听
     */
    public void listen() {
        try {
            System.out.println("服务端启动 ...");
            while (true) {
                int select = selector.select();
                if (select > 0) {
                    // 有事件key
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel acceptSocketChannel = serverSocketChannel.accept();
                            acceptSocketChannel.configureBlocking(false);
                            acceptSocketChannel.register(selector, SelectionKey.OP_READ);
                            // 客户端上线了：
                            System.out.println(acceptSocketChannel.getRemoteAddress() + " 上线了");
                        }
                        if (selectionKey.isReadable()) {
                            // 读取消息
                            readMessage(selectionKey);
                        }
                        // 使用迭代器是为了防止并发修改异常
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readMessage(SelectionKey selectionKey) {
        SocketChannel readableChannel = null;
        try {
            readableChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = readableChannel.read(buffer);
            if (read > 0) {
                String msg = new String(buffer.array());
                System.out.println("读取到来自客户端的消息：" + msg);
                // 转发消息（排除当前客户端）
                forwardMessage(selectionKey, msg);
            }
        } catch (IOException e) {
            try {
                System.out.println(readableChannel.getRemoteAddress() + " 已离线");
                // 取消注册
                selectionKey.cancel();
                readableChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 转发消息
     *
     * @param selectionKey 当前客户端选择器的 selectionKey
     * @param msg          消息内容
     */
    private void forwardMessage(SelectionKey selectionKey, String msg) throws IOException {
        System.out.println("服务端开始转发消息");
        Selector selector = selectionKey.selector();
        Set<SelectionKey> keys = selector.keys();
        SocketChannel fromChannel = (SocketChannel) selectionKey.channel();
        for (SelectionKey key : keys) {
            Channel everyChannel = key.channel();
            if (everyChannel instanceof SocketChannel && everyChannel != fromChannel) {
                SocketChannel destinationChannel = (SocketChannel) everyChannel;
                ByteBuffer wrapBuffer = ByteBuffer.wrap(msg.getBytes());
                destinationChannel.write(wrapBuffer);
            }
        }
    }

}
