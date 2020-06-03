package com.learning.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Lawrence
 * @date 2020/6/3
 */
public class NIOFileCopy {

    public static void main(String[] args) throws Exception {
        transferFile1();
        transferFile2();
    }

    public static void transferFile1() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("./file01.txt");
        FileChannel inputStreamChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("./file02.txt");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (true) {
            // this clear is important!
            byteBuffer.clear();

            int read = inputStreamChannel.read(byteBuffer);
            System.out.println("read: " + read);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            outputStreamChannel.write(byteBuffer);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public static void transferFile2() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("./file01.txt");
        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("./file03.txt");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();
        outputStreamChannel.transferFrom(inputStreamChannel, 0, inputStreamChannel.size());
        inputStreamChannel.close();
        outputStreamChannel.close();
        fileInputStream.close();
        fileOutputStream.close();

    }
}
