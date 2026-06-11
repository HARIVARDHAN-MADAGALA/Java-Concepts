# CopyOnWriteArrayList — Evolution, Problems & Solutions

---

## Stage 1: The Beginning — ArrayList

ArrayList is the most basic list. Backed by a simple array internally.

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
```

### Internal structure:
```
Object[] elementData = ["a", "b", "c"]
int modCount = 0;   // tracks structural changes
```

---

## Stage 2: The Problem — Write During Iteration

### What is modCount?
Every time you structurally modify ArrayList (add/remove), `modCount` increments.

```java
list.add("d");    // modCount = 1
list.remove("a"); // modCount = 2
list.set(0, "x"); // modCount stays same — set() is NOT structural change
```

### How iterator uses modCount:
When for-each loop starts, iterator captures modCount as `expectedModCount`.

```java
// for-each is just syntactic sugar — compiler converts it to:
Iterator<String> it = list.iterator();  // expectedModCount = modCount (say 3)
while (it.hasNext()) {
    String s = it.next();  // checkForComodification() called here!
}
```

### checkForComodification():
```java
final void checkForComodification() {
    if (modCount != expectedModCount)   // live vs snapshot
        throw new ConcurrentModificationException();
}
```

### The Crash:
```java
for (String s : list) {
    list.remove(s);  // modCount++ → now modCount != expectedModCount
                     // next it.next() → BOOM ConcurrentModificationException ❌
}
```

### Visual:
```
list = [a, b, c],  modCount = 3

for-each starts → expectedModCount = 3

next() → modCount(3) == expectedModCount(3) ✅ returns "a"
list.remove("a") → modCount becomes 4
next() → modCount(4) != expectedModCount(3) ❌ ConcurrentModificationException
```

---

## Stage 3: Solution 1 — iterator.remove()

Iterator has its own `remove()` method that **syncs** expectedModCount after removing.

```java
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("b"))
        it.remove();  // ✅ syncs expectedModCount internally
}
```

### Why it works — inside iterator.remove():
```java
public void remove() {
    // removes element
    modCount++;
    expectedModCount = modCount;  // keeps in sync ✅ no mismatch
}
```

### Limitation of iterator.remove():
Works only in **single-threaded** scenario.
If another thread modifies the list while you iterate → you have no control over that thread's `modCount` change.

---

## Stage 4: The Real Problem — Multi-Threading

```java
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

// Thread 1 - iterating
new Thread(() -> {
    for (String s : list) {     // expectedModCount = 3
        Thread.sleep(10);
        System.out.println(s);
    }
}).start();

// Thread 2 - modifying at the same time
new Thread(() -> {
    list.remove("c");           // modCount becomes 4 on Thread1's list ❌
}).start();

// Thread1 throws ConcurrentModificationException — it.remove() can't help here!
```

`iterator.remove()` is useless here — **Thread2** is doing the modification, not Thread1.

---

## Stage 5: Solution 2 — CopyOnWriteArrayList

Invented to solve multi-threaded read + write problem.

### Core idea — the name says it all:
Every **write** operation creates a **brand new copy** of the underlying array.

```java
List<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
```

### Internal structure:
```java
private transient volatile Object[] array;  // volatile — visible to all threads
final transient ReentrantLock lock = new ReentrantLock();
// NO modCount at all!
```

### How add() works internally:
```java
public boolean add(E e) {
    lock.lock();  // only one writer at a time
    try {
        Object[] oldArray = getArray();                        // [a, b, c]
        Object[] newArray = Arrays.copyOf(oldArray, len + 1); // copy → [a, b, c, _]
        newArray[len] = e;                                     // write → [a, b, c, d]
        setArray(newArray);                                    // swap reference
        return true;
    } finally {
        lock.unlock();
    }
}
```

### How iterator works — no modCount check at all!
```java
public Iterator<E> iterator() {
    return new COWIterator<E>(getArray(), 0);  // snapshot passed here
}

static final class COWIterator<E> {
    private final Object[] snapshot;  // holds OLD array

    public E next() {
        return (E) snapshot[cursor++];  // reads from snapshot — no checkForComodification!
    }
    // checkForComodification() DOES NOT EXIST here!
}
```

### Visual — how snapshot saves us:
```
list.iterator() called → snapshot = [a, b, c]  (array reference at this moment)

Thread2 calls list.add("d"):
  old array [a, b, c] → copied → new array [a, b, c, d]
  array reference swapped to new array

