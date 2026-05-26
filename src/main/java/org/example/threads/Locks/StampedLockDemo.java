package org.example.threads.Locks;

import java.util.concurrent.locks.StampedLock;

/**
 * ============================================================
 *  StampedLock (Java 8+) — The most advanced lock
 * ============================================================
 *
 *  PROBLEM with ReadWriteLock:
 *  → Even READ lock has overhead (acquiring/releasing)
 *  → What if reads are 99% of the time and data rarely changes?
 *  → Can we read WITHOUT acquiring a lock at all?
 *
 *  SOLUTION: StampedLock's OPTIMISTIC READ
 *  → "Read without locking, then CHECK if anyone wrote during read"
 *  → If no write happened → use the data ✅ (zero lock overhead!)
 *  → If a write happened  → fallback to real read lock ✅
 *
 * ============================================================
 *
 *  3 modes of StampedLock:
 *  ┌──────────────────┬──────────────────────────────────────┐
 *  │ Mode             │ When to use                          │
 *  ├──────────────────┼──────────────────────────────────────┤
 *  │ writeLock()      │ Exclusive write — same as WriteLock  │
 *  │ readLock()       │ Shared read — same as ReadLock       │
 *  │ tryOptimisticRead│ Read WITHOUT lock, then validate ✅  │
 *  └──────────────────┴──────────────────────────────────────┘
 *
 *  ⚠️  IMPORTANT DIFFERENCES from ReadWriteLock:
 *  → StampedLock is NOT reentrant (same thread can't re-lock!)
 *  → Returns a "stamp" (long) — you must pass it back to unlock
 *  → More complex — use only when performance is critical
 *
 *  Real-world use: Point/Coordinate class, high-frequency counters
 * ============================================================
 */
public class StampedLockDemo {

    // Shared data — a 2D point (x, y)
    private static double x = 0.0;
    private static double y = 0.0;

    private static final StampedLock stampedLock = new StampedLock();

    // -------------------------------------------------------
    //  WRITE — moves the point (exclusive)
    // -------------------------------------------------------
    static void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock(); // acquire write lock → returns a stamp
        try {
            x += deltaX;
            y += deltaY;
            System.out.println("✏️  WRITE → moved to (" + x + ", " + y + ")  stamp=" + stamp);
        } finally {
            stampedLock.unlockWrite(stamp); // must pass stamp back to unlock
        }
    }

    // -------------------------------------------------------
    //  READ — standard read lock (fallback option)
    // -------------------------------------------------------
    static double distanceWithReadLock() {
        long stamp = stampedLock.readLock(); // shared read lock
        try {
            System.out.println("📖 READ (normal) → reading (" + x + ", " + y + ")");
            return Math.sqrt(x * x + y * y);
        } finally {
            stampedLock.unlockRead(stamp);
        }
    }

    // -------------------------------------------------------
    //  OPTIMISTIC READ — the magic of StampedLock!
    //  → Read WITHOUT acquiring any lock
    //  → Then validate: "was there a write while I was reading?"
    //  → If yes → fallback to regular readLock
    // -------------------------------------------------------
    static double distanceOptimistic() {
        // Step 1: Get an optimistic stamp — NO lock acquired!
        long stamp = stampedLock.tryOptimisticRead();

        // Step 2: Read the data (no lock, super fast)
        double currentX = x;
        double currentY = y;

        // Step 3: VALIDATE — was there a write between step 1 and now?
        if (!stampedLock.validate(stamp)) {
            // Someone wrote while we were reading → our data might be stale!
            // Fallback: acquire a real read lock
            System.out.println("⚠️  Optimistic read FAILED (write happened) → fallback to readLock");
            stamp = stampedLock.readLock();
            try {
                currentX = x; // re-read with lock
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        } else {
            System.out.println("⚡ Optimistic read SUCCESS (no write happened) → zero lock cost!");
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("====== StampedLock Demo ======\n");

        // --- Scenario 1: Optimistic read succeeds (no concurrent write) ---
        System.out.println("--- Scenario 1: Optimistic read with no writer ---");
        move(3.0, 4.0); // set point to (3,4) → distance = 5.0
        double dist1 = distanceOptimistic();
        System.out.println("Distance = " + dist1 + " (expected 5.0)\n");

        // --- Scenario 2: Optimistic read fails (concurrent write) ---
        System.out.println("--- Scenario 2: Optimistic read with concurrent writer ---");

        // Writer thread that constantly moves the point
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(20);
                    move(1.0, 1.0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Writer");

        // Reader thread using optimistic reads
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(30);
                    double d = distanceOptimistic();
                    System.out.println("   → Computed distance: " + String.format("%.2f", d));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Reader");

        writer.start();
        reader.start();
        writer.join();
        reader.join();

        System.out.println("\n====== Lock Comparison Summary ======");
        System.out.println("┌───────────────────────┬──────────┬────────────────────────┐");
        System.out.println("│ Lock Type             │ Reentrant│ Best For               │");
        System.out.println("├───────────────────────┼──────────┼────────────────────────┤");
        System.out.println("│ synchronized          │ ✅       │ Simple critical section │");
        System.out.println("│ ReentrantLock         │ ✅       │ Need timeout/tryLock    │");
        System.out.println("│ ReadWriteLock         │ ✅       │ Many readers, few writes│");
        System.out.println("│ StampedLock           │ ❌       │ Max perf, optimistic    │");
        System.out.println("└───────────────────────┴──────────┴────────────────────────┘");
    }
}
