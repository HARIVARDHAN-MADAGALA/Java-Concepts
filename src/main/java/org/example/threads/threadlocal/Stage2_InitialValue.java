package org.example.threads.threadlocal;

/// Stage 2 — initialValue() and withInitial()
///
/// By default, get() returns null if set() was never called.
/// Two ways to provide a default value:
///
///   1. Override initialValue()  — called lazily on first get() per thread
///   2. ThreadLocal.withInitial(Supplier) — cleaner, lambda-based (Java 8+)
///
/// initialValue() is called ONCE per thread, on the first get() that finds no value.
/// After that, the thread owns its copy and can modify it independently.

public class Stage2_InitialValue {

    // ── option 1: subclass and override initialValue() ──
    static final ThreadLocal<Integer> counter = new ThreadLocal<>() {
        @Override
        protected Integer initialValue() {
            System.out.println("  [initialValue] called for " + Thread.currentThread().getName());
            return 0; // each thread starts at 0
        }
    };

    // ── option 2: withInitial — preferred, more concise ──
    static final ThreadLocal<StringBuilder> buffer =
        ThreadLocal.withInitial(() -> new StringBuilder("[" + Thread.currentThread().getName() + "] "));

    public static void main(String[] args) throws InterruptedException {

        System.out.println("── initialValue() called lazily on first get() ──");

        Runnable task = () -> {
            // first get() triggers initialValue() — then thread owns its copy
            System.out.println(Thread.currentThread().getName() + " initial: " + counter.get());
            counter.set(counter.get() + 10); // modify this thread's copy
            System.out.println(Thread.currentThread().getName() + " after +10: " + counter.get());
            counter.remove();
        };

        Thread t1 = new Thread(task, "Worker-1");
        Thread t2 = new Thread(task, "Worker-2");
        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("\n── withInitial — per-thread StringBuilder ──");

        Runnable bufferTask = () -> {
            StringBuilder sb = buffer.get(); // each thread gets its own StringBuilder
            sb.append("Hello from ").append(Thread.currentThread().getName());
            System.out.println(sb.toString());
            buffer.remove();
        };

        Thread t3 = new Thread(bufferTask, "Writer-A");
        Thread t4 = new Thread(bufferTask, "Writer-B");
        t3.start(); t4.start();
        t3.join();  t4.join();

        // ── initialValue() vs set() timing ──
        // initialValue() fires on first get() IF no set() has been called yet
        // once set() is called, initialValue() never fires for that thread again
        // calling remove() resets the thread — next get() calls initialValue() again
        System.out.println("\n── remove() resets — initialValue() fires again ──");
        counter.set(99);
        System.out.println("after set(99): " + counter.get()); // 99
        counter.remove();
        System.out.println("after remove(): " + counter.get()); // 0 — initialValue() fires again
        counter.remove();
    }
}
