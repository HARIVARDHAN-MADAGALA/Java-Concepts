package org.example.threads.BlockingQueue;

import java.util.concurrent.LinkedBlockingQueue;

/// Stage 2 — LinkedBlockingQueue
///
/// Backed by a linked-node chain — optionally bounded (defaults to Integer.MAX_VALUE = unbounded)
/// Uses TWO separate locks: putLock (for tail) + takeLock (for head)
/// → producers and consumers can proceed SIMULTANEOUSLY unlike ArrayBlockingQueue's single lock
/// → better throughput under high concurrency
///
/// Best for: classic producer-consumer, task queues, thread pool work queues (used by ThreadPoolExecutor)
///
/// LinkedBlockingQueue IS the queue backing Executors.newFixedThreadPool() internally

public class Stage2_LinkedBlockingQueue {

    record Task(int id, String name) {}

    public static void main(String[] args) throws InterruptedException {

        // ── bounded LinkedBlockingQueue — behaves like ArrayBlockingQueue but with two locks ──
        LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>(10);

        // ── multiple producers ──
        for (int p = 1; p <= 2; p++) {
            int producerId = p;
            Thread.ofVirtual().start(() -> {
                try {
                    for (int i = 1; i <= 5; i++) {
                        Task t = new Task(producerId * 10 + i, "P" + producerId + "-Task" + i);
                        taskQueue.put(t);
                        System.out.println("Produced: " + t.name() + "  queueSize=" + taskQueue.size());
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // ── multiple consumers — two locks let head and tail operate independently ──
        for (int c = 1; c <= 3; c++) {
            int consumerId = c;
            Thread.ofVirtual().start(() -> {
                try {
                    for (int i = 0; i < 3; i++) {
                        Task t = taskQueue.take();
                        System.out.println("  Consumer-" + consumerId + " processed: " + t.name());
                        Thread.sleep(120);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        Thread.sleep(1500);

        // ── drainTo — bulk transfer, more efficient than repeated poll() ──
        System.out.println("\n── drainTo demo ──");
        LinkedBlockingQueue<String> source = new LinkedBlockingQueue<>();
        for (int i = 1; i <= 6; i++) source.put("msg-" + i);

        java.util.List<String> batch = new java.util.ArrayList<>();
        int drained = source.drainTo(batch, 4); // drain at most 4
        System.out.println("Drained " + drained + " items: " + batch);
        System.out.println("Remaining in queue: " + source.size());

        // ── unbounded usage — careful, can cause OutOfMemoryError if producer outpaces consumer ──
        System.out.println("\n── unbounded (default capacity = Integer.MAX_VALUE) ──");
        LinkedBlockingQueue<Integer> unbounded = new LinkedBlockingQueue<>(); // no capacity arg
        unbounded.offer(1); unbounded.offer(2); unbounded.offer(3);
        System.out.println("remainingCapacity: " + unbounded.remainingCapacity()); // ~MAX_VALUE - 3

        // ── key insight: two-lock design ──
        // putLock  guards: tail node + count increment
        // takeLock guards: head node + count decrement
        // → put() and take() can run truly in parallel (different locks)
        // → ArrayBlockingQueue uses ONE lock → put() and take() serialise each other
    }
}
