package org.example.threads.BlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/// Stage 1 — ArrayBlockingQueue
///
/// Backed by a fixed-size array — capacity is set at construction and NEVER changes
/// BOUNDED: put() blocks when full, take() blocks when empty
/// Internally uses a single ReentrantLock + two Conditions (notFull, notEmpty)
/// Optionally fair (FIFO ordering for waiting threads)
///
/// Best for: when you want to cap memory usage and apply backpressure on producers
///
/// Core methods:
///   put(e)   → insert, blocks if full
///   take()   → remove, blocks if empty
///   offer(e) → insert, returns false if full (non-blocking)
///   poll()   → remove, returns null if empty (non-blocking)
///   peek()   → inspect head without removing

public class Stage1_ArrayBlockingQueue {

    static final int CAPACITY = 5;

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(CAPACITY, true); // fair=true

        // ── Producer — fills the queue; blocks when full ──
        Thread producer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 1; i <= 8; i++) {
                    String item = "item-" + i;
                    System.out.println("Producer: putting  [" + item + "]  size=" + queue.size());
                    queue.put(item); // blocks at i=6 until consumer drains
                }
                System.out.println("Producer: done");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // ── Consumer — drains slowly to show backpressure ──
        Thread consumer = Thread.ofVirtual().unstarted(() -> {
            try {
                for (int i = 1; i <= 8; i++) {
                    Thread.sleep(200);               // slower than producer → queue fills up
                    String item = queue.take();      // blocks if empty
                    System.out.println("Consumer: took     [" + item + "]  size=" + queue.size());
                }
                System.out.println("Consumer: done");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // ── non-blocking variants ──
        System.out.println("\n── Non-blocking offer / poll ──");
        ArrayBlockingQueue<Integer> q = new ArrayBlockingQueue<>(3);
        System.out.println("offer(1): " + q.offer(1));
        System.out.println("offer(2): " + q.offer(2));
        System.out.println("offer(3): " + q.offer(3));
        System.out.println("offer(4) on full queue: " + q.offer(4)); // false — won't block
        System.out.println("peek()  : " + q.peek());                 // 1, queue unchanged
        System.out.println("poll()  : " + q.poll());                 // 1, removes head
        System.out.println("poll()  : " + q.poll());                 // 2
        System.out.println("poll()  : " + q.poll());                 // 3
        System.out.println("poll() on empty queue: " + q.poll());    // null — won't block

        // ── ArrayBlockingQueue vs LinkedBlockingQueue ──
        // Array: fixed capacity, single lock, slightly higher throughput for small queues
        // Linked: optionally bounded, two locks (head/tail), better for high-concurrency
    }
}
