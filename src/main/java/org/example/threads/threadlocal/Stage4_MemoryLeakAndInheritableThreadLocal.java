package org.example.threads.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/// Stage 4 — Memory Leak + InheritableThreadLocal
///
/// ── THE memory leak ──
/// ThreadLocalMap key = WeakReference to the ThreadLocal instance
/// ThreadLocalMap value = STRONG reference to your object
///
/// If:
///   - Thread is pooled (never dies) — ThreadLocalMap lives forever
///   - ThreadLocal variable goes out of scope — key becomes weakly reachable → GC clears key
///   - BUT: value is still strongly referenced by the entry → value is NEVER collected
///   → classic memory leak in thread pools (Tomcat, HikariCP, etc.)
///
/// Fix: ALWAYS call remove() when you're done — ideally in a finally block.
///
/// ── InheritableThreadLocal ──
/// Child threads inherit the PARENT'S value at the time of thread creation (copied, not shared).
/// Mutations in the child do NOT affect the parent, and vice versa after the copy.
///
/// Used by: MDC (Mapped Diagnostic Context) in logging frameworks (Logback/Log4j2)

public class Stage4_MemoryLeakAndInheritableThreadLocal {

    // ── demonstrates the leak risk ──
    static final ThreadLocal<byte[]> leaky = new ThreadLocal<>();

    // ── InheritableThreadLocal — child gets a copy of parent's value ──
    static final InheritableThreadLocal<String> inherited = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {

        // ══ Part 1: Memory leak in thread pool ══
        System.out.println("══ Memory leak demo — thread pool without remove() ══\n");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        // ── task WITHOUT remove() — leaks 1MB per submission on pooled threads ──
        for (int i = 1; i <= 4; i++) {
            int taskId = i;
            pool.submit(() -> {
                leaky.set(new byte[1024 * 1024]); // 1 MB
                System.out.println("Task-" + taskId + " on " + Thread.currentThread().getName()
                    + " — set 1MB, no remove()");
                // leaky.remove() ← missing! value stays in thread's map forever
            });
        }

        Thread.sleep(500);

        // ── same pool, new tasks — threads still carry the old 1MB values ──
        System.out.println("\nSecond batch — old values still in thread maps:");
        for (int i = 5; i <= 6; i++) {
            int taskId = i;
            pool.submit(() ->
                System.out.println("Task-" + taskId + " on " + Thread.currentThread().getName()
                    + " — leaky.get()=" + (leaky.get() != null ? leaky.get().length + " bytes LEAKED" : "null"))
            );
        }

        Thread.sleep(300);

        // ── fix: always remove in finally ──
        System.out.println("\n── Fix: remove() in finally ──");
        pool.submit(() -> {
            leaky.set(new byte[1024 * 1024]);
            try {
                System.out.println("Task-safe — doing work");
            } finally {
                leaky.remove(); // thread-pool safe
                System.out.println("Task-safe — removed, slot clean");
            }
        });

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        // ══ Part 2: InheritableThreadLocal ══
        System.out.println("\n══ InheritableThreadLocal — child inherits parent's value ══\n");

        inherited.set("parent-context");
        System.out.println("Parent value: " + inherited.get()); // parent-context

        Thread child1 = new Thread(() -> {
            System.out.println("Child-1 inherited : " + inherited.get()); // parent-context (copied)
            inherited.set("child-1-override"); // only affects child-1's copy
            System.out.println("Child-1 after set : " + inherited.get()); // child-1-override
        });

        Thread child2 = new Thread(() ->
            System.out.println("Child-2 inherited : " + inherited.get()) // parent-context (own copy)
        );

        child1.start(); child1.join();
        child2.start(); child2.join();

        System.out.println("Parent after children: " + inherited.get()); // still parent-context

        inherited.remove();

        // ── InheritableThreadLocal caveat with thread pools ──
        // Thread pools create threads at startup — NOT when tasks are submitted
        // So InheritableThreadLocal does NOT propagate to pooled threads at task-submission time
        // Use TransmittableThreadLocal (TTL, Alibaba library) for pool-aware context propagation
    }
}
