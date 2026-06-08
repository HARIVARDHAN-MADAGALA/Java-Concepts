package org.example.threads.queues;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/// Level 5 — Deque (Double-Ended Queue)
///
/// Deque = Queue + Stack combined. Insert/remove from BOTH ends in O(1).
///
/// Two main implementations:
///   ArrayDeque  — backed by a resizable ring buffer. Preferred in most cases.
///                 Faster than LinkedList (better cache locality, no node allocation)
///                 Does NOT allow null. NOT thread-safe.
///   LinkedList  — doubly-linked nodes. Allows null. Slightly more overhead.
///                 Use when you need the List interface alongside Deque.
///
/// Method map:
///
///   End    │ Throws exception │ Returns special value
///   ───────┼──────────────────┼──────────────────────
///   Front  │ addFirst(e)      │ offerFirst(e)
///          │ removeFirst()    │ pollFirst()
///          │ getFirst()       │ peekFirst()
///   ───────┼──────────────────┼──────────────────────
///   Back   │ addLast(e)       │ offerLast(e)
///          │ removeLast()     │ pollLast()
///          │ getLast()        │ peekLast()
///
/// As a Queue (FIFO) : offerLast() + pollFirst()
/// As a Stack (LIFO) : push() (=addFirst) + pop() (=removeFirst)
///
/// Java docs explicitly say: prefer ArrayDeque over Stack class for stack use

public class Level5_Deque {

    public static void main(String[] args) {

        // ══ Part 1: As a Queue (FIFO) — offerLast + pollFirst ══
        System.out.println("══ Deque as Queue (FIFO) ══");
        Deque<String> queue = new ArrayDeque<>();
        queue.offerLast("First");
        queue.offerLast("Second");
        queue.offerLast("Third");

        System.out.print("poll order: ");
        while (!queue.isEmpty()) System.out.print(queue.pollFirst() + " "); // First Second Third
        System.out.println();

        // ══ Part 2: As a Stack (LIFO) — push + pop ══
        System.out.println("\n══ Deque as Stack (LIFO) ══");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("Bottom");   // addFirst
        stack.push("Middle");   // addFirst
        stack.push("Top");      // addFirst

        System.out.println("peek(): " + stack.peek()); // Top
        System.out.print("pop order: ");
        while (!stack.isEmpty()) System.out.print(stack.pop() + " "); // Top Middle Bottom
        System.out.println();

        // ══ Part 3: Both ends simultaneously — sliding window / palindrome check ══
        System.out.println("\n══ Both ends — palindrome check ══");
        String word = "racecar";
        Deque<Character> chars = new ArrayDeque<>();
        for (char c : word.toCharArray()) chars.offerLast(c);

        boolean isPalindrome = true;
        while (chars.size() > 1) {
            if (chars.pollFirst() != chars.pollLast()) { isPalindrome = false; break; }
        }
        System.out.println("\"" + word + "\" is palindrome: " + isPalindrome); // true

        // ══ Part 4: ArrayDeque vs LinkedList as Deque ══
        System.out.println("\n══ ArrayDeque vs LinkedList ══");
        Deque<Integer> arrayDeque  = new ArrayDeque<>();
        Deque<Integer> linkedDeque = new LinkedList<>();

        for (int i = 1; i <= 5; i++) {
            arrayDeque.offerLast(i);
            linkedDeque.offerLast(i);
        }

        // LinkedList allows null — ArrayDeque does NOT
        linkedDeque.offerLast(null);
        System.out.println("LinkedList (with null): " + linkedDeque);
        try {
            arrayDeque.offerLast(null); // throws NullPointerException
        } catch (NullPointerException e) {
            System.out.println("ArrayDeque rejected null: NullPointerException");
        }

        // ── peekFirst / peekLast ──
        System.out.println("\nArrayDeque peekFirst: " + arrayDeque.peekFirst()); // 1
        System.out.println("ArrayDeque peekLast : " + arrayDeque.peekLast());  // 5

        // ── why ArrayDeque beats Stack class ──
        // Stack extends Vector — all methods synchronized (unnecessary overhead)
        // ArrayDeque — no sync overhead, faster, modern API
        // java.util.Stack is considered legacy — never use it in new code
    }
}
