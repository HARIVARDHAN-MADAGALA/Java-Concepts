# BlockingQueue

## The 3 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_ArrayBlockingQueue | Fixed-size, single lock, backpressure, offer/poll |
| 2 | Stage2_LinkedBlockingQueue | Two-lock design, drainTo, unbounded gotcha |
| 3 | Stage3_ProducerConsumer | Poison pill shutdown, wiring with ThreadPoolExecutor |

---

## ArrayBlockingQueue vs LinkedBlockingQueue

| | ArrayBlockingQueue | LinkedBlockingQueue |
|--|-------------------|---------------------|
| Backing | fixed array | linked nodes |
| Capacity | always bounded | optional (default MAX_VALUE) |
| Locks | **1 lock** (put + take share it) | **2 locks** (putLock + takeLock) |
| Concurrency | put() and take() serialise each other | put() and take() run in parallel |
| Memory | pre-allocated, predictable | allocates node per item |
| Best for | small queues, memory cap | high-throughput producer-consumer |

## Core Methods

```
put(e)    → insert, BLOCKS if full
take()    → remove, BLOCKS if empty

offer(e)  → insert, returns false if full (non-blocking)
poll()    → remove, returns null if empty (non-blocking)
peek()    → inspect head, no removal

drainTo(collection, maxElements)  → bulk transfer, more efficient than repeated poll()
```

## Poison Pill Shutdown Pattern
```java
// 1 producer, N consumers → send N poison pills
for (int i = 0; i < consumerCount; i++)
    queue.put(POISON);

// consumer loop
while (true) {
    Task t = queue.take();
    if (t == POISON) break;
    process(t);
}
```

## How ThreadPoolExecutor uses BlockingQueue
```
submit(task)
  └─ workQueue.put(task)       ← BlockingQueue<Runnable>
        └─ worker thread calls workQueue.take() in a loop
```
- `newFixedThreadPool`   → `LinkedBlockingQueue` (unbounded)
- `newCachedThreadPool`  → `SynchronousQueue` (zero-capacity, direct handoff)
- Custom pools           → `ArrayBlockingQueue` (bounded, apply backpressure)
