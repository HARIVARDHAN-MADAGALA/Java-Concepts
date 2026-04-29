# 🧩 Vector --- Internal Working (Java Collections Framework)

## 📘 1. Introduction

`Vector` is a **legacy class** introduced in Java 1.0, part of the
`java.util` package. It implements a **dynamic array** that can grow or
shrink in size just like `ArrayList`, but with **synchronization** ---
meaning it is **thread-safe**.

Even though `ArrayList` replaced most of its use cases, `Vector` still
exists mainly for backward compatibility.

## ⚙️ 2. Class Hierarchy

    java.lang.Object
       ↳ java.util.AbstractCollection
            ↳ java.util.AbstractList
                 ↳ java.util.Vector<E>

Implements → `List`, `RandomAccess`, `Cloneable`, `Serializable`

## 🧠 3. Internal Data Structure

``` java
protected Object[] elementData;
protected int elementCount;
protected int capacityIncrement;
```

-   `elementData` → Array to store elements\
-   `elementCount` → Number of elements currently stored\
-   `capacityIncrement` → Growth rate (if 0 → doubles capacity)

## 🧩 4. How Vector Works Internally

### 1️⃣ Creation

``` java
Vector<Integer> vector = new Vector<>();
```

Internally:

``` java
public Vector() {
    this(10); // Default capacity = 10
}

public Vector(int initialCapacity) {
    this.elementData = new Object[initialCapacity];
}
```

### 2️⃣ Adding Elements

``` java
vector.add(1);
vector.add(2);
vector.add(3);
```

Internally:

``` java
public synchronized boolean add(E e) {
    ensureCapacityHelper(elementCount + 1);
    elementData[elementCount++] = e;
    return true;
}
```

The `synchronized` keyword ensures thread safety --- only one thread can
execute `add()` at a time.

### Capacity Expansion

``` java
private void ensureCapacityHelper(int minCapacity) {
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement)
                                              : (oldCapacity * 2);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

If capacity is not enough: - If `capacityIncrement` \> 0 → increase by
that number\
- Else → double the size

### 3️⃣ Accessing Elements

``` java
vector.get(2);
```

Internally:

``` java
public synchronized E get(int index) {
    if (index >= elementCount)
        throw new ArrayIndexOutOfBoundsException(index);
    return (E) elementData[index];
}
```

Again synchronized to ensure safe read.

### 4️⃣ Removing Elements

``` java
vector.remove(1);
```

Internally:

``` java
public synchronized E remove(int index) {
    if (index >= elementCount)
        throw new ArrayIndexOutOfBoundsException(index);
    E oldValue = (E) elementData[index];
    int numMoved = elementCount - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index + 1, elementData, index, numMoved);
    elementData[--elementCount] = null;
    return oldValue;
}
```

It uses `System.arraycopy()` to shift elements left --- same as
`ArrayList`.

## 🧩 5. Visual Representation

    Initial: capacity = 10, elementCount = 0
    After adding [A, B, C]: capacity = 10, elementCount = 3

    Index:     0    1    2
    Element:  [A]  [B]  [C]

If 10 elements are added, capacity doubles → 20.

## ⚡ 6. Time Complexity

Operation           Average   Worst   Thread Safe
  ------------------- --------- ------- -------------
add(E e)            O(1)      O(n)    ✅
get(int index)      O(1)      O(1)    ✅
remove(int index)   O(n)      O(n)    ✅
size()              O(1)      O(1)    ✅

## 🧩 7. Synchronization Internals

Every public method in Vector is declared `synchronized`:

``` java
public synchronized void addElement(E obj) { ... }
public synchronized E elementAt(int index) { ... }
```

This ensures **only one thread at a time** can access or modify it,
unlike `ArrayList` which is not synchronized.

However, synchronization adds **overhead**, making Vector slower in
single-threaded contexts.

## 🧩 8. Differences: Vector vs ArrayList

Feature           Vector                           ArrayList
  ----------------- -------------------------------- --------------
Synchronization   ✅ Yes                           ❌ No
Growth            Doubles or custom increment      50% increase
Introduced        Java 1.0                         Java 1.2
Performance       Slower (synchronized)            Faster
Enumeration       Supports                         ❌
Fail-fast         ❌ (Enumeration not fail-fast)   ✅
Preferred         Legacy (avoid)                   Modern use

## 🧩 9. Code Example

``` java
import java.util.*;

public class VectorDemo {
    public static void main(String[] args) {
        Vector<String> vector = new Vector<>(2);
        vector.add("Java");
        vector.add("Python");
        vector.add("C++");

        System.out.println(vector); // [Java, Python, C++]

        vector.remove("Python");
        System.out.println(vector); // [Java, C++]

        System.out.println(vector.capacity()); // 4
    }
}
```

Output:

    [Java, Python, C++]
    [Java, C++]
    4

## 🧩 10. Memory View

    Vector Object
     ├── elementData → [Java, C++, null, null]
     ├── elementCount = 2
     ├── capacityIncrement = 0

## 🧩 11. Enumeration Example

``` java
Enumeration<String> e = vector.elements();
while (e.hasMoreElements()) {
    System.out.println(e.nextElement());
}
```

## 🧑‍💼 12. Common Interview Questions

1️⃣ Why is Vector synchronized?\
→ To ensure thread safety by locking each method.\
2️⃣ Why is Vector slower?\
→ Synchronization overhead even in single-thread use.\
3️⃣ How does Vector expand?\
→ Doubles size if `capacityIncrement` = 0.\
4️⃣ Difference with ArrayList?\
→ Thread-safety, growth rate, legacy design.\
5️⃣ Should we use Vector today?\
→ ❌ No. Prefer `ArrayList` + `Collections.synchronizedList()` or
`CopyOnWriteArrayList`.

## 🧩 13. Summary Table

Feature            Description
  ------------------ ------------------
Structure          Dynamic Array
Thread Safe        ✅
Allows null        ✅
Duplicates         ✅
Ordered            ✅
Random Access      ✅
Default Capacity   10
Expansion          Double or custom
Preferred Today    ❌

## 🧠 Real-World Analogy

Think of Vector as a **locker with a single key** --- only one person
(thread) can use it at a time.\
ArrayList, on the other hand, is like an **open drawer** --- anyone can
access, but risk of conflict exists.
