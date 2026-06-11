# HashMap → SynchronizedMap → ConcurrentHashMap — Evolution, Problems & Solutions

---

## Stage 1: The Beginning — HashMap

HashMap is the most basic key-value store. Backed by an array of buckets internally.

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);
```

### Internal Structure:
```
Node<K,V>[] table   → array of buckets (default capacity = 16)
int size            → number of key-value pairs
int modCount        → tracks structural changes (just like ArrayList!)
float loadFactor    → 0.75 (when to resize)
```

### How put() works internally:
```
1. hash(key) → compute hash
2. index = hash & (capacity - 1)  → find bucket position
3. if bucket empty → insert node
4. if bucket has collision → add to LinkedList (or TreeNode if > 8 entries)
```

### Visual:
```
table[] index:
  0 → null
  1 → [a=1] → null
  2 → [b=2] → [x=9] → null   (collision — same bucket)
  3 → [c=3] → null
  ...
 15 → null
```

### When does it resize?
```
size > capacity * loadFactor
size > 16 * 0.75 = 12  → resize to 32, rehash all entries
```

---

## Stage 2: The Problem — HashMap is NOT Thread-Safe

### Problem 1: Data Loss on concurrent put()
```java
Map<String, Integer> map = new HashMap<>();

Thread t1 = new Thread(() -> map.put("a", 1));
Thread t2 = new Thread(() -> map.put("b", 2));

t1.start(); t2.start();
// Both threads compute same bucket index
// t2 overwrites t1's node → DATA LOSS ❌
```

### Problem 2: Infinite Loop during resize (Java 7 and below)
```
Two threads trigger resize at the same time
→ circular reference in LinkedList during rehashing
→ infinite loop, CPU 100% ❌
(Fixed in Java 8 — but still not thread-safe)
```

### Problem 3: modCount mismatch — same as ArrayList!
```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    map.remove(entry.getKey());  // modCount++ → ConcurrentModificationException ❌
}
```

HashMap has modCount too — iterator throws ConcurrentModificationException on structural change during iteration.

---

## Stage 3: Solution 1 — Collections.synchronizedMap()

Wraps HashMap with synchronized block on every method.

```java
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
map.put("a", 1);   // synchronized
map.get("a");      // synchronized
map.remove("a");   // synchronized
```

### How it works internally:
```java
// every method is wrapped like this:
public V put(K key, V value) {
    synchronized(mutex) {        // locks the ENTIRE map
        return m.put(key, value);
    }
}

public V get(Object key) {
    synchronized(mutex) {        // reads also locked
        return m.get(key);
    }
}
```

### Visual — one thread at a time:
```
Thread1 → put("a", 1) → acquires lock → writes → releases lock
Thread2 → put("b", 2) → WAITING... → acquires lock → writes → releases lock
Thread3 → get("a")    → WAITING... → acquires lock → reads  → releases lock
```

### Iteration still needs manual sync:
```java
// ❌ NOT safe
for (Map.Entry<String, Integer> e : map.entrySet()) { }

// ✅ manually lock
synchronized(map) {
    for (Map.Entry<String, Integer> e : map.entrySet()) { }
}
```

### The Big Problem — single lock on entire map:
```
All threads compete for ONE lock
→ only one thread can read OR write at any time
→ massive contention in multi-threaded apps
→ poor performance ❌
```

---

## Stage 4: Solution 2 — ConcurrentHashMap

Invented to solve the single-lock bottleneck of synchronizedMap.

```java
Map<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);
map.get("a");
```

### The Core Idea — Segment Locking (Java 7) → Bucket-level CAS (Java 8+)

#### Java 7 — Segment based locking:
```
map divided into 16 Segments (by default)
each Segment has its own lock

Thread1 writes to Segment 0 → locks Segment 0 only
Thread2 writes to Segment 5 → locks Segment 5 only
→ both run simultaneously! ✅
```

#### Java 8+ — Even better: CAS + synchronized per bucket:
```java
// internal put() simplified:
public V put(K key, V value) {
    int hash = hash(key);
    int index = hash & (capacity - 1);

    if (table[index] == null) {
        CAS(table[index], null, newNode);  // Compare-And-Swap — no lock needed!
    } else {
        synchronized(table[index]) {       // lock only THIS bucket, not whole map
            // add to chain
        }
    }
}
```

### Visual — multiple threads working simultaneously:
```
map buckets: [0] [1] [2] [3] [4] [5] ... [15]

Thread1 → put("a") → bucket 1 → locks bucket 1 only
Thread2 → put("b") → bucket 5 → locks bucket 5 only
Thread3 → get("c") → bucket 2 → NO lock needed for read!

