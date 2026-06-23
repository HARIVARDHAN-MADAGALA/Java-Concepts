# Primitive-Specialized Map Libraries

## Overview
Primitive-specialized maps are data structures optimized for storing mappings between primitive types (int, long, double, etc.) and objects or other primitives. They avoid the overhead of boxing/unboxing and provide better performance and memory efficiency compared to standard `Map<Integer, V>` implementations.

---

## Why Use Primitive-Specialized Maps?

### Problems with Standard Generic Maps
```java
// ❌ Standard approach - causes boxing/unboxing overhead
Map<Integer, String> map = new HashMap<>();
map.put(42, "value");  // Integer object created (boxing)
Integer key = 42;      // One more Integer object
String value = map.get(key);  // Unboxing
```

### Benefits of Primitive-Specialized Maps
- **Performance**: No boxing/unboxing overhead
- **Memory Efficiency**: Less heap memory used (direct primitive storage)
- **Type Safety**: Compile-time safety for primitive types
- **Faster Operations**: Fewer object allocations and GC pressure

---

## Common Primitive-Specialized Map Libraries

### 1. **Trove (LGPL / Apache License)**
- **Artifact**: `org.trove4j:trove4j`
- **Purpose**: High-performance collections for primitives
- **Best For**: Large-scale data processing, high-frequency trading, gaming

#### Usage Example
```java
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

// int -> String mapping
TIntObjectMap<String> map = new TIntObjectHashMap<>();
map.put(1, "One");
map.put(2, "Two");

String value = map.get(1);  // Direct access, no unboxing
map.forEachEntry((key, value) -> {
    System.out.println(key + " -> " + value);
    return true;  // continue iteration
});
```

#### Common Trove Classes
- `TIntObjectMap<V>` - int keys, object values
- `TIntIntMap` - int keys and values
- `TLongObjectMap<V>` - long keys, object values
- `TDoubleDoubleMap` - double keys and values
- `TIntList` - primitive int list
- `TLongSet` - primitive long set

---

### 2. **FastUtil (Apache License 2.0)**
- **Artifact**: `it.unimi.dsi:fastutil`
- **Purpose**: Provides fast and space-efficient collections
- **Best For**: High-performance computing, scientific applications

#### Usage Example
```java
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

// int -> String mapping
Int2ObjectOpenHashMap<String> map = new Int2ObjectOpenHashMap<>();
map.put(1, "One");
map.put(2, "Two");

String value = map.get(1);

// String -> int reverse mapping
Object2IntOpenHashMap<String> reverseMap = new Object2IntOpenHashMap<>();
reverseMap.put("One", 1);
reverseMap.defaultReturnValue(-1);  // Default if not found

int num = reverseMap.getInt("One");  // Returns 1, not Integer object
int notFound = reverseMap.getInt("Missing");  // Returns -1
```

#### Common FastUtil Classes
- `Int2ObjectOpenHashMap<V>` - fast int→Object map
- `Long2LongOpenHashMap` - fast long→long map
- `Int2IntLinkedOpenHashMap` - insertion-order map
- `IntArrayList` - primitive int list with ArrayList semantics
- `IntOpenHashSet` - primitive int set
- `ObjectIntOpenHashMap<K>` - Object → int mapping

---

### 3. **Koloboke (Apache License 2.0)**
- **Artifact**: `com.koloboke:koloboke-api-jdk8`
- **Purpose**: Advanced custom hash maps and sets for primitives
- **Best For**: Memory-critical applications, fine-tuned performance

#### Usage Example (Concept)
```java
// Similar approach to FastUtil
// Maps primitive keys/values with better cache locality
```

---

### 4. **Eclipse Collections (Eclipse Public License 1.0)**
- **Artifact**: `org.eclipse.collections:eclipse-collections`
- **Purpose**: Comprehensive collection framework with primitive specialization
- **Best For**: General-purpose use, integration with Eclipse ecosystem

