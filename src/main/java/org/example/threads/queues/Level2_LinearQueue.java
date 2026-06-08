package org.example.threads.queues;

import java.util.LinkedList;
import java.util.Queue;

/// Level 2 — Linear Queue (basic FIFO)
///
/// Strict First-In First-Out: the element added first is always removed first.
/// Backed by LinkedList — each element is a node with a pointer to the next.
///
/// Internal structure:
///   HEAD → [A] → [B] → [C] → TAIL
///   offer() adds to TAIL
///   poll()  removes from HEAD
///
/// LinkedList as Queue:
///   + unbounded (no fixed capacity)
///   + allows null elements
///   - extra memory per node (pointer overhead)
///   - poor cache locality (nodes scattered in heap)
///   → Prefer ArrayDeque when you just need a fast FIFO (Level 5)
///
/// Real-world: BFS graph traversal, print spooler, task scheduling (FCFS)

public class Level2_LinearQueue {

    public static void main(String[] args) {

        Queue<String> queue = new LinkedList<>();

        // ── basic FIFO behavior ──
        System.out.println("── Enqueue ──");
        queue.offer("Customer-1");
        queue.offer("Customer-2");
        queue.offer("Customer-3");
        System.out.println("Queue: " + queue); // [Customer-1, Customer-2, Customer-3]

        System.out.println("\n── Dequeue (FIFO order) ──");
        while (!queue.isEmpty()) {
            System.out.println("Serving: " + queue.poll()); // Customer-1 first
        }

        // ── BFS using a Queue — canonical use case ──
        System.out.println("\n── BFS traversal using Queue ──");
        // simple tree:   1
        //               / \
        //              2   3
        //             / \
        //            4   5
        int[][] children = { {}, {2, 3}, {4, 5}, {}, {}, {} }; // 1-indexed, node 0 unused

        Queue<Integer> bfsQueue = new LinkedList<>();
        bfsQueue.offer(1); // start from root

        System.out.print("BFS order: ");
        while (!bfsQueue.isEmpty()) {
            int node = bfsQueue.poll();
            System.out.print(node + " ");
            for (int child : children[node]) {
                bfsQueue.offer(child); // enqueue children
            }
        }
        System.out.println();
        // prints: 1 2 3 4 5  (level by level)

        // ── null handling ──
        System.out.println("\n── LinkedList allows null ──");
        Queue<String> withNull = new LinkedList<>();
        withNull.offer(null); // allowed
        withNull.offer("X");
        System.out.println("peek(): " + withNull.peek()); // null — ambiguous with empty!
        // ↑ this is why ArrayDeque (which rejects nulls) is safer for queue use

        // ── size and contains ──
        Queue<Integer> nums = new LinkedList<>();
        for (int i = 1; i <= 5; i++) nums.offer(i);
        System.out.println("\nsize()    : " + nums.size());       // 5
        System.out.println("contains(3): " + nums.contains(3));  // true — O(n) scan
    }
}
