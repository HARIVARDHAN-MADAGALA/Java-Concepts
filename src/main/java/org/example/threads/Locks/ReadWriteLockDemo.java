package org.example.threads.Locks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ============================================================
 *  ReadWriteLock (ReentrantReadWriteLock)
 * ============================================================
 *
 *  PROBLEM with ReentrantLock:
 *  → Even if 10 threads just READING, only 1 gets in at a time
 *  → Reads are safe to do in parallel! No need to block them.
 *
 *  SOLUTION: ReadWriteLock
 *  → READ  lock  → multiple threads can hold it simultaneously ✅
 *  → WRITE lock  → exclusive, blocks all readers AND writers    ✅
 *
 *  Real-world use case: Cache, Config store, Shared data
 *  → Many threads READ config frequently
 *  → Rarely one thread WRITES/updates config
 * ============================================================
 *
 *  RULES:
 *  → Many readers  + no writer  → all readers go in parallel  ✅
 *  → One writer    + no readers → writer gets exclusive access ✅
 *  → Writer waiting → new readers also blocked (fairness)      ✅
 */
public class ReadWriteLockDemo {

    // Simulating a shared config/cache value
    private static String sharedData = "initial-config-value";

    // ReadWriteLock gives you two locks: readLock and writeLock
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // -------------------------------------------------------
    //  READ operation — safe to run concurrently
    // -------------------------------------------------------
    static void readData(String threadName) {
        rwLock.readLock().lock();  // multiple threads can hold this simultaneously
        try {
            System.out.println(threadName + " 📖 READ  → " + sharedData
                    + "  [active readers can overlap!]");
            Thread.sleep(500); // simulate read time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // -------------------------------------------------------
    //  WRITE operation — exclusive, blocks everyone
    // -------------------------------------------------------
    static void writeData(String threadName, String newValue) {
        rwLock.writeLock().lock();  // exclusive — blocks all readers and writers
        try {
            System.out.println(threadName + " ✏️  WRITE → updating to: " + newValue);
            Thread.sleep(800); // simulate write time
            sharedData = newValue;
            System.out.println(threadName + " ✏️  WRITE → done!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("====== ReadWriteLock Demo ======\n");

        // Scenario: 3 readers + 1 writer + 2 more readers
        // Watch → readers overlap with each other, but writer is exclusive

        Thread r1 = new Thread(() -> readData("Reader-1"), "Reader-1");
        Thread r2 = new Thread(() -> readData("Reader-2"), "Reader-2");
        Thread r3 = new Thread(() -> readData("Reader-3"), "Reader-3");

        Thread w1 = new Thread(() -> writeData("Writer-1", "updated-config-v2"), "Writer-1");

        Thread r4 = new Thread(() -> readData("Reader-4"), "Reader-4");
        Thread r5 = new Thread(() -> readData("Reader-5"), "Reader-5");

        // Start 3 readers simultaneously → they should all overlap
        r1.start(); r2.start(); r3.start();
        Thread.sleep(100); // let readers settle in

        // Writer comes in → blocks until all current readers finish
        w1.start();
        Thread.sleep(100);

        // These readers start after writer → they wait for writer to finish
        r4.start(); r5.start();

        r1.join(); r2.join(); r3.join();
        w1.join();
        r4.join(); r5.join();

        System.out.println("\n📌 Final value: " + sharedData);
        System.out.println("\nKey takeaway:");
        System.out.println("  → R1, R2, R3 ran SIMULTANEOUSLY (see timestamps overlap)");
        System.out.println("  → W1 waited for readers, then was EXCLUSIVE");
        System.out.println("  → R4, R5 waited for writer, then ran SIMULTANEOUSLY again");
    }
}