All 3 threads run at the SAME TIME ✅
```

### How reads work — no lock at all!
```java
// get() uses volatile read — no synchronization needed
public V get(Object key) {
    Node<K,V> e = getNode(hash(key), key);  // reads volatile array — no lock ✅
    return e == null ? null : e.value;
}
```

Nodes have `volatile` value — guaranteed visibility across threads without locking.

---

## Stage 5: modCount in ConcurrentHashMap — Does it exist?

### HashMap — has modCount, throws ConcurrentModificationException:
```java
Map<String, Integer> map = new HashMap<>();
for (String key : map.keySet()) {
    map.remove(key);  // modCount++ → ❌ ConcurrentModificationException
}
```

### ConcurrentHashMap — weakly consistent iterator, NO ConcurrentModificationException:
```java
Map<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1); map.put("b", 2); map.put("c", 3);

for (String key : map.keySet()) {
    map.remove(key);  // ✅ NO exception — iterator is weakly consistent
}
```

### Why no exception?
ConcurrentHashMap iterator does NOT use modCount check.
It traverses the snapshot of the bucket structure — similar concept to CopyOnWriteArrayList snapshot.

```
Iterator created → starts traversing buckets from index 0
Another thread adds/removes → iterator may or may not see it
→ no crash, but weak consistency (may miss newly added entries)
```

---

## Stage 6: Key Differences — Side by Side

### put() behavior:
```
HashMap            → no lock → data loss in multi-thread ❌
synchronizedMap    → locks ENTIRE map → safe but slow ✅
ConcurrentHashMap  → locks ONE bucket (or CAS) → safe and fast ✅
```

### get() behavior:
```
HashMap            → no lock → may read stale data in multi-thread ❌
synchronizedMap    → locks ENTIRE map → safe but slow ✅
ConcurrentHashMap  → volatile read, no lock → safe and fast ✅
```

### Iteration behavior:
```
HashMap            → modCount check → ConcurrentModificationException on modification ❌
synchronizedMap    → modCount check → needs manual sync block for iteration ❌
ConcurrentHashMap  → no modCount check → weakly consistent, no exception ✅
```

### Null keys/values:
```
HashMap            → allows 1 null key, multiple null values ✅
synchronizedMap    → allows 1 null key (wraps HashMap) ✅
ConcurrentHashMap  → NO null keys or values ❌ → NullPointerException
```

Why ConcurrentHashMap doesn't allow null?
```java
map.get("key") == null
// Does this mean key doesn't exist OR value is null?
// In multi-thread you can't safely re-check → ambiguous → banned entirely
```

---

## Stage 7: Real World — When to use what

```
Single thread, no concurrency needed
    → HashMap ✅

Multi-thread, write-heavy, need strong consistency
    → Collections.synchronizedMap() ✅
    → but manually sync iteration block

Multi-thread, read-heavy OR mixed read-write
    → ConcurrentHashMap ✅  (almost always the best choice)
    → fine-grained locking → high throughput
```

---

## Final Summary — Full Evolution

```
Problem 1: Data corruption in multi-thread with HashMap
    → two threads write same bucket → data loss
    → resize triggers infinite loop (Java 7)
        ↓
    Solution: Collections.synchronizedMap()
      - locks entire map on every operation
      - safe but slow — single lock bottleneck

Problem 2: synchronizedMap is too slow — one lock for everything
    → all threads wait for single lock even for different buckets
        ↓
    Solution: ConcurrentHashMap
      - Java 7: 16 Segment locks → 16 threads can write simultaneously
      - Java 8: CAS + per-bucket lock → even more fine-grained
      - reads: no lock (volatile) → blazing fast
      - no modCount → no ConcurrentModificationException
      - trade-off: weakly consistent iteration
```

---

## Quick Reference Table

| Feature | `HashMap` | `synchronizedMap` | `ConcurrentHashMap` |
|---|---|---|---|
| Thread-safe | ❌ | ✅ | ✅ |
| Lock granularity | none | entire map | per bucket (CAS) |
| Read lock | none | full lock | no lock (volatile) |
| Write lock | none | full lock | bucket-level lock |
| modCount / CME | ✅ throws | ✅ throws | ❌ never throws |
| Null key allowed | ✅ | ✅ | ❌ |
| Null value allowed | ✅ | ✅ | ❌ |
| Iteration | fail-fast | fail-fast (manual sync) | weakly consistent |
| Performance | fastest (single thread) | slow (contention) | fastest (multi-thread) |
| Use when | single thread | write-heavy multi-thread | read/write multi-thread |
