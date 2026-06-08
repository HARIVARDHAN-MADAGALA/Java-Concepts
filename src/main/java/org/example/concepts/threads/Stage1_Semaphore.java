package org.example.concepts.threads;

import java.util.concurrent.Semaphore;

/// Semaphore — controls how many threads can access a resource SIMULTANEOUSLY
///
/// Think of it as a pool of "permits":
///   acquire() → take a permit (blocks if none available)
///   release() → return a permit (unblocks a waiting thread)
///
/// Binary Semaphore (1 permit)  → acts like a mutex / lock
/// Counting Semaphore (N permits) → limits concurrency to N threads at once
///
/// Real-world: connection pool (max 3 DB connections), rate limiter, parking lot

public class Stage1_Semaphore {

    // ── simulates a DB connection pool with max 3 concurrent connections ──
    static class ConnectionPool {
        private final Semaphore semaphore;
        private final String name;

        ConnectionPool(String name, int maxConnections) {
            this.name      = name;
            this.semaphore = new Semaphore(maxConnections, true); // fair = FIFO ordering
        }

        void use(String threadName) throws InterruptedException {
            System.out.println(threadName + " waiting for connection... (available: " + semaphore.availablePermits() + ")");
            semaphore.acquire();                    // blocks if pool is full
            try {
                System.out.println(threadName + " ── got connection from [" + name + "]");
                Thread.sleep(500);                  // simulate DB work
            } finally {
                semaphore.release();                // ALWAYS release in finally
                System.out.println(threadName + " ── released connection");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ConnectionPool pool = new ConnectionPool("DB-Pool", 3); // max 3 at a time

        // ── 7 threads competing for 3 permits ──
        Thread[] threads = new Thread[7];
        for (int i = 0; i < threads.length; i++) {
            String name = "Thread-" + (i + 1);
            threads[i] = Thread.ofVirtual().unstarted(() -> {
                try { pool.use(name); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        // ── tryAcquire — non-blocking attempt ──
        System.out.println("\n── tryAcquire demo ──");
        Semaphore s = new Semaphore(1);
        s.acquire(); // take the only permit

        boolean got = s.tryAcquire(); // returns false immediately instead of blocking
        System.out.println("tryAcquire on full semaphore: " + got); // false
        s.release();
        got = s.tryAcquire();
        System.out.println("tryAcquire after release    : " + got); // true
        s.release();

        // ── key properties ──
        // Semaphore is NOT reentrant — same thread acquiring twice will deadlock itself
        // release() can be called by a DIFFERENT thread than the one that acquired (unlike locks)
        // This makes it useful for producer/consumer signaling
    }
}
