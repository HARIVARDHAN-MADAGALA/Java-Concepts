package org.example.threads.DeadLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/// instead of lock1.lock we gave like if(lock1.tryLock()) so if lock is not locked then it will become true and
/// execute the code or it will skip the block and move forward instead of waiting for thread to unlock.

public class Solution_for_deadlocks {

    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            if(lock1.tryLock()) {
                try {
                    System.out.println("T1: Holding lock1");

                    sleep(100);

                    System.out.println("T1: Waiting for lock2");
                    if(lock2.tryLock()) { // BLOCKS if T2 holds it
                        try {
                            System.out.println("T1: Acquired lock2");
                        } finally {
                            lock2.unlock();
                        }
                    }

                } finally {
                    lock1.unlock();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            if(lock2.tryLock()){
            try {
                System.out.println("T2: Holding lock2");

                sleep(100);

                System.out.println("T2: Waiting for lock1");
                if (lock1.tryLock()) { // BLOCKS if T1 holds it
                    try {
                        System.out.println("T2: Acquired lock1");
                    } finally {
                        lock1.unlock();
                    }
                }

                } finally{
                    lock2.unlock();
                }
            }
        });

        t1.start();
        t2.start();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
