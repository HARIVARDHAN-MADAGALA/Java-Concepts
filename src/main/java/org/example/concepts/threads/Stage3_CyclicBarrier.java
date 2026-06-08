package org.example.concepts.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/// CyclicBarrier — all threads meet at a barrier point, then continue together
///
/// Every thread calls await() — the LAST one to arrive releases ALL of them
/// Optional barrier action runs once when all threads arrive (on the last thread)
///
/// REUSABLE ("Cyclic") — automatically resets after each cycle
/// → perfect for iterative algorithms where all threads must sync each round
///
/// Breaks if: a thread is interrupted or times out → BrokenBarrierException for all waiting threads
///
/// Real-world: parallel matrix computation phases, game loop sync, batch processing rounds

public class Stage3_CyclicBarrier {

    // ── simulates parallel workers processing data in rounds ──
    static void runWorkers(CyclicBarrier barrier, int rounds) {
        int parties = barrier.getParties();
        for (int i = 1; i <= parties; i++) {
            int workerId = i;
            Thread.ofVirtual().start(() -> {
                try {
                    for (int round = 1; round <= rounds; round++) {
                        // simulate variable-length work
                        Thread.sleep(workerId * 80L);
                        System.out.println("  Worker-" + workerId + " done with round " + round);

                        barrier.await(); // wait for everyone — then barrier action fires
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // ══ Basic barrier — 3 threads, 2 rounds ══
        System.out.println("══ CyclicBarrier: 3 workers, 2 rounds ══\n");

        int[] roundCounter = {0};

        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            // barrier action — runs once per cycle on the arriving thread
            roundCounter[0]++;
            System.out.println("── All workers reached barrier — round " + roundCounter[0] + " complete ──\n");
        });

        runWorkers(barrier, 2);

        Thread.sleep(2000); // let all rounds complete

        // ══ BrokenBarrier demo ══
        System.out.println("══ BrokenBarrier demo ══");
        CyclicBarrier fragile = new CyclicBarrier(3);

        Thread t1 = Thread.ofVirtual().unstarted(() -> {
            try { fragile.await(); }
            catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("  t1 caught: " + e.getClass().getSimpleName()); // BrokenBarrierException
            }
        });

        Thread t2 = Thread.ofVirtual().unstarted(() -> {
            try {
                Thread.sleep(100);
                fragile.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("  t2 caught: " + e.getClass().getSimpleName()); // BrokenBarrierException
            }
        });

        t1.start(); t2.start();
        Thread.sleep(50);
        t1.interrupt(); // interrupting one breaks the barrier for ALL waiting threads

        t1.join(); t2.join();
        System.out.println("  barrier.isBroken() = " + fragile.isBroken()); // true
        fragile.reset(); // can manually reset a broken barrier
        System.out.println("  after reset: barrier.isBroken() = " + fragile.isBroken()); // false

        // ── key differences from CountDownLatch ──
        // CyclicBarrier : ALL threads call await(), reusable, has barrier action
        // CountDownLatch: countDown() and await() are separate, single-use, no barrier action
    }
}
