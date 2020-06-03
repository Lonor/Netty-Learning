package com.learning.nio;

import java.nio.ByteBuffer;

/**
 * @author Lawrence
 * @date 2020/6/3
 */
public class ReadOnlyBufferTest {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        for (int i = 0; i < 15; i++) {
            byteBuffer.put((byte) i);
        }
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            System.out.println(byteBuffer.get());
        }
        byteBuffer.flip();
        ByteBuffer byteBufferReadOnly = byteBuffer.asReadOnlyBuffer();
        byteBufferReadOnly.put((byte) 1);

    }
}
