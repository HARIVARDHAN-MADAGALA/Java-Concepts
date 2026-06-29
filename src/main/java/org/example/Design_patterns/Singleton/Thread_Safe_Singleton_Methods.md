# Thread-Safe Singleton in Java

## What is a Singleton?

A **Singleton** is a design pattern that ensures:

1.  Only **one object** of a class is created.
2.  A **global access point** is provided to that object.

Example:

``` java
Singleton obj = Singleton.getInstance();
```

------------------------------------------------------------------------

# Why Thread Safety?

If two threads call `getInstance()` simultaneously, both may see
`instance == null` and each create a new object.

    Thread 1                 Thread 2
    ---------                ---------
    instance == null ✓
                             instance == null ✓
    create object
                             create object

Result: **Two instances are created**, violating the Singleton pattern.

------------------------------------------------------------------------

# 1. Lazy Initialization (Not Thread-Safe)

``` java
public class Singleton {

    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### Advantages

-   Lazy creation.
-   Simple implementation.

### Disadvantages

-   Not thread-safe.
-   Multiple instances may be created.

------------------------------------------------------------------------

# 2. Synchronized Method Singleton

``` java
public class Singleton {

    private static Singleton instance;

    private Singleton() {}

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### How it works

Only one thread can execute `getInstance()` at a time.

### Advantages

-   Thread-safe.
-   Easy to understand.

### Disadvantages

-   Every call acquires a lock.
-   Slower after the object has already been created.

------------------------------------------------------------------------

# 3. Double-Checked Locking (Recommended)

``` java
public class Singleton {

    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {

        if (instance == null) {

            synchronized (Singleton.class) {

                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }

        return instance;
    }
}
```

### Why two checks?

First check: - Avoids locking after the object has been created.

Second check: - Ensures another thread did not create the object while
waiting for the lock.

### Why `volatile`?

Without `volatile`, instruction reordering could expose a partially
initialized object to another thread.

### Advantages

-   Thread-safe.
-   Lazy initialization.
-   Excellent performance.

### Disadvantages

-   Slightly more complex.

------------------------------------------------------------------------

# 4. Eager Initialization

``` java
public class Singleton {

    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

### How it works

The instance is created when the class is loaded by the JVM.

### Advantages

-   Thread-safe.
-   Very simple.
-   No synchronization required.

### Disadvantages

-   Object is created even if never used.

------------------------------------------------------------------------

# 5. Bill Pugh Singleton (Initialization-on-Demand Holder)

``` java
public class Singleton {

    private Singleton() {}

    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

### How it works

The inner class is loaded only when `getInstance()` is called.

### Advantages

-   Thread-safe.
-   Lazy initialization.
-   No synchronization overhead.
-   Widely recommended.

### Disadvantages

-   Requires understanding of JVM class loading.

------------------------------------------------------------------------

# 6. Enum Singleton

``` java
public enum Singleton {
    INSTANCE;
}
```

Usage:

``` java
Singleton obj = Singleton.INSTANCE;
```

### Advantages

-   Thread-safe.
-   Prevents serialization issues.
-   Resistant to reflection attacks.
-   Simplest implementation.

### Disadvantages

-   Not suitable if the class must extend another class.

------------------------------------------------------------------------

# Comparison

  Method                   Thread-Safe   Lazy Initialization        Performance
  ------------------------ ------------- -------------------------- -------------
  Lazy Initialization      ❌            ✅                         Excellent
  Synchronized Method      ✅            ✅                         Moderate
  Double-Checked Locking   ✅            ✅                         Excellent
  Eager Initialization     ✅            ❌                         Excellent
  Bill Pugh Holder         ✅            ✅                         Excellent
  Enum Singleton           ✅            Enum loaded on first use   Excellent

------------------------------------------------------------------------

# Interview Recommendation

1.  Explain why a normal lazy singleton is **not thread-safe**.
2.  Explain how `synchronized` solves the problem but impacts
    performance.
3.  Explain **Double-Checked Locking** and the need for `volatile`.
4.  Mention **Bill Pugh** as a preferred lazy, thread-safe
    implementation.
5.  Mention **Enum Singleton** as the safest and simplest implementation
    in Java.
