# Virtual Threads (Java 21)

## The 4 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_WhatAreVirtualThreads | 3 creation ways, isVirtual(), 10k VT scale test |
| 2 | Stage2_CarrierThreadsAndPinning | mount/unmount lifecycle, pinning with synchronized, fix with ReentrantLock |
| 3 | Stage3_ExecutorAndStructuredConcurrency | newVirtualThreadPerTaskExecutor, ShutdownOnFailure, ShutdownOnSuccess |
| 4 | Stage4_VirtualVsPlatform | I/O vs CPU benchmark, anti-patterns, correct usage |

---

## How Virtual Threads Work

```
JVM Carrier Thread Pool (= CPU cores, e.g. 8)
  Carrier-1 ──► VT-1 (running)   VT-2 (blocked/heap) VT-3 (blocked/heap)
  Carrier-2 ──► VT-4 (running)   ...
  ...

When VT blocks (sleep/I/O/lock):
  JVM unmounts VT → saves stack to heap → carrier picks up next runnable VT
When block completes:
  JVM remounts VT → possibly on a different carrier
```

## Three Ways to Create

```java
// 1 — direct
Thread vt = Thread.ofVirtual().name("my-vt").start(runnable);

// 2 — shorthand
Thread vt = Thread.startVirtualThread(runnable);

// 3 — executor (production recommended)
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    exec.submit(task);
}
```

## Pinning — The Main Gotcha

```java
// ❌ PINNING — synchronized holds the carrier thread too
synchronized (lock) {
    Thread.sleep(100); // carrier blocked, can't serve other VTs
}

// ✅ NO PINNING — ReentrantLock releases the carrier during wait
reentrantLock.lock();
try {
    Thread.sleep(100); // VT unmounts, carrier is FREE
} finally { reentrantLock.unlock(); }
```
Detect pinning: `-Djdk.tracePinnedThreads=full`

## Structured Concurrency

```java
// ShutdownOnFailure — all or nothing
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var t1 = scope.fork(() -> fetchDB());
    var t2 = scope.fork(() -> fetchCache());
    scope.join();
    scope.throwIfFailed();
    use(t1.get(), t2.get());
}

// ShutdownOnSuccess — first wins, rest cancelled
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
    scope.fork(() -> fetchFromReplica1());
    scope.fork(() -> fetchFromReplica2());
    scope.join();
    return scope.result();
}
```

## Virtual vs Platform

| | Platform Thread | Virtual Thread |
|--|----------------|----------------|
| Managed by | OS | JVM |
| Stack size | ~1MB (OS) | ~few KB (heap, grows) |
| Creation cost | high | near zero |
| Max practical count | ~thousands | millions |
| Best for | CPU-bound | I/O-bound |
| Blocking I/O | wastes OS thread | unmounts, carrier reused |

## Anti-Patterns

| ❌ Don't | ✅ Do instead |
|---------|--------------|
| Pool virtual threads | one VT per task (they're cheap) |
| `synchronized` for long sections | `ReentrantLock` |
| ThreadLocal with large objects | keep ThreadLocal values small or use ScopedValue |
| Use VTs for CPU-bound work | use `parallelStream` / `ForkJoinPool` |

## ScopedValue (Java 21 preview → Java 24 stable)
```java
// ScopedValue = immutable, scoped alternative to ThreadLocal for virtual threads
ScopedValue<String> USER = ScopedValue.newInstance();
ScopedValue.where(USER, "alice").run(() -> {
    System.out.println(USER.get()); // "alice" — available to all VTs in this scope
});
```
