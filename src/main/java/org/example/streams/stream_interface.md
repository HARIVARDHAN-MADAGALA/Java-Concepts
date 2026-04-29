# ☕ Java Stream<T> Interface — Abstract Methods Only

This document lists **only** the abstract methods declared directly in the `Stream<T>` interface  
(from `java.util.stream`), excluding inherited ones from `BaseStream`.

---

## 🔹 1️⃣ Intermediate Operations (return Stream)

| Method Signature | Description |
|------------------|-------------|
| `Stream<T> filter(Predicate<? super T> predicate)` | Filters elements based on predicate |
| `<R> Stream<R> map(Function<? super T, ? extends R> mapper)` | Maps elements to another type |
| `<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)` | Flattens nested streams |
| `Stream<T> distinct()` | Removes duplicates (based on equals/hashCode) |
| `Stream<T> sorted()` | Sorts elements naturally |
| `Stream<T> sorted(Comparator<? super T> comparator)` | Sorts using comparator |
| `Stream<T> peek(Consumer<? super T> action)` | Performs an action without consuming stream |
| `Stream<T> limit(long maxSize)` | Truncates stream to given size |
| `Stream<T> skip(long n)` | Skips first n elements |
| `Stream<T> takeWhile(Predicate<? super T> predicate)` *(Java 9+)* | Takes elements while condition holds |
| `Stream<T> dropWhile(Predicate<? super T> predicate)` *(Java 9+)* | Skips elements while condition holds |

---

## 🔹 2️⃣ Primitive Stream Conversions

| Method Signature | Description |
|------------------|-------------|
| `IntStream mapToInt(ToIntFunction<? super T> mapper)` | Maps to IntStream |
| `LongStream mapToLong(ToLongFunction<? super T> mapper)` | Maps to LongStream |
| `DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper)` | Maps to DoubleStream |

---

## 🔹 3️⃣ Terminal Operations

| Method Signature | Description |
|------------------|-------------|
| `void forEach(Consumer<? super T> action)` | Performs action on each element |
| `void forEachOrdered(Consumer<? super T> action)` | Performs action preserving order |
| `Object[] toArray()` | Collects elements into Object array |
| `<A> A[] toArray(IntFunction<A[]> generator)` | Collects into typed array |
| `<R> R collect(Collector<? super T, ?, R> collector)` | Performs reduction using Collector |
| `<R, A> R collect(Supplier<A> supplier, BiConsumer<A, ? super T> accumulator, BiConsumer<A, A> combiner)` | Manual collection version |
| `Optional<T> reduce(BinaryOperator<T> accumulator)` | Reduces elements |
| `T reduce(T identity, BinaryOperator<T> accumulator)` | Reduction with identity |
| `<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)` | Reduction with combiner |
| `Optional<T> min(Comparator<? super T> comparator)` | Finds min element |
| `Optional<T> max(Comparator<? super T> comparator)` | Finds max element |
| `long count()` | Returns element count |
| `boolean anyMatch(Predicate<? super T> predicate)` | Returns true if any match |
| `boolean allMatch(Predicate<? super T> predicate)` | Returns true if all match |
| `boolean noneMatch(Predicate<? super T> predicate)` | Returns true if none match |
| `Optional<T> findFirst()` | Returns first element |
| `Optional<T> findAny()` | Returns any element |

---

## ✅ Summary

| Category | Methods Count | Examples |
|-----------|----------------|-----------|
| Intermediate | 11 | filter, map, flatMap, sorted, etc. |
| Terminal | 16 | forEach, collect, reduce, min, max, etc. |
| Primitive Conversions | 3 | mapToInt, mapToLong, mapToDouble |

**Total: 30 abstract methods defined directly in `Stream<T>`**

---
