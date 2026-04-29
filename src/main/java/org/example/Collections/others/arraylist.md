# 🧩 ArrayList --- Internal Working (Java Collections Framework)

## 📘 1. Introduction

`ArrayList` is a **resizable array** implementation of the `List`
interface.\
It stores elements **in an array internally** and grows automatically
when more elements are added.

## ⚙️ 2. Class Hierarchy

    java.lang.Object
       ↳ java.util.AbstractCollection
            ↳ java.util.AbstractList
                 ↳ java.util.ArrayList<E>

Implements → `List`, `RandomAccess`, `Cloneable`, `Serializable`

## 🧠 3. Internal Data Structure

Internally, it uses:

``` java
transient Object[] elementData; // stores the actual elements
private int size;                // number of elements currently stored
```

## 🧩 4. How ArrayList Works Internally

### 1️⃣ Initialization

``` java
List<Integer> list = new ArrayList<>();
```

Initially, `elementData` array is empty until the first element is
added.

### 2️⃣ Adding Elements

If capacity is full → it **grows automatically**.

Growth formula (Java 8+):

``` java
newCapacity = oldCapacity + (oldCapacity >> 1);
```

→ 1.5x growth

### 3️⃣ Internal Method (JDK 17)

``` java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    if (elementData.length < minCapacity)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity < minCapacity)
        newCapacity = minCapacity;
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

## 🧮 5. Visual Representation

    Before Adding Elements:
    elementData = []

    After adding 3 elements:
    [10, 20, 30, null, null, null, null, null, null, null]

    Capacity = 10
    Size = 3

    When adding 11th element → grows to 15 capacity

## 🧹 6. Removing Elements

``` java
list.remove(1);
```

Internally:

``` java
System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
elementData[--size] = null;
```

## ⚡ 7. Time Complexity

Operation             Average          Worst   Explanation
  --------------------- ---------------- ------- ---------------------------------
add(E e)              O(1) amortized   O(n)    Only O(n) when resizing happens
add(int index, E e)   O(n)             O(n)    Shifts elements
get(int index)        O(1)             O(1)    Direct array access
remove(int index)     O(n)             O(n)    Shifts elements
contains(Object o)    O(n)             O(n)    Linear search

## 🧠 8. Important Notes

-   Not synchronized → use `Collections.synchronizedList()` for thread
    safety.
-   Allows null values.
-   Maintains insertion order.
-   Fast random access.
-   Slow insertion/removal in middle.

## 🧑‍💻 9. Code Example

``` java
import java.util.*;

public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        list.add("C++");

        System.out.println(list); // [Java, Python, C++]

        list.remove("Python");
        System.out.println(list); // [Java, C++]

        System.out.println(list.get(1)); // C++
    }
}
```

## 🧩 10. Internal Memory View

    ArrayList Object
     ├── elementData → [Java, Python, C++, null, null, ...]
     ├── size = 3

## 🧑‍💼 11. Common Interview Questions

1️⃣ Default capacity? → 10\
2️⃣ Growth? → 1.5x\
3️⃣ Not good for middle inserts/deletes? → Shifting cost O(n)\
4️⃣ Thread-safe? → No\
5️⃣ Difference between size & capacity?\
- size = actual elements\
- capacity = internal array length\
  6️⃣ Why transient? → For custom serialization.

## 🧩 12. Summary Table

Feature                Description
  ---------------------- ----------------------------
Internal Structure     Dynamic Array (`Object[]`)
Default Capacity       10
Growth Factor          1.5x
Allows null            ✅
Thread Safe            ❌
Ordered                ✅
Duplicates             ✅
Access Speed           O(1)
Insert/Delete Middle   O(n)

## 🧠 Real-World Analogy

A train with compartments (array).\
If full, railway adds 1.5x more compartments and moves all passengers to
new train → resizing.
