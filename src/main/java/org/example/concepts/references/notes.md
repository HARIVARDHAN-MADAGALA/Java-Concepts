# WeakReference, SoftReference, PhantomReference in Java

---

## The Problem — Normal (Strong) References

In Java, every object you create normally is a **Strong Reference**:

```java
Employee emp = new Employee();  // strong reference
```

As long as `emp` variable exists and points to the object,
the **Garbage Collector (GC) will NEVER collect it** — even if memory is running low.

This causes problems:
- **Memory leaks** — objects held in caches/maps never get cleaned up
- **OutOfMemoryError** — JVM runs out of heap because GC can't free anything
- **No way to say "collect this if memory is needed"**

---

## The Solution — Reference Types

Java provides 4 reference types that give you **different levels of control over GC**:

```
Strong  ──►  never collected while reachable           (normal variable)
Soft    ──►  collected only when JVM is LOW on memory  (good for caches)
Weak    ──►  collected at NEXT GC run                  (good for listeners, maps)
Phantom ──►  collected, then enqueued for cleanup      (good for resource cleanup)
```

All non-strong references live in `java.lang.ref` package.

---

## The 4 Reference Types

### Strong Reference (default)
Normal Java variable. GC never touches it while reachable.
→ see `Stage1_StrongReference.java`

### SoftReference
JVM keeps the object as long as memory is available.
Collected only when JVM is about to throw OutOfMemoryError.
Best for: **in-memory caches**
→ see `Stage2_SoftReference.java`

### WeakReference
Object is collected at the **very next GC cycle** regardless of memory.
Best for: **WeakHashMap, event listeners, avoiding memory leaks**
→ see `Stage3_WeakReference.java`

### PhantomReference
Object is already collected. You get notified via a `ReferenceQueue` AFTER collection.
Best for: **cleanup of native resources (files, sockets, native memory)**
→ see `Stage4_PhantomReference.java`

---

## GC strength order

```
Strong > Soft > Weak > Phantom
```

| Reference | When GC collects | get() returns |
|---|---|---|
| Strong | Never (while reachable) | the object |
| Soft | Only when low on memory | object or null |
| Weak | Next GC run | object or null |
| Phantom | Already collected | always null |

---

## Real world usage

| Reference Type | Used in |
|---|---|
| SoftReference | Image/thumbnail caches, computed result caches |
| WeakReference | `WeakHashMap`, Spring's `@EventListener` cleanup |
| PhantomReference | JVM internals, native memory cleanup (off-heap) |
