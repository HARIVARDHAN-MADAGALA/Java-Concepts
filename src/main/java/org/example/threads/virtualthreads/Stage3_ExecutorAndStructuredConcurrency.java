package org.example.threads.virtualthreads;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/// Stage 3 — Virtual Threads with ExecutorService (production pattern)
///
/// Executors.newVirtualThreadPerTaskExecutor():
///   - Creates a NEW virtual thread for EVERY submitted task
///   - No thread pool — virtual threads are cheap enough to not need pooling
///   - This is the recommended pattern for I/O-bound workloads in Java 21+
///
/// Why NOT pool virtual threads?
///   Platform threads are expensive → pool them and reuse.
///   Virtual threads are cheap (~few KB) → create and discard per task is fine.
///   Pooling virtual threads adds overhead with no benefit.
///
/// Structured Concurrency (Java 21 preview → stable in Java 24):
///   StructuredTaskScope — scopes a group of virtual threads to a parent task lifetime.
///   Two built-in policies:
///     ShutdownOnFailure  — cancel all if ANY subtask fails
///     ShutdownOnSuccess  — cancel all as soon as ANY subtask succeeds (first-wins)

public class Stage3_ExecutorAndStructuredConcurrency {

    // ── simulates an I/O-bound operation (DB call, HTTP request) ──
    static String fetchData(String source, long delayMs) throws InterruptedException {
        Thread.sleep(delayMs); // virtual thread unmounts here — carrier is free
        return "data-from-" + source;
    }

    public static void main(String[] args) throws Exception {

        // ══ Part 1: newVirtualThreadPerTaskExecutor ══
        System.out.println("══ newVirtualThreadPerTaskExecutor ══\n");

        long start = System.currentTimeMillis();
        List<Future<String>> futures = new ArrayList<>();

        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            // submit 10 I/O-bound tasks — each gets its own virtual thread
            for (int i = 1; i <= 10; i++) {
                String source = "service-" + i;
                futures.add(exec.submit(() -> fetchData(source, 300)));
            }
            // executor closes here → waits for all tasks
        }

        for (Future<String> f : futures) System.out.println("  " + f.get());
        System.out.println("10 tasks (300ms each) done in: " + (System.currentTimeMillis() - start) + "ms");
        // ~300ms total — all run concurrently on virtual threads, not 10*300=3000ms

        // ══ Part 2: Structured Concurrency — ShutdownOnFailure ══
        // All subtasks share the same lifetime as the parent scope.
        // If any subtask throws → scope cancels the rest → no orphaned threads.
        System.out.println("\n══ StructuredTaskScope.ShutdownOnFailure ══\n");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            StructuredTaskScope.Subtask<String> db   = scope.fork(() -> fetchData("DB",    200));
            StructuredTaskScope.Subtask<String> cache = scope.fork(() -> fetchData("Cache", 100));
            StructuredTaskScope.Subtask<String> api  = scope.fork(() -> fetchData("API",   150));

            scope.join();           // wait for all (or until one fails)
            scope.throwIfFailed();  // rethrow if any subtask threw

            System.out.println("DB    : " + db.get());
            System.out.println("Cache : " + cache.get());
            System.out.println("API   : " + api.get());
        }

        // ══ Part 3: StructuredTaskScope.ShutdownOnSuccess — first-wins ══
        System.out.println("\n══ StructuredTaskScope.ShutdownOnSuccess (first-wins) ══\n");

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> fetchData("slow-replica",  500));
            scope.fork(() -> fetchData("fast-replica",  100)); // this wins
            scope.fork(() -> fetchData("medium-replica",300));

            scope.join(); // returns when FIRST subtask succeeds → others cancelled
            System.out.println("Winner: " + scope.result()); // data-from-fast-replica
        }

        // ── key rule: one virtual thread per task, not per connection/resource ──
        // Don't do: Executors.newFixedThreadPool(N) with virtual threads inside
        // Do:        Executors.newVirtualThreadPerTaskExecutor() — one VT per task
    }
}
