package org.example.threads.BlockingQueue;

import java.util.concurrent.*;

/// Stage 3 — Producer-Consumer pattern (real-world wiring)
///
/// BlockingQueue is THE canonical solution to producer-consumer:
///   - No manual wait/notify
///   - No synchronized blocks
///   - Backpressure built-in (bounded queue blocks fast producers)
///   - Thread-safe by design
///
/// This stage shows:
///   1. Classic single-producer / multi-consumer with poison pill shutdown
///   2. How ExecutorService uses LinkedBlockingQueue as its work queue internally

public class Stage3_ProducerConsumer {

    record Order(int id, String item) {
        static final Order POISON = new Order(-1, "STOP"); // sentinel for graceful shutdown
    }

    // ── Producer ──
    static class Producer implements Runnable {
        private final BlockingQueue<Order> queue;
        private final int consumerCount;

        Producer(BlockingQueue<Order> queue, int consumerCount) {
            this.queue         = queue;
            this.consumerCount = consumerCount;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 10; i++) {
                    Order o = new Order(i, "Product-" + i);
                    queue.put(o);
                    System.out.println("Produced : " + o.item() + "  queue=" + queue.size());
                    Thread.sleep(80);
                }
                // send one poison pill per consumer to shut them all down
                for (int i = 0; i < consumerCount; i++) queue.put(Order.POISON);
                System.out.println("Producer : sent " + consumerCount + " poison pills");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ── Consumer ──
    static class Consumer implements Runnable {
        private final int id;
        private final BlockingQueue<Order> queue;

        Consumer(int id, BlockingQueue<Order> queue) {
            this.id    = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Order o = queue.take(); // blocks when empty — no spin/wait
                    if (o == Order.POISON) {
                        System.out.println("Consumer-" + id + ": received poison pill, stopping");
                        break;
                    }
                    System.out.println("  Consumer-" + id + " processed: " + o.item());
                    Thread.sleep(150); // consumers slower than producer → queue fills, backpressure kicks in
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // ══ Classic producer-consumer with bounded queue ══
        System.out.println("══ Producer-Consumer with ArrayBlockingQueue (capacity=4) ══\n");

        int consumerCount = 3;
        BlockingQueue<Order> queue = new ArrayBlockingQueue<>(4); // bounded → backpressure

        Thread producerThread = new Thread(new Producer(queue, consumerCount));
        Thread[] consumers    = new Thread[consumerCount];
        for (int i = 0; i < consumerCount; i++)
            consumers[i] = new Thread(new Consumer(i + 1, queue));

        for (Thread c : consumers) c.start();
        producerThread.start();

        producerThread.join();
        for (Thread c : consumers) c.join();

        // ══ How ExecutorService uses this internally ══
        System.out.println("\n══ ExecutorService work queue (same concept) ══");

        // ThreadPoolExecutor is just: pool of threads + BlockingQueue of Runnables
        // newFixedThreadPool uses LinkedBlockingQueue(unbounded) internally
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(5);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 2, 0L, TimeUnit.MILLISECONDS, workQueue
        );

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.submit(() -> {
                System.out.println("  Task-" + taskId + " running on " + Thread.currentThread().getName());
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
    }
}
