package com.learning.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Lawrence
 * @date 2020/6/2
 */
public class NIOFileChannelExample {

    public static void main(String[] args) throws Exception {
        writeFile();
        String res = readFile();
        System.out.println(res);
    }

    /**
     * 本地文件写入
     */
    public static void writeFile() throws IOException {
        String string = "Hello world";
        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("./file01.txt");
        // 通过输出流获取对应的文件 channel -> FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();
        // 创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 将字符串放入缓冲区
        byteBuffer.put(string.getBytes());
        // 反转 position
        byteBuffer.flip();
        // 将缓冲区数据写入通道: 从 buffer -> channel 使用 channel 的 write 方法。
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }

    /**
     * 读取文件
     * @return 文件中的字符串
     */
    private static String readFile() throws Exception {
        File file = new File("./file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        // 读取通道中的数据到 buffer 中：read
        fileChannel.read(byteBuffer);
        fileInputStream.close();
        return new String(byteBuffer.array());
    }

}
