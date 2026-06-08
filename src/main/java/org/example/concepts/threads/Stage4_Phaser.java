package org.example.concepts.threads;

import java.util.concurrent.Phaser;

/// Phaser — the most flexible synchronization barrier (Java 7+)
///
/// Combines CountDownLatch + CyclicBarrier + dynamic party registration
///
/// Key concepts:
///   phase     — current round number (starts at 0, increments after each barrier)
///   party     — a thread registered with the phaser
///   arrive()      → signal arrival WITHOUT waiting (non-blocking)
///   arriveAndAwaitAdvance() → arrive AND wait for all parties (like CyclicBarrier.await())
///   arriveAndDeregister()   → arrive and remove yourself as a party (dynamic!)
///   register() / bulkRegister(n) → add parties at runtime
///
/// Real-world: multi-phase algorithms, tasks that join/leave mid-computation, fork-join patterns

public class Stage4_Phaser {

    public static void main(String[] args) throws InterruptedException {

        // ══ Part 1: Multi-phase pipeline (all threads, all phases) ══
        System.out.println("══ Part 1: Multi-phase pipeline ══\n");

        Phaser phaser = new Phaser(1); // register main thread as 1 party

        for (int i = 1; i <= 3; i++) {
            int id = i;
            phaser.register(); // register each worker before starting
            Thread.ofVirtual().start(() -> {
                try {
                    // Phase 0 — Load data
                    Thread.sleep(id * 80L);
                    System.out.println("  Worker-" + id + " loaded data  [phase " + phaser.getPhase() + "]");
                    phaser.arriveAndAwaitAdvance(); // wait for all → phase advances to 1

                    // Phase 1 — Process data
                    Thread.sleep(id * 60L);
                    System.out.println("  Worker-" + id + " processed    [phase " + phaser.getPhase() + "]");
                    phaser.arriveAndAwaitAdvance(); // wait for all → phase advances to 2

                    // Phase 2 — Write results
                    Thread.sleep(id * 40L);
                    System.out.println("  Worker-" + id + " wrote results [phase " + phaser.getPhase() + "]");
                    phaser.arriveAndDeregister(); // done — deregister from phaser
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // main thread drives each phase barrier
        phaser.arriveAndAwaitAdvance(); // end of phase 0
        System.out.println("── Phase 0 complete ──\n");
        phaser.arriveAndAwaitAdvance(); // end of phase 1
        System.out.println("── Phase 1 complete ──\n");
        phaser.arriveAndAwaitAdvance(); // end of phase 2
        System.out.println("── Phase 2 complete ──\n");

        phaser.arriveAndDeregister(); // main deregisters — phaser is now terminated

        Thread.sleep(300);

        // ══ Part 2: Dynamic registration — threads join/leave mid-computation ══
        System.out.println("══ Part 2: Dynamic party registration ══\n");

        Phaser dynamic = new Phaser(1); // only main registered initially

        // late-joining workers
        for (int i = 1; i <= 4; i++) {
            int id = i;
            Thread.ofVirtual().start(() -> {
                dynamic.register(); // join dynamically — even after phaser started
                try { Thread.sleep(id * 50L); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("  DynWorker-" + id + " done, deregistering");
                dynamic.arriveAndDeregister(); // leave — reduce party count
            });
        }

        Thread.sleep(100);
        System.out.println("Registered parties: " + dynamic.getRegisteredParties());
        dynamic.arriveAndDeregister(); // main leaves — if all others also left, phaser terminates
        Thread.sleep(400);
        System.out.println("Phaser terminated: " + dynamic.isTerminated());

        // ── Phaser vs CyclicBarrier ──
        // CyclicBarrier: fixed parties, reusable, simple
        // Phaser        : dynamic parties, multi-phase, onAdvance() hook, terminates gracefully
    }
}
