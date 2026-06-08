# Thread Synchronizers

## The 4 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_Semaphore | Permit-based concurrency limiting, connection pool, tryAcquire |
| 2 | Stage2_CountDownLatch | One-shot gate, completion gate pattern, starting gun pattern |
| 3 | Stage3_CyclicBarrier | Reusable meeting point, barrier action, BrokenBarrierException |
| 4 | Stage4_Phaser | Dynamic parties, multi-phase pipeline, arriveAndDeregister |

---

## Quick Comparison

| | Semaphore | CountDownLatch | CyclicBarrier | Phaser |
|--|-----------|----------------|---------------|--------|
| Reusable | ✅ | ❌ | ✅ | ✅ |
| Dynamic parties | ❌ | ❌ | ❌ | ✅ |
| All threads must sync | ❌ | ❌ | ✅ | ✅ |
| Barrier action | ❌ | ❌ | ✅ | ✅ (onAdvance) |
| Use case | Limit concurrency | Wait for N events | Meet at checkpoint | Multi-phase pipeline |

---

## Semaphore
```
permits = N
acquire() → take permit (block if 0)
release() → return permit
```
- NOT reentrant — same thread acquiring twice deadlocks itself
- `release()` can be called by a different thread than the acquirer

## CountDownLatch
```
count = N
countDown() → N--   (any thread, many times)
await()     → block until N == 0
```
- One-shot: once 0, stays 0
- Callers of `countDown()` and `await()` can be completely different threads

## CyclicBarrier
```
parties = N
await() → arrive + block until all N arrive → all released
```
- Reusable: auto-resets after each cycle
- Barrier action runs once per cycle on the last-arriving thread
- One interrupted thread → `BrokenBarrierException` for all waiting

## Phaser
```
register()              → add a party
arriveAndAwaitAdvance() → arrive + wait (like barrier.await())
arriveAndDeregister()   → arrive + leave permanently
arrive()                → arrive without waiting (non-blocking)
```
- Phase number increments after each full cycle
- Override `onAdvance(phase, parties)` to control termination
- Terminates when registered parties drops to 0
