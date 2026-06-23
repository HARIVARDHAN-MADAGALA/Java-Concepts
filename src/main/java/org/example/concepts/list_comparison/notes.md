# Arrays.asList() vs List.of() vs Collections.unmodifiableList()

---

## The Core Question — All three give you a "read-ish" list. What's the difference?

---

## 1. Arrays.asList()

```java
List<Integer> l = Arrays.asList(1, 2, 3);
```

### Internal Working
- Returns `Arrays$ArrayList` — a **private static inner class** inside `java.util.Arrays`
- It is NOT `java.util.ArrayList`
- Backed directly by the original array — shares the same memory

```
int[] arr = {1, 2, 3}
               ↑
        Arrays$ArrayList  ←  l
        (just a wrapper, no copy)
```

### What you can and cannot do

| Operation       | Allowed? | Why |
|----------------|----------|-----|
| `get(i)`        | ✅       | reads from backing array |
| `set(i, val)`   | ✅       | mutates the backing array |
| `add()`         | ❌       | `UnsupportedOperationException` — fixed size |
| `remove()`      | ❌       | `UnsupportedOperationException` — fixed size |

### Key behavior — backed by array (two-way sync)

```java
String[] arr = {"a", "b", "c"};
List<String> l = Arrays.asList(arr);

arr[0] = "X";
System.out.println(l.get(0)); // X ← list reflects array change

l.set(1, "Y");
System.out.println(arr[1]);   // Y ← array reflects list change
```

### Null allowed?
```java
List<String> l = Arrays.asList("a", null, "c"); // ✅ nulls allowed
```

---

## 2. List.of()

```java
List<Integer> l = List.of(1, 2, 3);
```

### Internal Working
- Returns one of several `ImmutableCollections$ListN` / `List12` classes (JDK internal)
- Stores elements directly in fields (for small lists) or an `Object[]` array (for larger)
- **No backing array** — completely independent copy

```
List.of(1, 2, 3)
    → ImmutableCollections$ListN { Object[] elements = {1, 2, 3} }
    (no reference to original source)
```

### What you can and cannot do

| Operation       | Allowed? | Why |
|----------------|----------|-----|
| `get(i)`        | ✅       | reads internal array |
| `set(i, val)`   | ❌       | `UnsupportedOperationException` — truly immutable |
| `add()`         | ❌       | `UnsupportedOperationException` |
| `remove()`      | ❌       | `UnsupportedOperationException` |

### Null NOT allowed

```java
List.of("a", null); // ❌ NullPointerException at creation time
```

### No array sync — independent copy

```java
String[] arr = {"a", "b"};
List<String> l = List.of(arr);

arr[0] = "X";
System.out.println(l.get(0)); // a ← NOT affected, no backing link
```

---

## 3. Collections.unmodifiableList()

```java
List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2, 3));
List<Integer> l = Collections.unmodifiableList(mutable);
```

### Internal Working
- Returns `Collections$UnmodifiableList` — a **wrapper/view** over your original list
- Does NOT copy the data — holds a reference to the original list

```
ArrayList { 1, 2, 3 }
       ↑
UnmodifiableList  ←  l
(just a view, same reference)
```

### What you can and cannot do

| Operation           | Allowed? | Why |
|--------------------|----------|-----|
| `get(i)`            | ✅       | delegates to original list |
| `set(i, val)`       | ❌       | `UnsupportedOperationException` — wrapper blocks it |
| `add()`             | ❌       | `UnsupportedOperationException` |
| `remove()`          | ❌       | `UnsupportedOperationException` |
| modify original list | ✅      | unmodifiable view reflects the change! |

### Key behavior — still reflects changes to the original list

```java
List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2, 3));
List<Integer> view = Collections.unmodifiableList(mutable);

mutable.add(99);
System.out.println(view); // [1, 2, 3, 99] ← view updated!
```

This is the biggest trap — it's unmodifiable, NOT immutable.

### Null allowed?
```java
List<String> mutable = new ArrayList<>();
mutable.add(null);
List<String> view = Collections.unmodifiableList(mutable); // ✅ nulls allowed
```

---

## Side-by-Side Comparison

| Feature                        | `Arrays.asList()` | `List.of()`  | `Collections.unmodifiableList()` |
|-------------------------------|-------------------|--------------|----------------------------------|
| `set()` allowed               | ✅                | ❌           | ❌                               |
| `add()` / `remove()` allowed  | ❌                | ❌           | ❌                               |
| Null elements                 | ✅                | ❌           | ✅                               |
| Backed by original array/list | ✅ (array)        | ❌           | ✅ (list)                        |
| Truly immutable               | ❌                | ✅           | ❌                               |
| Returns type                  | `Arrays$ArrayList`| `ImmutableCollections$ListN` | `Collections$UnmodifiableList` |
| Introduced in                 | Java 1.2          | Java 9       | Java 1.2                         |

---

## The Trap Summary

```
Arrays.asList()
  → fixed size, but elements are mutable via set()
  → changes to original array reflect in the list and vice versa

List.of()
  → fully immutable — nothing can change, no nulls
  → safest option when you want a constant list

Collections.unmodifiableList()
  → only the VIEW is unmodifiable
  → if someone holds reference to original list and modifies it,
     your "unmodifiable" list changes too ⚠️
```

---

## When to use what

```
Need a quick fixed list from known values, no changes at all?
    → List.of()  ✅ (safest, most modern)

Wrapping an existing mutable list to hand it out safely?
    → Collections.unmodifiableList()  ✅

Converting an array to a list and might need set()?
    → Arrays.asList()  ✅

Need full mutability (add/remove)?
    → new ArrayList<>(Arrays.asList(...))  ✅
```
