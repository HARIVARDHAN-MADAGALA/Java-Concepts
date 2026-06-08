package org.example.threads.virtualthreads;

/// Stage 1 — What are Virtual Threads?
///
/// Before Java 21:
///   Thread = OS thread (platform thread). Creating 10,000 of them = 10,000 OS threads.
///   Each OS thread needs ~1MB stack → 10k threads = ~10GB RAM. Not scalable.
///   Blocking I/O (DB call, HTTP, file read) → OS thread sits idle, wasting a kernel resource.
///
/// Virtual Threads (Project Loom, Java 21 GA):
///   Lightweight threads managed by the JVM, NOT the OS.
///   Millions of virtual threads can run on a handful of OS "carrier" threads.
///   When a virtual thread BLOCKS (I/O, sleep, lock) → JVM unmounts it from the carrier thread.
///   Carrier thread is FREE to run another virtual thread. No OS thread wasted.
///
/// Mental model:
///   Platform thread = expensive hotel room  (OS owns it, always occupied)
///   Virtual thread  = reservation ticket    (JVM manages it, carrier only used when actually running)
///
/// Three ways to create a virtual thread:
///   1. Thread.ofVirtual().start(runnable)
///   2. Thread.startVirtualThread(runnable)
///   3. Executors.newVirtualThreadPerTaskExecutor()  ← recommended for production

public class Stage1_WhatAreVirtualThreads {

    public static void main(String[] args) throws InterruptedException {

        // ── Way 1: Thread.ofVirtual() ──
        System.out.println("── Thread.ofVirtual() ──");
        Thread vt = Thread.ofVirtual()
            .name("my-virtual-thread")
            .start(() -> System.out.println("Running on: " + Thread.currentThread()));
        vt.join();

        // ── Way 2: Thread.startVirtualThread() — shorthand ──
        System.out.println("\n── Thread.startVirtualThread() ──");
        Thread vt2 = Thread.startVirtualThread(() ->
            System.out.println("isVirtual: " + Thread.currentThread().isVirtual()) // true
        );
        vt2.join();

        // ── Way 3: Executor (production recommended) ──
        System.out.println("\n── Executors.newVirtualThreadPerTaskExecutor() ──");
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                int id = i;
                executor.submit(() ->
                    System.out.println("Task-" + id + " → " + Thread.currentThread())
                );
            }
        } // auto-shutdown (ExecutorService implements AutoCloseable in Java 19+)

        // ── platform vs virtual ──
        System.out.println("\n── Platform vs Virtual comparison ──");
        Thread platform = Thread.ofPlatform().name("platform-1").unstarted(() -> {});
        Thread virtual  = Thread.ofVirtual().name("virtual-1").unstarted(() -> {});
        System.out.println("Platform isVirtual: " + platform.isVirtual()); // false
        System.out.println("Virtual  isVirtual: " + virtual.isVirtual());  // true

        // ── scale test: 10,000 virtual threads — would OOM with platform threads ──
        System.out.println("\n── 10,000 virtual threads ──");
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10_000];
        for (int i = 0; i < 10_000; i++) {
            threads[i] = Thread.startVirtualThread(() -> {
                try { Thread.sleep(100); } // each blocks for 100ms
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        for (Thread t : threads) t.join();
        System.out.println("10,000 virtual threads done in " + (System.currentTimeMillis() - start) + "ms");
        // Platform threads: would need ~10GB RAM, likely crash or be very slow
        // Virtual threads: run on a small carrier pool, ~100ms total
    }
}
