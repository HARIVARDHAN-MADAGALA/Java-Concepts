package org.example.threads.virtualthreads;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/// Stage 4 — Virtual Threads vs Platform Threads + What NOT to do
///
/// Virtual threads shine for: I/O-bound, high-concurrency workloads
///   HTTP servers, DB queries, file I/O, microservice fan-out calls
///
/// Virtual threads do NOT help with: CPU-bound workloads
///   Sorting, encryption, image processing — CPU is the bottleneck, not threads
///   For CPU-bound → use ForkJoinPool / parallelStream (platform threads)
///
/// What NOT to do with virtual threads:
///   1. Don't pool them — defeats the purpose, adds overhead
///   2. Don't use synchronized for long critical sections — causes pinning
///   3. Don't use ThreadLocal for bulky objects — VT stack is on heap, but ThreadLocal
///      values accumulate if not removed (same memory leak risk as platform threads)
///   4. Don't expect them to speed up CPU-bound code

public class Stage4_VirtualVsPlatform {

    static final AtomicInteger completedTasks = new AtomicInteger(0);

    // ── simulates I/O latency ──
    static void simulateIO() throws InterruptedException { Thread.sleep(50); }

    // ── simulates CPU work ──
    static long simulateCPU() {
        long sum = 0;
        for (long i = 0; i < 5_000_000L; i++) sum += i;
        return sum;
    }

    public static void main(String[] args) throws Exception {

        int taskCount = 1000;

        // ══ I/O-bound: Virtual threads WIN ══
        System.out.println("══ I/O-bound: " + taskCount + " tasks, 50ms each ══\n");

        // Platform thread pool (fixed 50 threads)
        completedTasks.set(0);
        long start = System.currentTimeMillis();
        try (var pool = Executors.newFixedThreadPool(50)) {
            for (int i = 0; i < taskCount; i++) {
                pool.submit(() -> {
                    try { simulateIO(); completedTasks.incrementAndGet(); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }
        }
        System.out.println("Platform (50 threads) : " + (System.currentTimeMillis() - start) + "ms");

        // Virtual thread per task
        completedTasks.set(0);
        start = System.currentTimeMillis();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                exec.submit(() -> {
                    try { simulateIO(); completedTasks.incrementAndGet(); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }
        }
        System.out.println("Virtual (1 VT/task)   : " + (System.currentTimeMillis() - start) + "ms");
        // Virtual threads: ~50ms. Platform 50-thread pool: ~1000ms (1000/50 batches * 50ms)

        // ══ CPU-bound: Virtual threads do NOT help ══
        System.out.println("\n══ CPU-bound: 20 tasks (virtual vs parallel stream) ══\n");

        int cpuTasks = 20;

        start = System.currentTimeMillis();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<Long>>();
            for (int i = 0; i < cpuTasks; i++) futures.add(exec.submit(Stage4_VirtualVsPlatform::simulateCPU));
            for (var f : futures) f.get();
        }
        System.out.println("Virtual threads (CPU) : " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        java.util.stream.IntStream.range(0, cpuTasks).parallel().forEach(c -> simulateCPU());
        System.out.println("parallelStream  (CPU) : " + (System.currentTimeMillis() - start) + "ms");
        // parallelStream uses ForkJoinPool (platform threads pinned to cores) → faster for CPU work

        // ══ Anti-patterns ══
        System.out.println("\n══ Anti-patterns ══\n");

        // ❌ Anti-pattern 1: pooling virtual threads
        // var pool = Executors.newFixedThreadPool(100, Thread.ofVirtual().factory());
        // ↑ limits concurrency to 100 VTs — pointless, VTs are meant to be unlimited
        System.out.println("❌ Don't pool virtual threads — create one per task");

        // ❌ Anti-pattern 2: synchronized causing pinning
        System.out.println("❌ Don't use synchronized for I/O inside VTs — use ReentrantLock");

        // ❌ Anti-pattern 3: ThreadLocal with large objects
        System.out.println("❌ Don't store large objects in ThreadLocal on VTs — millions of VTs = millions of copies");

        // ✅ Correct pattern
        System.out.println("\n✅ Virtual threads are best for: thread-per-request servers");
        System.out.println("   Each incoming HTTP request → one virtual thread → synchronous blocking code");
        System.out.println("   No callbacks, no reactive chains — just plain readable blocking code at scale");
    }
}
