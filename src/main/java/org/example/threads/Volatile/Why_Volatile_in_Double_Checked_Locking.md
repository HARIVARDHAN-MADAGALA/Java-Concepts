# Why `volatile` is Required in Double-Checked Locking (DCL)

## Object Creation

`instance = new Singleton();` is internally:

1.  Allocate memory
2.  Run constructor (initialize object)
3.  Assign reference to `instance`

## Normal Flow

    Allocate Memory
          ↓
    Run Constructor
          ↓
    Assign Reference

The object is fully initialized before other threads can access it.

## Possible JIT / CPU Optimization

The JIT compiler and CPU may legally reorder instructions:

    Allocate Memory
          ↓
    Assign Reference
          ↓
    Run Constructor

This optimization is allowed because it does not change the behavior of
a single-threaded program.

It does **not always happen**, but **it may happen**.

## The Problem

Thread A:

    Allocate Memory
          ↓
    Assign Reference
          ↓
    Constructor Still Running

Thread B:

``` java
if(instance != null){
    return instance;
}
```

Thread B sees a non-null reference and starts using the object before
construction finishes.

Result: **Partially constructed object.**

## House Analogy

Correct order:

    Buy Land
       ↓
    Build House
       ↓
    Give Address

Wrong reordered order:

    Buy Land
       ↓
    Give Address
       ↓
    Build House

Your friend reaches an unfinished house.

## Solution: volatile

``` java
private static volatile Singleton instance;
```

`volatile` prevents the reference from being published before the
constructor finishes.

Safe order:

    Allocate Memory
          ↓
    Run Constructor
          ↓
    Assign Reference

Now every thread sees a fully initialized object.

## Interview Summary

Without `volatile`, the JVM/JIT may reorder object creation so another
thread sees a non-null reference before the constructor completes.

With `volatile`, instruction reordering is prevented for this variable
and a happens-before relationship ensures the object is fully
initialized before its reference becomes visible.
