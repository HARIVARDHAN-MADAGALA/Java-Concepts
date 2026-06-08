# ThreadLocal

## The 4 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_WhatIsThreadLocal | get/set/remove, per-thread isolation, internal structure |
| 2 | Stage2_InitialValue | initialValue(), withInitial(), remove() resets to default |
| 3 | Stage3_RealWorldPattern | per-thread SimpleDateFormat, request context propagation |
| 4 | Stage4_MemoryLeakAndInheritableThreadLocal | memory leak in pools, fix, InheritableThreadLocal |

---

## Internal Structure

```
Thread object
└── threadLocals: ThreadLocalMap
      ├── Entry { key=WeakRef(threadLocalA), value=valueA }
      └── Entry { key=WeakRef(threadLocalB), value=valueB }
```
- Map lives ON the Thread, not on the ThreadLocal
- ThreadLocal is just the key
- When thread dies → map is GC'd with it

## API

```java
ThreadLocal<T> tl = ThreadLocal.withInitial(() -> defaultValue);

tl.set(value)   // store in current thread's map
tl.get()        // read from current thread's map (calls initialValue() if absent)
tl.remove()     // delete from current thread's map — ALWAYS call this in thread pools
```

## Memory Leak Explained

```
ThreadLocalMap entry:
  key   = WeakReference(ThreadLocal)  ← GC can clear this
  value = strong reference to object  ← GC CANNOT clear this

Scenario:
  1. Thread is pooled (lives forever)
  2. ThreadLocal variable goes out of scope → key cleared by GC
  3. value is still strongly held by the orphaned entry → LEAK
```

Fix: `threadLocal.remove()` in a `finally` block — always.

## InheritableThreadLocal

```java
InheritableThreadLocal<String> itl = new InheritableThreadLocal<>();
itl.set("parent-value");

new Thread(() -> {
    itl.get(); // "parent-value" — copied at thread creation time
    itl.set("child-override"); // only affects this child thread
}).start();

itl.get(); // still "parent-value" in parent
```

- Value is COPIED from parent → child at thread creation (not shared)
- Does NOT work with thread pools (threads are created once at startup, not per-task)
- For pool-aware propagation → use Alibaba's TransmittableThreadLocal (TTL)

## Real-World Uses

| Usage | What's stored |
|-------|---------------|
| Spring TransactionSynchronizationManager | `Map<DataSource, Connection>` |
| Spring RequestContextHolder | `RequestAttributes` (HttpServletRequest) |
| Spring SecurityContextHolder | `SecurityContext` (logged-in user) |
| Logback/Log4j2 MDC | `Map<String, String>` (trace/request IDs) |
| Hibernate | `Session` per thread |
| Custom | `SimpleDateFormat`, parsers, formatters |
