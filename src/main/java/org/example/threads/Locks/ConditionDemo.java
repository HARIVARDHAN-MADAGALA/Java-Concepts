package org.example.threads.Locks;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================
 *  Condition (await / signal)
 * ============================================================
 *
 *  PROBLEM: With synchronized you have wait() / notify()
 *  → wait()   = release lock and wait
 *  → notify() = wake up ONE waiting thread
 *  → But tied to Object monitor — crude, no flexibility
 *
 *  SOLUTION: Lock + Condition
 *  → condition.await()  = same as wait()   but on a specific condition
 *  → condition.signal() = same as notify() but targeted
 *  → You can have MULTIPLE conditions on ONE lock (powerful!)
 *
 *  Classic use case: Producer-Consumer (Bounded Buffer)
 *  → Producer waits when buffer is FULL
 *  → Consumer waits when buffer is EMPTY
 *  → Each has its OWN condition → no spurious wakeups
 * ============================================================
 *
 *  Condition vs wait/notify:
 *  ┌─────────────────┬──────────────┬─────────────────────┐
 *  │ Feature         │ wait/notify  │ Condition            │
 *  ├─────────────────┼──────────────┼─────────────────────┤
 *  │ Multiple waits  │ ❌ one       │ ✅ many per lock     │
 *  │ Targeted signal │ ❌ random    │ ✅ specific condition │
 *  │ Works with      │ synchronized │ ReentrantLock        │
 *  └─────────────────┴──────────────┴─────────────────────┘
 */
public class ConditionDemo {

    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();

    private static final Lock lock = new ReentrantLock();

    // TWO separate conditions on ONE lock — this is the power!
    private static final Condition notFull  = lock.newCondition(); // producer waits here
    private static final Condition notEmpty = lock.newCondition(); // consumer waits here

    // -------------------------------------------------------
    //  PRODUCER — adds items to buffer
    //  Waits when buffer is FULL
    // -------------------------------------------------------
    static void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            // Wait while buffer is full
            while (buffer.size() == BUFFER_SIZE) {
                System.out.println("🔴 Producer WAITING — buffer full (" + buffer.size() + "/" + BUFFER_SIZE + ")");
                notFull.await();  // releases lock and waits
                // → woken up by consumer calling notFull.signal()
            }

            buffer.add(item);
            System.out.println("✅ Produced: " + item + "  | Buffer: " + buffer);

            // Signal consumer that buffer is no longer empty
            notEmpty.signal();  // targeted — only wakes up a consumer
        } finally {
            lock.unlock();
        }
    }

    // -------------------------------------------------------
    //  CONSUMER — takes items from buffer
    //  Waits when buffer is EMPTY
    // -------------------------------------------------------
    static int consume() throws InterruptedException {
        lock.lock();
        try {
            // Wait while buffer is empty
            while (buffer.isEmpty()) {
                System.out.println("🔴 Consumer WAITING — buffer empty");
                notEmpty.await();  // releases lock and waits
                // → woken up by producer calling notEmpty.signal()
            }

            int item = buffer.poll();
            System.out.println("🛒 Consumed: " + item + " | Buffer: " + buffer);

            // Signal producer that buffer has space now
            notFull.signal();  // targeted — only wakes up a producer

            return item;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("====== Condition (Producer-Consumer) Demo ======\n");
        System.out.println("Buffer capacity: " + BUFFER_SIZE + "\n");

        // Producer: produces 10 items with a small delay
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    Thread.sleep(100); // produce every 100ms
                    produce(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer");

        // Consumer: consumes items with a slightly longer delay (makes buffer fill up)
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    Thread.sleep(250); // consume every 250ms (slower than producer)
                    consume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        System.out.println("\n✅ All items produced and consumed!");
        System.out.println("\nKey takeaway:");
        System.out.println("  → Producer used notFull.await() when buffer was full");
        System.out.println("  → Consumer used notEmpty.await() when buffer was empty");
        System.out.println("  → Two SEPARATE conditions on ONE lock = precise control");
    }
}
