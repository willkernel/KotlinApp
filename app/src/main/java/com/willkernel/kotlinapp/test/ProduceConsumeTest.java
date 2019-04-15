package com.willkernel.kotlinapp.test;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by willkernel
 * on 2019/4/9.
 */
public class ProduceConsumeTest {
    private final int MAX_SIZE = 10;
    private final LinkedList<Object> list = new LinkedList<Object>();
    private       LinkedList<Object> mList    = new LinkedList<Object>();
    private ReentrantLock mLock    = new ReentrantLock();
    private Condition mEmpty   = mLock.newCondition();
    private LinkedBlockingQueue<Object> blockingQueue     = new LinkedBlockingQueue<Object>(MAX_SIZE);
    private       Condition          mFull    = mLock.newCondition();
    public void produce2() {
        if (blockingQueue.size() == MAX_SIZE) {
            System.out.println("缓冲区已满，暂停生产");
        }

        try {
            blockingQueue.put(new Object());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("生产了一个产品，现容量为：" + blockingQueue.size());
    }

    public void consume2() {
        if (blockingQueue.size() == 0) {
            System.out.println("缓冲区为空，暂停消费");
        }

        try {
            blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("消费了一个产品，现容量为：" + blockingQueue.size());
    }

    private void produce() {
        synchronized (list) {
            while (list.size() == MAX_SIZE) {
                System.out.println("仓库已满：生产暂停");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            list.add(new Object());
            System.out.println("生产了一个新产品，现库存为：" + list.size());
            list.notifyAll();
        }
    }

    private void consume() {
        synchronized (list) {
            while (list.size() == 0) {
                System.out.println("仓库已空：生产");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            list.remove();
            System.out.println("消费了一个产品，现库存为：" + list.size());
            list.notifyAll();
        }
    }
    public void produce1() {
        mLock.lock();
        while (mList.size() == MAX_SIZE) {
            System.out.println("缓冲区满，暂停生产");
            try {
                mFull.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mList.add(new Object());
        System.out.println("生产了一个新产品，现容量为：" + mList.size());
        mEmpty.signalAll();

        mLock.unlock();
    }
    public void consume1() {
        mLock.lock();
        while (mList.size() == 0) {
            System.out.println("缓冲区为空，暂停消费");
            try {
                mEmpty.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mList.remove();
        System.out.println("消费了一个产品，现容量为：" + mList.size());
        mFull.signalAll();

        mLock.unlock();
    }
    public static void main(String[] args) {
        ProduceConsumeTest test = new ProduceConsumeTest();
        while (true) {
            test.produce2();
            test.consume2();
        }
    }
}
