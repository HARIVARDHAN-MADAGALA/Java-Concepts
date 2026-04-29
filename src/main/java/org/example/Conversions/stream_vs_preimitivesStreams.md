# ☕ Stream Hierarchy and Why Only `Stream<T>` Comes from Collections

## 🧩 1️⃣ Where `stream()` Comes From

Defined in `java.util.Collection<E>`:

```java
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}
```

✅ So, **all `Collection` types** (`List`, `Set`, etc.) get this method by default.

Example:
```java
List<Integer> list = List.of(1, 2, 3);
Stream<Integer> s = list.stream();  // ✅ works
```

---

## 🚫 2️⃣ Why There’s No `IntStream`, `LongStream`, or `DoubleStream` in Collections

There are **no methods like:**
```java
intStream()
longStream()
doubleStream()
```
in `Collection`.

### Reason:
- A `Collection<E>` stores **objects (`E`)**, not **primitives (`int`, `long`, `double`)**.
- Java generics **don’t support primitives**, only object types.
- Therefore, `Collection` can only return `Stream<E>` — not specialized primitive streams.

---

## 🧠 3️⃣ How We Get `IntStream`, `LongStream`, and `DoubleStream`

From **mapping methods** inside the `Stream<T>` interface:

```java
Stream<Integer> s = list.stream();

IntStream intStream = s.mapToInt(Integer::intValue);
LongStream longStream = s.mapToLong(Integer::longValue);
DoubleStream doubleStream = s.mapToDouble(Double::valueOf);
```

Defined as:
```java
IntStream mapToInt(ToIntFunction<? super T> mapper);
LongStream mapToLong(ToLongFunction<? super T> mapper);
DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);
```

---

## 🧱 4️⃣ Class Hierarchy (Stream Family)

```
AutoCloseable
   ↑
BaseStream<T, S>
   ↑
 ┌─────────────┼──────────────────┐
 │             │                  │
Stream<T>   IntStream        LongStream
                                 │
                           DoubleStream
```

All of them extend `BaseStream`, but are **specialized** for different data types.

---

## ⚙️ 5️⃣ Primitive Stream Purpose

| Stream Type | Base | Holds | Benefit |
|--------------|------|--------|----------|
| `Stream<T>` | `BaseStream<T, Stream<T>>` | Objects | General-purpose |
| `IntStream` | `BaseStream<Integer, IntStream>` | int | No boxing/unboxing |
| `LongStream` | `BaseStream<Long, LongStream>` | long | Performance |
| `DoubleStream` | `BaseStream<Double, DoubleStream>` | double | Performance |

✅ Primitive streams exist mainly for **speed** — they avoid creating wrapper objects like `Integer`, `Long`, or `Double`.

---

## 🔁 6️⃣ Summary Table

| Stream Type | How You Get It | Example | Defined In |
|--------------|----------------|----------|-------------|
| `Stream<T>` | From `Collection.stream()` | `list.stream()` | `Collection` |
| `IntStream` | From `Stream.mapToInt()` | `list.stream().mapToInt(Integer::intValue)` | `Stream` |
| `LongStream` | From `Stream.mapToLong()` | `stream.mapToLong(...)` | `Stream` |
| `DoubleStream` | From `Stream.mapToDouble()` | `stream.mapToDouble(...)` | `Stream` |

---

## 🧩 Final Summary

- ✅ `Collection` can only return **`Stream<T>`** (object stream).
- 🚫 It cannot return **`IntStream`, `LongStream`, or `DoubleStream`** directly.
- ✅ You can **convert** a `Stream<T>` to a primitive stream using **mapping methods**.
- ✅ All streams (`Stream`, `IntStream`, `LongStream`, `DoubleStream`) **extend `BaseStream`**.
