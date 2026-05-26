package org.example.threads.Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================
 *  ReentrantLock - 3 ways to use it
 * ============================================================
 *
 *  Why ReentrantLock over synchronized?
 *  ------------------------------------------------------------
 *  synchronized  → simple but no control (can't timeout, can't check)
 *  ReentrantLock → full control: tryLock, timeout, fairness
 *
 *  KEY RULE: ALWAYS unlock in finally block — JVM won't save you!
 * ============================================================
 */
public class ReentrantLockDemo {

    static int count = 0;

    // ✅ IMPORTANT: lock must be static — shared across all threads
    // If not static → each thread gets its OWN lock → race condition again!
    static Lock lock = new ReentrantLock();

    // -------------------------------------------------------
    //  Demo 1: Basic lock() / unlock()
    //  → Blocks until lock is available. No timeout. Simple.
    // -------------------------------------------------------
    static void basicLock() throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock();          // blocks until it gets the lock
                try {
                    count++;           // critical section
                } finally {
                    lock.unlock();     // ALWAYS release in finally
                }
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("✅ [basicLock] Expected: 20000, Got: " + count);
        count = 0; // reset for next demo
    }

    // -------------------------------------------------------
    //  Demo 2: tryLock()
    //  → Does NOT block. Returns true if lock acquired, false if not.
    //  → Use case: "Don't wait — if busy, skip or do something else"
    // -------------------------------------------------------
    static void tryLockDemo() throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                boolean acquired = lock.tryLock(); // non-blocking
                if (acquired) {
                    try {
                        System.out.println(Thread.currentThread().getName()
                                + " ✅ got lock, doing work...");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // lock was busy → don't wait, just skip
                    System.out.println(Thread.currentThread().getName()
                            + " ❌ lock busy, skipping this iteration");
                }
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        t1.start(); t2.start();
        t1.join();  t2.join();
    }

    // -------------------------------------------------------
    //  Demo 3: tryLock(timeout)
    //  → Waits up to N time units. If still locked → gives up.
    //  → Use case: "Try for 2 seconds, then fail fast" (API calls, DB)
    // -------------------------------------------------------
    static void tryLockWithTimeout() throws InterruptedException {
        // Thread 1 holds lock for 3 seconds
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("T1 → Holding lock for 3 seconds...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println("T1 → Released lock");
            }
        }, "T1");

        // Thread 2 tries to get lock but only waits 1 second
        Thread t2 = new Thread(() -> {
            try {
                System.out.println("T2 → Trying to acquire lock (will wait max 1 sec)...");
                boolean acquired = lock.tryLock(1, TimeUnit.SECONDS);
                if (acquired) {
                    try {
                        System.out.println("T2 ✅ Got the lock!");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // ✅ Fails fast — system stays alive!
                    System.out.println("T2 ❌ Couldn't get lock in 1 sec → giving up (fail fast)");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "T2");

        t1.start();
        Thread.sleep(100); // make sure T1 grabs lock first
        t2.start();
        t1.join(); t2.join();
    }

    // -------------------------------------------------------
    //  Demo 4: Fairness
    //  → new ReentrantLock(true) → threads get lock in order they waited
    //  → new ReentrantLock()     → no guarantee (faster but unfair)
    // -------------------------------------------------------
    static void fairnessDemo() throws InterruptedException {
        Lock fairLock = new ReentrantLock(true); // true = fair mode

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                fairLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " → got fair lock");
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    fairLock.unlock();
                }
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        Thread t3 = new Thread(task, "T3");
        t1.start(); t2.start(); t3.start();
        t1.join();  t2.join();  t3.join();

        System.out.println("✅ Fairness demo done — notice threads take turns!");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== Demo 1: Basic lock/unlock ==========");
        basicLock();

        System.out.println("\n========== Demo 2: tryLock() (non-blocking) ==========");
        tryLockDemo();

        System.out.println("\n========== Demo 3: tryLock(timeout) ==========");
        tryLockWithTimeout();

        System.out.println("\n========== Demo 4: Fairness Mode ==========");
        fairnessDemo();
    }
}
