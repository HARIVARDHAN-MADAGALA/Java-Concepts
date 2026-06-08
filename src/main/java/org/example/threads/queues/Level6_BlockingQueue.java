package org.example.threads.queues;

import java.util.concurrent.*;

/// Level 6 — BlockingQueue (thread-safe queues)
///
/// BlockingQueue extends Queue with two blocking operations:
///   put(e)  → inserts, BLOCKS if full  (bounded queues)
///   take()  → removes, BLOCKS if empty
///
/// Three key implementations:
///
///   ArrayBlockingQueue  — fixed-size ring buffer, ONE lock (put+take share it)
///   LinkedBlockingQueue — linked nodes, TWO locks (putLock + takeLock → higher throughput)
///   SynchronousQueue    — ZERO capacity — put() blocks until another thread calls take()
///                         direct hand-off channel, no storage at all
///
/// Method summary:
///   put(e) / take()            → blocking (infinite wait)
///   offer(e, timeout, unit)    → blocking with timeout
///   offer(e) / poll()          → non-blocking (return false/null immediately)
///
/// SynchronousQueue is used by Executors.newCachedThreadPool() internally —
/// every submitted task MUST be picked up by a thread immediately or a new thread is spawned.

public class Level6_BlockingQueue {

    public static void main(String[] args) throws InterruptedException {

        // ══ Part 1: ArrayBlockingQueue — bounded, single lock ══
        System.out.println("══ ArrayBlockingQueue (capacity=3) ══");
        BlockingQueue<String> abq = new ArrayBlockingQueue<>(3);
        abq.put("A"); abq.put("B"); abq.put("C");
        System.out.println("Full queue: " + abq);

        // offer with timeout — won't block forever
        boolean inserted = abq.offer("D", 100, TimeUnit.MILLISECONDS);
        System.out.println("offer(D) on full (100ms timeout): " + inserted); // false

        System.out.println("take(): " + abq.take()); // A — unblocks space
        inserted = abq.offer("D", 100, TimeUnit.MILLISECONDS);
        System.out.println("offer(D) after take()           : " + inserted); // true

        // ══ Part 2: LinkedBlockingQueue — bounded, two locks ══
        System.out.println("\n══ LinkedBlockingQueue (capacity=5) ══");
        BlockingQueue<Integer> lbq = new LinkedBlockingQueue<>(5);

        // producer and consumer running simultaneously — two-lock design lets them not block each other
        Thread producer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    lbq.put(i);
                    System.out.println("  put(" + i + ")  size=" + lbq.size());
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    int val = lbq.take();
                    System.out.println("  take() → " + val + "  size=" + lbq.size());
                    Thread.sleep(80);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        producer.start(); consumer.start();
        producer.join();  consumer.join();

        // ══ Part 3: SynchronousQueue — zero capacity, direct hand-off ══
        System.out.println("\n══ SynchronousQueue — zero capacity ══");
        SynchronousQueue<String> sq = new SynchronousQueue<>();

        // put() blocks until a thread calls take()
        Thread sender = Thread.ofVirtual().unstarted(() -> {
            try {
                System.out.println("Sender: waiting to hand off...");
                sq.put("Hello from sender"); // blocks until receiver calls take()
                System.out.println("Sender: hand-off complete");
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread receiver = Thread.ofVirtual().unstarted(() -> {
            try {
                Thread.sleep(300); // arrive late — sender must wait
                String msg = sq.take();
                System.out.println("Receiver: received → " + msg);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        sender.start(); receiver.start();
        sender.join();  receiver.join();

        // SynchronousQueue.size() is ALWAYS 0 — it holds nothing
        System.out.println("SynchronousQueue.size() = " + sq.size()); // always 0

        // ── which to choose ──
        // ArrayBlockingQueue  → cap memory, apply backpressure, fair ordering option
        // LinkedBlockingQueue → high-throughput producer-consumer, thread pool work queues
        // SynchronousQueue    → direct hand-off, newCachedThreadPool, CSP-style messaging
    }
}
