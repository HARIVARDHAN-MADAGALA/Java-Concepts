# Java Garbage Collection - End-to-End Guide

## 1. Why Garbage Collection Exists

Before Java, developers had to manually free memory.

Example in C/C++:

```c
free(ptr);
```

If developers forgot:
- Memory leaks occurred
- Applications crashed
- Systems became unstable

Java introduced Garbage Collection (GC) to automatically reclaim unused memory.

---

## 2. What Happens When an Application Starts?

When a Java application starts:

1. JVM starts.
2. Heap memory is allocated.
3. Objects are created inside the heap.
4. Garbage Collector starts running as a JVM service.

GC runs throughout the lifetime of the application.

---

## 3. Main Memory Areas

### Heap

Stores objects.

```java
Employee e = new Employee();
```

The Employee object is stored in Heap.

### Stack

Stores:
- Local variables
- Method calls
- Object references

```java
Employee e = new Employee();
```

Reference `e` is stored in Stack.

Object is stored in Heap.

---

## 4. What Is Garbage?

An object becomes garbage when it can no longer be reached from any live reference.

```java
Employee e = new Employee();
e = null;
```

Now no reference points to the object.

Object becomes eligible for GC.

Important:
Eligible for GC ≠ immediately deleted.

---

## 5. Role of Garbage Collector

GC performs:

### Memory Reclamation

Removes unused objects.

### Heap Cleanup

Frees memory for new objects.

### Compaction

Moves objects together to reduce fragmentation.

### Promotion

Moves long-living objects to older generations.

---

## 6. Does GC Run 24/7?

Yes and No.

GC service is active throughout JVM life.

But GC does NOT continuously scan memory every second.

Instead it runs when required.

Triggers include:

- Eden space becomes full
- Old generation thresholds reached
- Memory pressure
- Explicit System.gc() request
- JVM internal decisions

---

## 7. How Frequently Does GC Run?

No fixed interval.

### High Traffic Application

Thousands of objects created every second.

GC may run many times per second.

### Idle Application

Very few objects created.

GC may not run for long periods.

Frequency depends on:

- Allocation rate
- Heap size
- GC algorithm
- Memory pressure

---

## 8. GC Lifecycle

### Step 1

Application creates objects.

### Step 2

Objects become unreachable.

### Step 3

GC identifies unreachable objects.

### Step 4

GC reclaims memory.

### Step 5

Application continues.

Cycle repeats throughout application life.

---

## 9. Reachability States

### Strong Reference

```java
String s = new String("Hello");
```

Object survives.

### Soft Reference

Object survives until memory becomes low.

Used in caches.

### Weak Reference

Object removed during next GC cycle.

### Phantom Reference

Used for advanced cleanup tracking.

---

## 10. Heap Generations

Modern JVM divides heap.

### Young Generation

Contains newly created objects.

Components:

#### Eden Space

New objects created here.

#### Survivor S0

Temporary survivor area.

#### Survivor S1

Temporary survivor area.

Most objects die here.

---

### Old Generation

Objects surviving multiple GCs move here.

Long-living objects:

- Cache entries
- Singleton objects
- Application state

---

## 11. Minor GC

Runs on Young Generation.

Fast.

Process:

1. Eden fills.
2. GC runs.
3. Dead objects removed.
4. Live objects copied to Survivor.

Occurs frequently.

---

## 12. Major GC

Runs on Old Generation.

More expensive.

Occurs less frequently.

---

## 13. Full GC

Entire heap collected.

Includes:

- Young Generation
- Old Generation
- Sometimes Metaspace cleanup

Slowest GC.

Usually avoided.

---

## 14. Stop-The-World (STW)

During some GC phases:

Application threads pause.

```text
Application Running
        |
        V
Stop The World
        |
        V
GC Executes
        |
        V
Application Resumes
```

Goal of modern collectors:

Reduce pause times.

---

## 15. Mark and Sweep Algorithm

Classic GC algorithm.

### Mark Phase

Find live objects.

### Sweep Phase

Remove garbage.

Problem:

Memory fragmentation.

---

## 16. Mark Sweep Compact

Improvement.

### Mark

Find live objects.

### Sweep

Remove garbage.

### Compact

Move objects together.

Reduces fragmentation.

---

## 17. Copying Collector

Used in Young Generation.

Copies live objects.

Ignores dead objects.

Very efficient because most young objects die quickly.

---

## 18. Important GC Components

### Mutator

Application threads.

### Collector Threads

GC worker threads.

### Root Set

Starting points for reachability analysis.

Examples:

- Stack references
- Static variables
- JNI references

---

## 19. Reachability Analysis

GC starts from GC Roots.

If object reachable:

Keep it.

If not reachable:

Collect it.

Example:

```java
A -> B -> C
```

If A is a GC Root:

B and C survive.

If A disappears:

All become eligible.

---

## 20. Evolution of Java Garbage Collectors

### Serial GC

Oldest.

Characteristics:

- Single thread
- Stop-the-world

Suitable:

- Small applications

---

### Parallel GC

Uses multiple GC threads.

Focus:

High throughput.

Default for many older JVMs.

---

### CMS (Concurrent Mark Sweep)

Goal:

Reduce pauses.

Worked concurrently with application.

Problems:

- Fragmentation
- Complex maintenance

Deprecated and removed.

---

### G1 GC (Garbage First)

Introduced for large heaps.

Divides heap into regions.

Goal:

Predictable pause times.

Default GC in modern JVMs.

---

### Shenandoah GC

Very low pause times.

Concurrent compaction.

Useful for large heaps.

---

### ZGC

Ultra-low latency collector.

Pause times typically in milliseconds.

Supports huge heaps.

Modern cloud workloads use it heavily.

---

## 21. How JVM Chooses Objects to Promote

Object survives Minor GC.

Age increases.

Example:

Age 1
Age 2
Age 3

After threshold:

Move to Old Generation.

Called Promotion.

---

## 22. Metaspace and GC

Before Java 8:

PermGen.

After Java 8:

Metaspace.

Stores:

- Class metadata
- Method metadata

If Metaspace fills:

```text
java.lang.OutOfMemoryError: Metaspace
```

---

## 23. Common GC Related Errors

### Java Heap Space

Heap exhausted.

```text
OutOfMemoryError: Java heap space
```

### GC Overhead Limit Exceeded

JVM spends most time in GC.

```text
OutOfMemoryError: GC overhead limit exceeded
```

### Metaspace

```text
OutOfMemoryError: Metaspace
```

---

## 24. Viewing GC Logs

JDK 9+

```bash
-Xlog:gc*
```

Sample:

```text
[2.1s] Young GC
[5.8s] Young GC
[42.3s] Full GC
```

---

## 25. Interview Summary

1. GC automatically removes unreachable objects.
2. Objects become eligible when unreachable.
3. GC frequency depends on allocation rate.
4. Young Generation contains Eden and Survivors.
5. Minor GC is frequent.
6. Major GC is less frequent.
7. Full GC is expensive.
8. G1 is modern default collector.
9. ZGC and Shenandoah focus on low latency.
10. GC uses reachability analysis from GC Roots.
