package org.example.threads.queues;

import java.util.LinkedList;
import java.util.Queue;

/// Level 1 — Queue Interface (java.util.Queue)
///
/// Queue is the ROOT contract for all queue types in Java.
/// Extends Collection — adds ordering semantics (typically FIFO).
///
/// Every method comes in TWO flavors:
///
///   Operation   │ Throws exception │ Returns special value
///   ────────────┼──────────────────┼──────────────────────
///   Insert      │ add(e)           │ offer(e)  → false if full
///   Remove      │ remove()         │ poll()    → null if empty
///   Inspect     │ element()        │ peek()    → null if empty
///
/// Rule of thumb:
///   Use offer/poll/peek in production — no exception handling needed
///   add/remove/element are for when empty/full is a programming error
///
/// Hierarchy overview:
///   Queue
///   ├── LinkedList          (basic FIFO, Level 2)
///   ├── PriorityQueue       (heap-based, Level 4)
///   ├── Deque               (double-ended, Level 5)
///   │   ├── ArrayDeque
///   │   └── LinkedList
///   └── BlockingQueue       (thread-safe, Level 6)
///       ├── ArrayBlockingQueue
///       ├── LinkedBlockingQueue
///       └── SynchronousQueue

public class Level1_QueueInterface {

    public static void main(String[] args) {

        Queue<String> queue = new LinkedList<>(); // simplest Queue implementation

        // ── offer vs add ──
        System.out.println("── offer (safe insert) ──");
        System.out.println(queue.offer("A")); // true
        System.out.println(queue.offer("B")); // true
        System.out.println(queue.offer("C")); // true

        // ── peek vs element ──
        System.out.println("\n── peek / element (inspect head) ──");
        System.out.println("peek()   : " + queue.peek());    // A — null if empty
        System.out.println("element(): " + queue.element()); // A — throws NoSuchElementException if empty

        // ── poll vs remove ──
        System.out.println("\n── poll / remove (dequeue) ──");
        System.out.println("poll()  : " + queue.poll());   // A — null if empty
        System.out.println("poll()  : " + queue.poll());   // B
        System.out.println("remove(): " + queue.remove()); // C — throws NoSuchElementException if empty

        // ── safe vs unsafe on empty queue ──
        System.out.println("\n── behavior on empty queue ──");
        System.out.println("poll() on empty  : " + queue.poll());   // null — safe
        System.out.println("peek() on empty  : " + queue.peek());   // null — safe
        try {
            queue.remove(); // throws
        } catch (java.util.NoSuchElementException e) {
            System.out.println("remove() on empty: NoSuchElementException ← unsafe");
        }
        try {
            queue.element(); // throws
        } catch (java.util.NoSuchElementException e) {
            System.out.println("element() on empty: NoSuchElementException ← unsafe");
        }

        // ── Queue is an interface — behavior depends on implementation ──
        // LinkedList  → FIFO, unbounded, allows null
        // ArrayDeque  → FIFO, unbounded, NO null, faster than LinkedList
        // PriorityQueue → ordered by priority, NOT strict FIFO
        // BlockingQueue → thread-safe, put/take block
    }
}
