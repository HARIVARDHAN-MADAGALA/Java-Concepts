# Queue Hierarchy

```
Queue  (java.util.Queue)
├── Level 2: Linear Queue     → LinkedList (basic FIFO)
├── Level 3: Circular Queue   → ring buffer, slot reuse, O(1) ops (custom / ArrayDeque internals)
├── Level 4: PriorityQueue    → min-heap, dequeue by priority not insertion order
├── Level 5: Deque            → double-ended, stack + queue
│   ├── ArrayDeque            → ring buffer, preferred, no null
│   └── LinkedList            → doubly-linked, allows null
└── Level 6: BlockingQueue    → thread-safe, put/take block
    ├── ArrayBlockingQueue    → bounded, 1 lock
    ├── LinkedBlockingQueue   → bounded/unbounded, 2 locks (higher throughput)
    └── SynchronousQueue      → 0 capacity, direct hand-off
```

---

## Level Summary

| Level | Type | Ordering | Thread-safe | Bounded | Null |
|-------|------|----------|-------------|---------|------|
| 1 | Queue interface | FIFO | ❌ | depends | depends |
| 2 | LinearQueue (LinkedList) | FIFO | ❌ | ❌ | ✅ |
| 3 | CircularQueue | FIFO | ❌ | ✅ | ❌ |
| 4 | PriorityQueue | priority | ❌ | ❌ | ❌ |
| 5 | ArrayDeque | FIFO/LIFO | ❌ | ❌ | ❌ |
| 6a | ArrayBlockingQueue | FIFO | ✅ | ✅ always | ❌ |
| 6b | LinkedBlockingQueue | FIFO | ✅ | optional | ❌ |
| 6c | SynchronousQueue | hand-off | ✅ | 0 | ❌ |

---

## Queue Interface — Two-flavor Methods

```
offer(e)  / add(e)      → insert  (false vs throws on full)
poll()    / remove()    → remove  (null  vs throws on empty)
peek()    / element()   → inspect (null  vs throws on empty)
```
Always prefer offer/poll/peek — no exception handling needed.

## Circular Queue — Ring Buffer Trick
```
tail = (tail + 1) % capacity   // wrap around
head = (head + 1) % capacity   // wrap around
full  → (tail + 1) % capacity == head
empty → head == tail
```

## PriorityQueue — Heap Index Math
```
parent of i → (i-1) / 2
left   of i → 2*i + 1
right  of i → 2*i + 2

offer → O(log n)  sift up
poll  → O(log n)  sift down
peek  → O(1)      root always
```

## Deque Method Map
```
             Front                Back
add      addFirst(e)          addLast(e)   ← throws on full
safe     offerFirst(e)        offerLast(e) ← false on full
remove   removeFirst()        removeLast() ← throws on empty
safe     pollFirst()          pollLast()   ← null on empty
inspect  getFirst() / peekFirst()  getLast() / peekLast()

As Stack → push() = addFirst,  pop() = removeFirst
As Queue → offerLast() + pollFirst()
```

## BlockingQueue — Locking Design
```
ArrayBlockingQueue  → 1 ReentrantLock  → put() and take() block each other
LinkedBlockingQueue → putLock + takeLock → put() and take() run in parallel
SynchronousQueue    → no storage, transfer() — rendezvous point
```
