package org.example.threads.queues;

import java.util.Comparator;
import java.util.PriorityQueue;

/// Level 4 — PriorityQueue (Min-Heap)
///
/// Elements are dequeued by PRIORITY, not insertion order.
/// Internally backed by a binary min-heap stored in an array.
///
/// Min-Heap property: parent is always ≤ its children
///   peek()/poll() always returns the SMALLEST element (natural order)
///   For MAX, pass Comparator.reverseOrder()
///
/// Internal array layout (heap index math):
///   parent  of index i → (i - 1) / 2
///   left    of index i → 2*i + 1
///   right   of index i → 2*i + 2
///
/// Complexity:
///   offer()  → O(log n)  — sifts up
///   poll()   → O(log n)  — sifts down
///   peek()   → O(1)      — always the root
///   contains → O(n)      — no index structure
///
/// NOT thread-safe. Use PriorityBlockingQueue for concurrent use.
/// Does NOT allow null.
/// Iteration order is NOT sorted — only poll() gives sorted order.
///
/// Real-world: Dijkstra's shortest path, CPU scheduling, event simulation, hospital triage

public class Level4_PriorityQueue {

    record Task(int priority, String name) {}

    public static void main(String[] args) {

        // ══ Part 1: Natural order (min-heap on Integer) ══
        System.out.println("══ Min-Heap (natural order) ══");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(30);
        minHeap.offer(10);
        minHeap.offer(50);
        minHeap.offer(20);

        System.out.println("peek()    : " + minHeap.peek()); // 10 — always min
        System.out.println("iteration : " + minHeap);        // NOT sorted — internal heap array
        System.out.print("poll order: ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " "); // 10 20 30 50 ✓
        System.out.println();

        // ══ Part 2: Max-Heap via reversed Comparator ══
        System.out.println("\n══ Max-Heap (reverseOrder) ══");
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.offer(30);
        maxHeap.offer(10);
        maxHeap.offer(50);
        maxHeap.offer(20);

        System.out.print("poll order: ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " "); // 50 30 20 10 ✓
        System.out.println();

        // ══ Part 3: Custom object — sort by priority field ══
        System.out.println("\n══ Custom Comparator — Task by priority ══");
        PriorityQueue<Task> taskQueue = new PriorityQueue<>(
            Comparator.comparingInt(Task::priority) // lower number = higher priority
        );
        taskQueue.offer(new Task(3, "Low priority job"));
        taskQueue.offer(new Task(1, "Critical alert"));
        taskQueue.offer(new Task(2, "Normal task"));

        System.out.print("Processing order: ");
        while (!taskQueue.isEmpty()) {
            Task t = taskQueue.poll();
            System.out.print("[P" + t.priority() + " " + t.name() + "] ");
        }
        System.out.println();

        // ══ Part 4: K largest elements — classic interview pattern ══
        System.out.println("\n══ K Largest elements using min-heap of size K ══");
        int[] nums = {3, 1, 5, 12, 2, 11, 7, 9};
        int k = 3;

        PriorityQueue<Integer> kLargest = new PriorityQueue<>(k); // min-heap of size k
        for (int n : nums) {
            kLargest.offer(n);
            if (kLargest.size() > k) kLargest.poll(); // evict smallest — keeps top K
        }

        System.out.println("Input : {3,1,5,12,2,11,7,9}  k=" + k);
        System.out.print("Top-" + k + " largest: ");
        while (!kLargest.isEmpty()) System.out.print(kLargest.poll() + " "); // 7 11 12
        System.out.println();

        // ── key gotcha ──
        // PriorityQueue.remove(obj) is O(n) — must scan the heap
        // PriorityQueue iteration (for-each) is NOT in sorted order
        // Always use poll() to get elements in priority order
    }
}