Thread1 iterator:
  still reading from snapshot [a, b, c]  ← completely unaffected ✅
  no modCount → no mismatch → no exception ✅
```

---

## Stage 6: Three Iterators — Snapshot Timing

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));

// Loop 1 — snapshot1 = [a, b, c]
for (String s : list) {
    list.add("d");  // creates new arrays, snapshot1 unaffected
    System.out.println("Loop1: " + s);
}
// Output: a, b, c  (only 3, snapshot length = 3)
// live list = [a, b, c, d, d, d]

// Loop 2 — snapshot2 = [a, b, c, d, d, d]
for (String s : list) {
    list.add("x");
    System.out.println("Loop2: " + s);
}
// Output: a, b, c, d, d, d  (6 iterations, snapshot length = 6)
// live list = [a, b, c, d, d, d, x, x, x, x, x, x]

// Loop 3 — snapshot3 = [a, b, c, d, d, d, x, x, x, x, x, x]
for (String s : list) {
    System.out.println("Loop3: " + s);
}
// Output: 12 elements
```

### Rule: snapshot = whatever array reference points to AT THE MOMENT iterator is created
- iterator created before write → old array snapshot
- iterator created after write → new array snapshot

---

## Stage 7: The Trade-off — Weak Consistency

Since iterator holds old snapshot, it may NOT see latest writes. This is called **Weak Consistency**.

```java
for (String s : list) {          // snapshot = [a, b, c]
    list.add("d");               // live list updated, snapshot untouched
    System.out.println(list);    // prints LIVE list each time
}
```
```
Output:
[a, b, c, d]        ← live list after 1st add
[a, b, c, d, d]     ← live list after 2nd add
[a, b, c, d, d, d]  ← live list after 3rd add

but iterator only ran 3 times — based on snapshot length = 3
```

---

## Stage 8: Solution 3 — Collections.synchronizedList()

CopyOnWriteArrayList is bad for **write-heavy** scenarios — every write copies the full array.

```java
// 1000 writes = 1000 full array copies = memory nightmare ❌
list.add("a");  // copies entire array
list.add("b");  // copies entire array again
list.add("c");  // copies entire array again
```

For write-heavy multi-threaded scenario → use `Collections.synchronizedList()`.

### How it works — just a lock, no copying:
```java
List<String> list = Collections.synchronizedList(new ArrayList<>());

// internally every method is synchronized block
public boolean add(E e) {
    synchronized(mutex) {   // locks list — no copy needed
        return list.add(e);
    }
}
```

### Iteration needs manual sync:
```java
// ❌ NOT safe — iterator not synchronized
for (String s : list) { }

// ✅ manually synchronize
synchronized(list) {
    for (String s : list) { }
}
```

---

## Final Summary — Evolution

```
Problem 1: Write during iteration in single thread
    ArrayList throws ConcurrentModificationException (modCount mismatch)
        ↓
    Solution: iterator.remove() — syncs expectedModCount, safe for single thread

Problem 2: Multi-threaded read + write, read-heavy
    ArrayList not thread-safe, modCount mismatch across threads
        ↓
    Solution: CopyOnWriteArrayList
      - no modCount at all
      - every write creates NEW array
      - iterator holds snapshot → reads unaffected by writes
      - trade-off: weak consistency + memory cost per write

Problem 3: Multi-threaded read + write, write-heavy
    CopyOnWriteArrayList too expensive — full array copy on every write
        ↓
    Solution: Collections.synchronizedList()
      - just locks the list on every operation
      - no array copying — cheap writes
      - iteration needs manual synchronized block
```

---

## Quick Reference Table

| Scenario | Solution |
|---|---|
| Single thread, no write during iteration | `ArrayList` |
| Single thread, write during iteration | `iterator.remove()` |
| Multi-thread, read-heavy, write-rare | `CopyOnWriteArrayList` |
| Multi-thread, write-heavy | `Collections.synchronizedList()` |

| Feature | `ArrayList` | `CopyOnWriteArrayList` | `synchronizedList` |
|---|---|---|---|
| Thread-safe | ❌ | ✅ | ✅ |
| Has modCount | ✅ | ❌ | ✅ |
| Write during iteration | ❌ throws | ✅ safe | ❌ needs manual sync |
| Write cost | cheap | copies full array | just a lock |
| Read cost | fast | fast (no lock) | slow (locked) |
| Consistency | strong | weak (snapshot) | strong |
