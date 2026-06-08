package org.example.concepts.threads;

import java.util.concurrent.CountDownLatch;

/// CountDownLatch — one-shot gate: wait until N events have happened
///
/// Initialized with a count N.
/// countDown() → decrements the count (can be called from any thread)
/// await()     → blocks until count reaches 0
///
/// SINGLE USE — cannot be reset. Once count hits 0, it stays 0 forever.
///
/// Two classic patterns:
///   1. Starting gun  — 1 latch, main thread calls countDown(), workers await() to start together
///   2. Completion gate — N latch, each worker calls countDown() when done, main await()s all
///
/// Real-world: integration test setup, parallel service initialization, fan-out/fan-in

public class Stage2_CountDownLatch {

    public static void main(String[] args) throws InterruptedException {

        // ══ Pattern 1: Completion Gate ══
        // main thread waits for all workers to finish
        System.out.println("══ Pattern 1: Completion Gate ══");
        int workerCount = 5;
        CountDownLatch done = new CountDownLatch(workerCount);

        for (int i = 1; i <= workerCount; i++) {
            int id = i;
            Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(id * 100L);
                    System.out.println("  Worker-" + id + " finished");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown(); // decrement — even if worker throws
                }
            });
        }

        done.await(); // blocks until all 5 workers call countDown()
        System.out.println("All workers done — proceeding\n");

        // ══ Pattern 2: Starting Gun ══
        // all threads wait for the signal, then race together (useful for load testing)
        System.out.println("══ Pattern 2: Starting Gun ══");
        CountDownLatch ready  = new CountDownLatch(3); // workers signal ready
        CountDownLatch start  = new CountDownLatch(1); // main fires the gun
        CountDownLatch finish = new CountDownLatch(3);

        for (int i = 1; i <= 3; i++) {
            int id = i;
            Thread.ofVirtual().start(() -> {
                try {
                    System.out.println("  Racer-" + id + " ready");
                    ready.countDown();   // signal: I'm ready
                    start.await();       // wait for starting gun
                    System.out.println("  Racer-" + id + " running!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finish.countDown();
                }
            });
        }

        ready.await();                          // wait until all racers are ready
        System.out.println("  All racers ready — FIRE!");
        start.countDown();                      // release all at once
        finish.await();
        System.out.println("Race finished\n");

        // ── CountDownLatch vs CyclicBarrier ──
        // CountDownLatch: one-shot, different threads can countDown() and await()
        // CyclicBarrier : reusable, ALL threads must call await() — they wait for each other
    }
}
