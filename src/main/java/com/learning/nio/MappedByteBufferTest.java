package com.learning.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer 可让文件直接在堆外内存中修改，操作系统不需要拷贝一次
 *
 * @author Lawrence
 * @date 2020/6/3
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("./file01.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        // 读写模式，可直接修改的起始位置，映射到内存的大小（将文件的多少个字节映射到内存，此处即 0 - 5 ）

        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte) 'h');
        mappedByteBuffer.put(3, (byte) 65);
        fileChannel.close();
        randomAccessFile.close();
    }
}
