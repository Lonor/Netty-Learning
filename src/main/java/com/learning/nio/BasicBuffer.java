package com.learning.nio;

import java.nio.IntBuffer;

/**
 * @author Lawrence
 * @date 2020/6/2
 */
public class BasicBuffer {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }
        // 读写切换
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

    }
}
