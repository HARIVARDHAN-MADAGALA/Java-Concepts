# 🧱 Avoiding Race Conditions in Java

A **race condition** occurs when two or more threads access shared data simultaneously, and the final outcome depends on the order of execution.

Below are **five ways to prevent race conditions** in Java.

---

## 1️⃣ Use `synchronized` Keyword

Locks a method or block so that only **one thread executes at a time**.

```java
public class SyncExample {
    private static int counter = 0;

    public static synchronized void increment() {
        counter++;
    }
}
```

✅ Simple and reliable
⚠️ Can cause blocking if many threads wait for the lock


---

## 2️⃣ Use `ReentrantLock`

Gives more **flexible control** than `synchronized`.
You can manually acquire or release the lock.

```java
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {
    private static int counter = 0;
    private static final ReentrantLock lock = new ReentrantLock();

    public static void increment() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }
}
```

✅ Fine-grained control
✅ Can use `tryLock()`
⚠️ Must release the lock manually



we can write like this but

public void increment() {
a.lock();
count++;
a.unlock();
}

If something goes wrong **between `lock()` and `unlock()`** (like an exception),
the lock will **never be released**, and other threads will get **stuck forever** waiting for it.

That’s called a **deadlock risk** due to missing `finally`.

---

## 3️⃣ Use `Atomic` Variables

Atomic classes like `AtomicInteger`, `AtomicBoolean`, etc., provide **lock-free, thread-safe** operations.

```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicExample {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void increment() {
        counter.incrementAndGet(); // atomic operation
    }
}
```

✅ Fast and non-blocking
✅ Ideal for counters or flags
⚠️ Only works for simple atomic updates

---

## 4️⃣ Use `volatile` for Visibility (Not Safety)

`volatile` ensures **visibility** of variable updates across threads —
but does **not** make compound operations (like `counter++`) atomic.

```java
public class VolatileExample {
    private static volatile boolean flag = true;

    public static void toggle() {
        flag = !flag; // not atomic, but visibility guaranteed
    }
}
```

✅ Ensures visibility between threads
⚠️ Still not thread-safe for non-atomic updates

---

## 5️⃣ Avoid Shared Mutable State

Design threads so they **don’t share** the same data.

Examples:

- Use **local variables** inside threads
- Use **immutable objects**
- Use **message passing** via queues

```java
public class SafeDesign {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> process("Task1"));
        Thread t2 = new Thread(() -> process("Task2"));

        t1.start();
        t2.start();
    }

    static void process(String taskName) {
        // Each thread has its own data
        int localCounter = 0;
        localCounter++;
        System.out.println(taskName + " -> " + localCounter);
    }
}
```

✅ No synchronization needed
✅ Clean and scalable design

---

## 🧠 Summary


| Method             | Thread-Safety | Blocking | Best Use Case         |
| ------------------ | ------------- | -------- | --------------------- |
| `synchronized`     | ✅            | ⚠️ Yes | Simple shared updates |
| `ReentrantLock`    | ✅            | ⚠️ Yes | Advanced lock control |
| `Atomic` classes   | ✅            | ❌ No    | Counters/flags        |
| `volatile`         | ⚠️ Partial  | ❌ No    | Visibility only       |
| Avoid shared state | ✅            | ❌ No    | Best design practice  |

---

### 💬 In short:

> 🔹 **Race condition** = multiple threads updating shared data concurrently.
> 🔹 **Fix** = synchronize access or remove shared mutable state.
