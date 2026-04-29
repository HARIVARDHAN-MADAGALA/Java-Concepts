# 🧩 LinkedList --- Internal Working (Java Collections Framework)

## 📘 1. Introduction

`LinkedList` is a **doubly linked list** implementation of the `List`
and `Deque` interfaces.\
Unlike `ArrayList`, it **does not use an array** --- each element is
stored in a separate **Node object** connected by references.

## ⚙️ 2. Class Hierarchy

    java.lang.Object
       ↳ java.util.AbstractCollection
            ↳ java.util.AbstractList
                 ↳ java.util.AbstractSequentialList
                      ↳ java.util.LinkedList<E>

Implements → `List`, `Deque`, `Cloneable`, `Serializable`

## 🧠 3. Internal Data Structure

``` java
transient int size = 0;
transient Node<E> first;
transient Node<E> last;

private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;
    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

Each element is wrapped inside a **Node** object →
`Node(prev, item, next)`

## 🧩 4. How LinkedList Works Internally

### 1️⃣ Adding Elements

``` java
LinkedList<String> list = new LinkedList<>();
list.add("A");
list.add("B");
list.add("C");
```

Internally: - First element → both `first` and `last` point to it. - New
element → linked to the previous `last`.

``` java
void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    size++;
}
```

## 🧩 5. Visual Representation

    null ← [A] ↔ [B] ↔ [C] → null

    first → A
    last → C
    size = 3

Each node has `prev` and `next` pointers.

### 2️⃣ Accessing Elements

``` java
list.get(2);
```

Internally traverses from `first` or `last` depending on index:

``` java
Node<E> node(int index) {
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
```

### 3️⃣ Removing Elements

``` java
list.remove(1);
```

Internally:

``` java
E unlink(Node<E> x) {
    final E element = x.item;
    final Node<E> next = x.next;
    final Node<E> prev = x.prev;

    if (prev == null)
        first = next;
    else
        prev.next = next;

    if (next == null)
        last = prev;
    else
        next.prev = prev;

    x.item = null;
    size--;
    return element;
}
```

## ⚡ 6. Time Complexity

Operation                    Average   Worst   Explanation
  ---------------------------- --------- ------- -----------------------
add(E e)                     O(1)      O(1)    Append to tail
add(int index, E e)          O(n)      O(n)    Traversal
get(int index)               O(n)      O(n)    Must traverse
remove(int index)            O(n)      O(n)    Must find node
removeFirst()/removeLast()   O(1)      O(1)    Direct pointer update

## 🧩 7. Differences: ArrayList vs LinkedList

Feature                  ArrayList        LinkedList
  ------------------------ ---------------- --------------------------
Structure                Dynamic Array    Doubly Linked Nodes
Access Speed             O(1)             O(n)
Insert/Delete (middle)   O(n)             O(1) after locating node
Memory                   Compact          Higher (extra pointers)
Random Access            ✅               ❌
Reverse Traversal        ❌               ✅
Use Case                 Frequent reads   Frequent inserts/deletes

## 🧑‍💻 8. Code Example

``` java
import java.util.*;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();
        list.add("A");
        list.add("B");
        list.addFirst("Start");
        list.addLast("End");

        System.out.println(list); // [Start, A, B, End]

        list.remove("B");
        System.out.println(list); // [Start, A, End]

        System.out.println(list.get(1)); // A
    }
}
```

## 🧩 9. Internal Memory View

    LinkedList Object
     ├── first → Node(Start)
     ├── last → Node(End)
     └── size = 3

    Each Node:
     ├── prev
     ├── item
     └── next

## 🧩 10. Important Notes

-   Not synchronized
-   Allows nulls
-   Maintains insertion order
-   Implements both `List` and `Deque`
-   No resizing like ArrayList

## 🧑‍💼 11. Common Interview Questions

1️⃣ How does LinkedList store elements internally? → Doubly linked nodes\
2️⃣ Difference from ArrayList? → Node-based vs Array-based\
3️⃣ Why traverse from both ends? → Efficiency\
4️⃣ Why higher memory? → Extra pointers\
5️⃣ Thread-safe? → No

## 🧩 12. Summary Table

Feature             Description
  ------------------- --------------------------
Structure           Doubly Linked Nodes
Allows null         ✅
Thread Safe         ❌
Duplicates          ✅
Ordered             ✅
Access Speed        O(n)
Insert/Delete       O(1) after locating node
Default Capacity    N/A
Reverse Traversal   ✅

## 🧠 Real-World Analogy

A train where each coach knows who is before and after it.\
If one coach is removed, the two neighbors simply link together --- no
shifting required.
