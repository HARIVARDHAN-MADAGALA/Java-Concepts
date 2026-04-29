# Pattern Matching (Java 17, finalized)

## The Problem it solved

Before pattern matching, whenever you used `instanceof` to check a type, you had to do **three separate steps**:

java

```java
// Old way
Object obj = "Hello Harish";

if (obj instanceof String) {          // Step 1 - check type
    String s = (String) obj;          // Step 2 - cast manually
    System.out.println(s.length());   // Step 3 - use it
}
```

Three lines just to **check and use** a type. And that manual cast on line 2 is:

* **Redundant** — you already checked it's a String on line 1
* **Error prone** — you could accidentally cast to the wrong type
* **Noisy** — adds clutter with no real logic

This got **worse** when you had multiple types to handle:

java

```java
// Old way with multiple types
Object obj = getShape();

if (obj instanceof Circle) {
    Circle c = (Circle) obj;      // cast again
    System.out.println(c.area());
} else if (obj instanceof Rectangle) {
    Rectangle r = (Rectangle) obj; // cast again
    System.out.println(r.area());
} else if (obj instanceof Triangle) {
    Triangle t = (Triangle) obj;   // cast again
    System.out.println(t.area());
}
```

Every branch — **check, cast, use**. Same pattern repeated endlessly.

---

## The Solution — Pattern Matching for instanceof

Java 17 combined the **check + cast into one step**:

java

```java
// New way
Object obj = "Hello Harish";

if (obj instanceof String s) {        // check + cast + bind in ONE step
    System.out.println(s.length());   // s is ready to use directly
}
```

The variable `s` here is called a **pattern variable** — it is automatically cast and available inside the `if` block.

---

## Multiple types — much cleaner now:

java

```java
// New way
Object obj = getShape();

if (obj instanceof Circle c) {
    System.out.println(c.area());
} else if (obj instanceof Rectangle r) {
    System.out.println(r.area());
} else if (obj instanceof Triangle t) {
    System.out.println(t.area());
}
```

No manual casting anywhere. Clean and safe.

---

## Pattern Matching with Switch (the real power)

This is where it gets really powerful. Java 17 brought **pattern matching into switch expressions**:

java

```java
Object obj = getShape();

String result = switch (obj) {
    case Circle c    -> "Circle with area: " + c.area();
    case Rectangle r -> "Rectangle with area: " + r.area();
    case Triangle t  -> "Triangle with area: " + t.area();
    default          -> "Unknown shape";
};
```

Compare this to the old if-else chain — **same logic, half the code, zero manual casting**.

---

## Now combine all three — Sealed + Records + Pattern Matching:

This is the **modern Java combination** that interviewers love:

java

```java
// Sealed interface
public sealed interface Shape
    permits Circle, Rectangle, Triangle {}

// Records
public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}

// Pattern matching switch — no default needed (sealed!)
double getArea(Shape shape) {
    return switch (shape) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t  -> 0.5 * t.base() * t.height();
    };
}
```

What's happening here:

* **Sealed** — compiler knows exactly 3 shapes exist
* **Records** — no boilerplate, clean data holders
* **Pattern matching** — no casting, direct access to fields
* **No default** — compiler verifies all cases are covered

This is **bulletproof code** — bugs that used to reach production now get caught at compile time.

---

## Guarded Patterns — adding conditions:

You can add extra conditions inside the case itself using `when`:

java

```java
double getArea(Shape shape) {
    return switch (shape) {
        case Circle c when c.radius() > 100 -> {
            System.out.println("Large circle!");
            yield Math.PI * c.radius() * c.radius();
        }
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t  -> 0.5 * t.base() * t.height();
    };
}
```

`when` is called a **guard** — it adds an extra condition on top of the type check.

---

## Null handling — also improved:

Old way — you had to check null separately or get NPE:

java

```java
// Old — crash if obj is null
if (obj instanceof String s) { ... }
```

New way — handle null directly in switch:

java

```java
switch (obj) {
    case null        -> System.out.println("null value!");
    case String s    -> System.out.println("String: " + s);
    case Integer i   -> System.out.println("Integer: " + i);
    default          -> System.out.println("Other");
}
```

No separate null check needed.

---

## Key characteristics to remember:


| Point               | Detail                                             |
| ------------------- | -------------------------------------------------- |
| Pattern variable    | Auto cast + bind in one step                       |
| Scope               | Pattern variable only available inside that branch |
| `when`guard         | Add extra condition on top of type check           |
| With sealed classes | No`default`needed in switch                        |
| Null handling       | Can handle`null`directly in switch case            |

---

## Real world use in your project:

Your `ApiResponse` example from sealed classes — now with pattern matching:

java

```java
public sealed interface ApiResponse
    permits SuccessResponse, ErrorResponse {}

public record SuccessResponse(Object data) implements ApiResponse {}
public record ErrorResponse(String message, int code) implements ApiResponse {}

// Usage
void handleResponse(ApiResponse response) {
    switch (response) {
        case SuccessResponse s -> System.out.println("Data: " + s.data());
        case ErrorResponse e   -> System.out.println("Error " + e.code() + ": " + e.message());
    }
}
```

Clean, safe, no casting, compiler verified. This is production-grade modern Java.

---

## Interview angle:

> *"Pattern matching removes the gap between type checking and type usage. Before, instanceof was just a boolean check — you still had to cast manually. Now the check and cast happen atomically, and the compiler tracks it. Combined with sealed classes and records, it forms a complete system for safe, exhaustive type handling."*

---

## Final Summary — All Three Together:


| Feature          | Problem Solved                 | Key Benefit                      |
| ---------------- | ------------------------------ | -------------------------------- |
| Records          | Boilerplate in data classes    | One line replaces 40+ lines      |
| Sealed Classes   | Uncontrolled class hierarchies | Compiler enforces exhaustiveness |
| Pattern Matching | Redundant check + cast         | One step type check and use      |

These three features were designed to **complement each other** — that's why they were all finalized together in Java 17. In interviews, mentioning how they work **together** sets you apart from candidates who only know them individually.
