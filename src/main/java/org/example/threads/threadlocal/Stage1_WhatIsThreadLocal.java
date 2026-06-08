package org.example.threads.threadlocal;

/// Stage 1 — What is ThreadLocal?
///
/// ThreadLocal gives each thread its own ISOLATED copy of a variable.
/// No sharing, no synchronization needed — each thread reads/writes its own slot.
///
/// Internal structure:
///   Each Thread object has a field:  ThreadLocal.ThreadLocalMap threadLocals
///   ThreadLocalMap is a hash map where:
///     key   = the ThreadLocal instance (weakly referenced)
///     value = this thread's copy of the value
///
///   threadLocal.set(v)  → Thread.currentThread().threadLocals.put(this, v)
///   threadLocal.get()   → Thread.currentThread().threadLocals.get(this)
///
/// The map lives ON the thread — NOT on the ThreadLocal object.
/// When the thread dies, its ThreadLocalMap (and all values in it) are GC'd.
///
/// Real-world: per-thread SimpleDateFormat, user session context, DB transaction connections,
///             request-scoped data in web frameworks (Spring's RequestContextHolder uses this)

public class Stage1_WhatIsThreadLocal {

    // ── one ThreadLocal shared across the class, but each thread gets its own value ──
    static final ThreadLocal<String> userName = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {

        // ── basic get / set / remove ──
        System.out.println("── Basic usage ──");
        userName.set("Alice");
        System.out.println("Main thread user : " + userName.get()); // Alice

        Thread t1 = new Thread(() -> {
            userName.set("Bob");
            System.out.println("Thread-1 user    : " + userName.get()); // Bob
        });

        Thread t2 = new Thread(() -> {
            userName.set("Charlie");
            System.out.println("Thread-2 user    : " + userName.get()); // Charlie
        });

        t1.start(); t2.start();
        t1.join();  t2.join();

        // main thread's value is untouched by t1 and t2
        System.out.println("Main thread after: " + userName.get()); // still Alice

        userName.remove(); // clean up — important in thread pools (threads are reused!)

        // ── without set() — get() returns null ──
        System.out.println("\n── get() without set() ──");
        Thread t3 = new Thread(() ->
            System.out.println("Thread-3 (no set): " + userName.get()) // null
        );
        t3.start(); t3.join();

        // ── each thread has its own slot — visualized ──
        // Thread-1's ThreadLocalMap: { userName → "Bob" }
        // Thread-2's ThreadLocalMap: { userName → "Charlie" }
        // main's    ThreadLocalMap: { userName → "Alice" }
        // same ThreadLocal key, completely separate values
    }
}