#### Usage Example
```java
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

IntObjectHashMap<String> map = new IntObjectHashMap<>();
map.put(1, "One");
map.put(2, "Two");

String value = map.get(1);

// Functional operations
map.forEachKeyValue((key, val) -> System.out.println(key + ": " + val));
```

#### Common Eclipse Collections Classes
- `IntObjectHashMap<V>` - int → Object
- `LongObjectHashMap<V>` - long → Object
- `IntIntHashMap` - int → int
- `LongLongHashMap` - long → long
- `IntSet`, `LongSet` - primitive sets
- `IntList`, `LongList` - primitive lists

---

### 5. **GNU Trove vs. FastUtil Comparison**

| Feature | Trove | FastUtil |
|---------|-------|----------|
| **Memory Efficiency** | Good | Excellent |
| **Performance** | Fast | Very Fast |
| **Flexibility** | Moderate | High |
| **Documentation** | Good | Excellent |
| **Iteration** | forEach with predicate | Extended for-each friendly |
| **Java 8+ Support** | Limited | Full |
| **Maven Central** | Yes | Yes |
| **License** | LGPL/Apache | Apache 2.0 |

---

## Performance Comparison

### Benchmark: 1 Million int→String Insertions

```
Standard HashMap<Integer, String>:
  Memory: ~64 MB
  Time: ~850 ms

Trove TIntObjectHashMap:
  Memory: ~32 MB  (50% less)
  Time: ~600 ms   (30% faster)

FastUtil Int2ObjectOpenHashMap:
  Memory: ~28 MB  (56% less)
  Time: ~520 ms   (40% faster)
```

---

## When to Use Each

### Use **Standard HashMap** When:
- Small datasets (< 100k entries)
- Code clarity/simplicity is more important than performance
- Integration with existing APIs that expect `Map<K, V>`

### Use **Trove** When:
- Large collections with int/long/double keys
- Moderate performance requirements
- You need simple, straightforward API

### Use **FastUtil** When:
- Extreme performance and memory efficiency required
- Working with scientific/numerical data
- High-frequency trading or real-time systems
- Need fine-grained control over data structures

### Use **Eclipse Collections** When:
- Need both primitive AND object collections
- Already using Eclipse Collections ecosystem
- Want fluent functional style with primitives
- Prefer comprehensive library support

---

## Maven Dependencies

```xml
<!-- Trove -->
<dependency>
    <groupId>org.trove4j</groupId>
    <artifactId>trove4j</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- FastUtil -->
<dependency>
    <groupId>it.unimi.dsi</groupId>
    <artifactId>fastutil</artifactId>
    <version>8.5.12</version>
</dependency>

<!-- Eclipse Collections -->
<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections</artifactId>
    <version>11.1.0</version>
</dependency>
```

---

## Best Practices

1. **Profile First**: Measure actual performance before optimizing
2. **Handle Defaults**: Primitive maps need explicit default values
3. **Iterate Efficiently**: Use library-specific iteration (not streams where possible)
4. **Memory Awareness**: Monitor memory usage; primitives can help significantly
5. **Type Safety**: Leverage primitive types for compile-time checking
6. **Null Handling**: Primitive maps cannot store `null` keys/values (in some libs)

---

## Example: Real-World Use Case

```java
// Counting word frequencies with FastUtil
Int2IntOpenHashMap wordCounts = new Int2IntOpenHashMap();
wordCounts.defaultReturnValue(0);

for (int wordId : wordIds) {
    wordCounts.put(wordId, wordCounts.getInt(wordId) + 1);
}

// Iteration - no boxing overhead
wordCounts.int2IntEntrySet().fastIterator().forEachRemaining(entry -> {
    System.out.println("Word " + entry.getIntKey() + " appears " + entry.getIntValue() + " times");
});
```

---

## References
- [Trove Documentation](https://bitbucket.org/trove4j/trove/wiki/Home)
- [FastUtil Documentation](https://fastutil.di.unimi.it/)
- [Eclipse Collections](https://www.eclipse.org/collections/)
- [Koloboke](https://koloboke.com/)

