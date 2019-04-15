package com.willkernel.kotlinapp.test;


/**
 * Created by willkernel
 * on 2019/4/13.
 */
public class LockTest {
    public static void main(String[] args) {
//        staticLockOrderDeadLock.a();
//        staticLockOrderDeadLock.b();

        Account account=new Account();
        new Thread(new Runnable() {
            @Override
            public void run() {
        StaticLockOrderDeadLock staticLockOrderDeadLock = new StaticLockOrderDeadLock();
                staticLockOrderDeadLock.a();


                DynamicLockOrderDeadLock dynamicLockOrderDeadLock=new DynamicLockOrderDeadLock();
                dynamicLockOrderDeadLock.transefMoney(account,account,10d);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {

        StaticLockOrderDeadLock staticLockOrderDeadLock = new StaticLockOrderDeadLock();
                staticLockOrderDeadLock.b();

                DynamicLockOrderDeadLock dynamicLockOrderDeadLock=new DynamicLockOrderDeadLock();
                dynamicLockOrderDeadLock.transefMoney(account,account,10d);
            }
        }).start();


    }
}

class StaticLockOrderDeadLock {
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    public void a() {
        synchronized (lockA) {
            synchronized (lockB) {
                System.out.println("function a");
            }
        }
    }

    public void b() {
        synchronized (lockB) {
            synchronized (lockA) {
                System.out.println("function b");
            }
        }
    }
}

//可能发生动态锁顺序死锁的代码
class DynamicLockOrderDeadLock {
    public void transefMoney(Account fromAccount, Account toAccount, Double amount) {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                //...
                fromAccount.minus(amount);
                toAccount.add(amount);
                //...
            }
        }
    }
}

//正确的代码
class DynamicLockOrderDeadLock1 {
    private final Object myLock = new Object();

    public void transefMoney(final Account fromAccount, final Account toAccount, final Double amount) {
        class Helper {
            public void transfer() {
                //...
                fromAccount.minus(amount);
                toAccount.add(amount);
                //...
            }
        }
        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);

        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (myLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }

    }
}

class Account {

    public void minus(Double amount) {
        amount--;
    }

    public void add(Double amount) {
        amount++;
    }
}