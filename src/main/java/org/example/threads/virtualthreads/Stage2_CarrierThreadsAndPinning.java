package org.example.threads.virtualthreads;

/// Stage 2 — Mounting, Unmounting and Carrier Threads
///
/// JVM maintains a small pool of OS (platform) threads called CARRIER threads.
/// Default carrier pool size = number of CPU cores (Runtime.getRuntime().availableProcessors())
///
/// Lifecycle of a virtual thread during blocking I/O:
///
///   1. Virtual thread is MOUNTED onto a carrier thread → runs on CPU
///   2. Virtual thread calls a blocking op (sleep, I/O, lock)
///   3. JVM UNMOUNTS the virtual thread → saves its stack on the heap
///   4. Carrier thread is FREE → picks up another runnable virtual thread
///   5. When blocking op completes → virtual thread is REMOUNTED (possibly on a different carrier)
///
/// This is called "continuation-based scheduling" (Project Loom internals).
///
/// PINNING — the one gotcha:
///   A virtual thread is PINNED (cannot be unmounted) when:
///     a. Inside a synchronized block/method
///     b. Inside a native method call
///   Pinned = carrier thread is BLOCKED too → defeats the purpose
///   Fix: replace synchronized with ReentrantLock
///
/// JVM flag to detect pinning: -Djdk.tracePinnedThreads=full

public class Stage2_CarrierThreadsAndPinning {

    static final Object lock = new Object();
    static final java.util.concurrent.locks.ReentrantLock reentrantLock =
        new java.util.concurrent.locks.ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        // ── show carrier thread info ──
        System.out.println("── Carrier thread pool size (= CPU cores) ──");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());

        // ── unmounting in action — virtual threads block, carrier is reused ──
        System.out.println("\n── Unmounting demo: 20 virtual threads, each blocking 200ms ──");
        System.out.println("(all finish in ~200ms total — carrier threads shared)");

        long start = System.currentTimeMillis();
        Thread[] vts = new Thread[20];
        for (int i = 0; i < 20; i++) {
            int id = i + 1;
            vts[i] = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(200); // unmounts from carrier → carrier free for others
                    System.out.println("VT-" + id + " done on " + Thread.currentThread());
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        for (Thread t : vts) t.join();
        System.out.println("Total time: " + (System.currentTimeMillis() - start) + "ms\n");
        // ~200ms, not 20*200=4000ms — carrier threads are shared across all 20

        // ── PINNING with synchronized — bad ──
        System.out.println("── Pinning: synchronized PINS the carrier thread ──");
        Thread pinned = Thread.ofVirtual().start(() -> {
            synchronized (lock) {          // virtual thread is now PINNED
                try {
                    Thread.sleep(100);     // carrier is also blocked — cannot serve others
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            System.out.println("Pinned VT done (carrier was blocked too)");
        });
        pinned.join();

        // ── FIXED with ReentrantLock — carrier is freed during lock wait ──
        System.out.println("\n── Fixed: ReentrantLock does NOT pin the carrier ──");
        Thread notPinned = Thread.ofVirtual().start(() -> {
            reentrantLock.lock();
            try {
                Thread.sleep(100);         // virtual thread unmounts, carrier is FREE
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { reentrantLock.unlock(); }
            System.out.println("Non-pinned VT done (carrier was free during sleep)");
        });
        notPinned.join();

        // ── virtual thread toString shows carrier info ──
        System.out.println("\n── Virtual thread toString ──");
        Thread.ofVirtual().start(() ->
            // format: VirtualThread[#id]/runnable@CarrierThreadName
            System.out.println(Thread.currentThread().toString())
        ).join();
    }
}
