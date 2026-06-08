package org.example.threads.threadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;

/// Stage 3 — Real-world pattern: per-thread expensive object
///
/// SimpleDateFormat is NOT thread-safe — if shared, concurrent parse/format calls corrupt results.
/// Creating a new instance per call is expensive (object allocation + pattern compilation).
///
/// ThreadLocal solution: create one instance per thread, reuse it safely.
///
/// This is also the standard pattern for:
///   - java.util.Random (before ThreadLocalRandom)
///   - DB connections in non-pool scenarios
///   - Marshallers / parsers that are expensive to create but not thread-safe
///
/// Spring Framework uses this pattern heavily:
///   TransactionSynchronizationManager → ThreadLocal<Map<DataSource, Connection>>
///   RequestContextHolder              → ThreadLocal<RequestAttributes>
///   SecurityContextHolder             → ThreadLocal<SecurityContext>

public class Stage3_RealWorldPattern {

    // ── one instance per thread — created lazily, reused across calls ──
    static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> {
            System.out.println("  [creating SDF for] " + Thread.currentThread().getName());
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        });

    // ── simulates a request context carried through a call stack without passing it as a parameter ──
    static final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();

    record RequestContext(String userId, String requestId) {}

    // ── service methods that need context — no parameter passing needed ──
    static void serviceLayer() {
        RequestContext ctx = requestContext.get();
        System.out.println("  ServiceLayer  — user=" + ctx.userId() + " req=" + ctx.requestId());
        repositoryLayer();
    }

    static void repositoryLayer() {
        RequestContext ctx = requestContext.get();
        System.out.println("  RepositoryLayer— user=" + ctx.userId() + " req=" + ctx.requestId());
    }

    public static void main(String[] args) throws InterruptedException {

        // ══ Part 1: Per-thread SimpleDateFormat ══
        System.out.println("══ Per-thread SimpleDateFormat ══\n");

        Runnable formatTask = () -> {
            for (int i = 0; i < 3; i++) {
                // dateFormat.get() reuses the SAME instance within this thread
                String formatted = dateFormat.get().format(new Date());
                System.out.println(Thread.currentThread().getName() + " → " + formatted);
            }
            dateFormat.remove(); // clean up after thread is done
        };

        Thread t1 = new Thread(formatTask, "Request-1");
        Thread t2 = new Thread(formatTask, "Request-2");
        Thread t3 = new Thread(formatTask, "Request-3");
        t1.start(); t2.start(); t3.start();
        t1.join();  t2.join();  t3.join();

        // ══ Part 2: Request context propagation (simulates Spring's RequestContextHolder) ══
        System.out.println("\n══ Request Context via ThreadLocal ══\n");

        Runnable handleRequest = () -> {
            String threadName = Thread.currentThread().getName();
            // set context at entry point (like a servlet filter)
            requestContext.set(new RequestContext("user-" + threadName, "req-" + System.nanoTime()));
            try {
                System.out.println(threadName + " → handling request");
                serviceLayer();   // deep call — accesses context without parameter
            } finally {
                requestContext.remove(); // ALWAYS clean up — critical in thread pools
            }
        };

        Thread req1 = new Thread(handleRequest, "Thread-A");
        Thread req2 = new Thread(handleRequest, "Thread-B");
        req1.start(); req2.start();
        req1.join();  req2.join();

        // ── why remove() in finally matters ──
        // Thread pools reuse threads — if you don't remove(), the next task on that thread
        // will see the PREVIOUS task's context → silent data leak / wrong user bugs
    }
}
