package org.example.threads.queues;

/// Level 3 — Circular Queue (Ring Buffer)
///
/// A fixed-size array where head and tail pointers wrap around using modulo arithmetic.
/// When tail reaches the end of the array, it wraps to index 0 — reusing empty slots.
///
/// Internal structure (capacity = 5):
///
///   index:  [0]  [1]  [2]  [3]  [4]
///           [ ]  [B]  [C]  [D]  [ ]
///                 ↑              ↑
///                head           tail
///
///   After polling B:
///   head moves to [2], slot [1] is free for reuse
///
/// Key formula:
///   tail = (tail + 1) % capacity   ← wrap around
///   head = (head + 1) % capacity   ← wrap around
///
/// Full condition : (tail + 1) % capacity == head
/// Empty condition: head == tail
///
/// Java doesn't have a built-in CircularQueue class.
/// ArrayDeque and ArrayBlockingQueue use this ring-buffer technique internally.
///
/// Real-world: I/O stream buffers, audio/video streaming, CPU scheduling (Round Robin)

public class Level3_CircularQueue {

    static class CircularQueue<T> {

        private final Object[] buffer;
        private int head = 0; // points to the front element
        private int tail = 0; // points to the next empty slot
        private int size = 0;
        private final int capacity;

        CircularQueue(int capacity) {
            this.capacity = capacity;
            this.buffer   = new Object[capacity];
        }

        // ── enqueue — O(1) ──
        boolean offer(T item) {
            if (isFull()) return false;
            buffer[tail] = item;
            tail = (tail + 1) % capacity; // wrap around
            size++;
            return true;
        }

        // ── dequeue — O(1) ──
        @SuppressWarnings("unchecked")
        T poll() {
            if (isEmpty()) return null;
            T item = (T) buffer[head];
            buffer[head] = null;           // help GC
            head = (head + 1) % capacity;  // wrap around
            size--;
            return item;
        }

        @SuppressWarnings("unchecked")
        T peek() {
            return isEmpty() ? null : (T) buffer[head];
        }

        boolean isFull()  { return size == capacity; }
        boolean isEmpty() { return size == 0; }
        int size()        { return size; }

        @Override
        public String toString() {
            if (isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < size; i++) {
                sb.append(buffer[(head + i) % capacity]);
                if (i < size - 1) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }

    public static void main(String[] args) {

        CircularQueue<String> cq = new CircularQueue<>(5);

        // ── fill the queue ──
        System.out.println("── Enqueue ──");
        System.out.println("offer(A): " + cq.offer("A")); // true
        System.out.println("offer(B): " + cq.offer("B")); // true
        System.out.println("offer(C): " + cq.offer("C")); // true
        System.out.println("offer(D): " + cq.offer("D")); // true
        System.out.println("offer(E): " + cq.offer("E")); // true
        System.out.println("offer(F): " + cq.offer("F")); // false — full
        System.out.println("Queue: " + cq);               // [A, B, C, D, E]

        // ── dequeue frees slots ──
        System.out.println("\n── Dequeue ──");
        System.out.println("poll(): " + cq.poll()); // A — head moves, slot 0 freed
        System.out.println("poll(): " + cq.poll()); // B — head moves, slot 1 freed
        System.out.println("Queue: " + cq);         // [C, D, E]

        // ── key insight: slots 0 and 1 are reused without shifting any elements ──
        System.out.println("\n── Reusing freed slots (wrap-around) ──");
        System.out.println("offer(F): " + cq.offer("F")); // true — tail wraps to slot 0
        System.out.println("offer(G): " + cq.offer("G")); // true — tail wraps to slot 1
        System.out.println("offer(H): " + cq.offer("H")); // false — full again (size=5)
        System.out.println("Queue: " + cq);               // [C, D, E, F, G]

        // ── drain in FIFO order ──
        System.out.println("\n── FIFO drain ──");
        while (!cq.isEmpty()) System.out.print(cq.poll() + " "); // C D E F G
        System.out.println();

        // ── comparison with linear queue ──
        // Linear array queue: poll() shifts all elements left → O(n)
        // Circular queue    : poll() just moves head pointer   → O(1)
        // Memory            : no wasted slots — wraps and reuses
    }
}
