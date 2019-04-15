package com.willkernel.kotlinapp.test;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by willkernel
 * on 2019/4/13.
 */
public class ChannelTest {
    public static void main(String[] args) {

        try {
//            RandomAccessFile accessFile = new RandomAccessFile("./raf.txt", "rw");
            FileInputStream accessFile = new FileInputStream("./raf.txt");
            FileChannel fileChannel = accessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(48);
            int byteRead;
            while ((byteRead = fileChannel.read(byteBuffer)) != -1) {
                System.out.println("length " + byteRead);
                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    System.out.println(byteBuffer.get());
                }
                byteBuffer.clear();
            }
            accessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayBlockingQueue<Integer> queue=new ArrayBlockingQueue(Integer.MAX_VALUE>>2);
        try {
            for (int i = 0; i < queue.size(); i++) {

            queue.put(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
