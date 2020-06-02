package com.learning.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Blocking I/O
 * <p>
 * We can use the Telnet as the client to send message:
 * `telnet 127.0.0.1 6666`
 *
 * @author Lawrence
 */
public class BioServer {

    // We had better create the thread pool.
    // When a connection comes in, just create a new thread to handle it.

    public static void main(String[] args) throws IOException {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        serverSocket = new ServerSocket(6666);
        System.out.println("Server starts ...");
        while (true) {
            try {
                System.out.println("Waiting for connection ...");
                final Socket socket = serverSocket.accept();
                System.out.println("A client connected ...");
                cachedThreadPool.execute(new Runnable() {
                    public void run() {
                        handle(socket);
                    }
                });
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * This method will be executed by every thread from the poll.
     *
     * @param socket socket
     */
    private static void handle(final Socket socket) {
        System.out.println("Thread - " + Thread.currentThread().getId());
        System.out.println("Receive from socket:");
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                System.out.println("Waiting for reading ...");
                // The thread is always blocking here which costs waste.
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Connection closed ...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
