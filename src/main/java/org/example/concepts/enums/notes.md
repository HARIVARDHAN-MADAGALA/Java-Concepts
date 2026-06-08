# Enum in Java

---

## The Problem — Before enum existed (pre Java 5)

Developers used `static final int` constants to represent fixed sets of values:

```java
public class Direction {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST  = 2;
    public static final int WEST  = 3;
}
```

### Problems with this approach:
- **No type safety** — you could pass any random int like `move(99)` and compiler won't complain
- **No meaningful print** — printing gave `0`, `1`, `2` instead of NORTH, SOUTH
- **No grouping** — constants scattered across classes, no way to know they belong together
- **No behaviour** — can't add methods or fields to an int constant

---

## The Solution — enum (Java 5, 2004)

Java introduced `enum` as a special class type to represent a **fixed set of named constants**.

```java
public enum Direction {
    NORTH, SOUTH, EAST, WEST;
}
```

Now `move(Direction.NORTH)` — compiler enforces only valid Direction values.

---

## How enum evolved (3 stages)

### Stage 1 — Basic enum (just constants)
Just named constants, type-safe, printable.
→ see `Stage1_BasicEnum.java`

### Stage 2 — enum with fields and methods
Each constant can carry data (like a label or code).
→ see `Stage2_EnumWithFields.java`

### Stage 3 — enum with abstract methods
Each constant can have its **own behaviour**.
→ see `Stage3_EnumWithAbstractMethod.java`

---

## Key built-in features every enum gets for free

| Feature | Description |
|---|---|
| `name()` | Returns the constant name as String |
| `ordinal()` | Returns the position (0-based index) |
| `values()` | Returns array of all constants |
| `valueOf("NORTH")` | Converts String → enum constant |
| `toString()` | Same as name() by default |
| works in `switch` | Enums work natively in switch statements |
| `==` comparison | Safe to use == instead of .equals() |

---

## enum vs static final int — comparison

| | static final int | enum |
|---|---|---|
| Type safe | ❌ | ✅ |
| Readable print | ❌ | ✅ |
| Can have fields | ❌ | ✅ |
| Can have methods | ❌ | ✅ |
| Works in switch | ✅ | ✅ |
| Grouped together | ❌ | ✅ |
