# Sealed Classes (Java 17, finalized)

## The Problem it solved

Before sealed classes, when you created a class hierarchy (parent-child using `extends`), **anyone** could extend your class — even from outside your package or library.

java

```java
public abstract class Shape {
    abstract double area();
}
```

Now anyone can do this:

java

```java
// someone in another package
public class RandomShape extends Shape {
    double area() { return 999; }
}
```

You had **no control** over who extends your class.

This caused two real problems:

**Problem 1 — Design intent not enforced** You designed `Shape` to have only `Circle`, `Rectangle`, `Triangle` — but you couldn't restrict it. Anyone could add arbitrary subclasses.

**Problem 2 — Compiler couldn't help you** When you write a method that handles all shapes:

java

```java
double getArea(Shape shape) {
    if (shape instanceof Circle) { ... }
    else if (shape instanceof Rectangle) { ... }
    // compiler doesn't warn if you missed Triangle
    // because it doesn't KNOW there are only 3 shapes
}
```

The compiler had **no idea** how many subclasses exist — so it couldn't warn you if you missed handling one. You'd find out only at **runtime** (bug!).

---

## The Solution — Sealed Classes

Sealed classes let you **explicitly declare** which classes are permitted to extend it.

java

```java
public abstract sealed class Shape
    permits Circle, Rectangle, Triangle {
  
    abstract double area();
}
```

Now **only**`Circle`, `Rectangle`, and `Triangle` can extend `Shape`. Anyone else trying to extend it gets a **compile-time error**.

---

## The permitted subclasses must do one of three things:

Every class in the `permits` list must declare what it is:

**Option 1 — `final`** (no further extension allowed)

java

```java
public final class Circle extends Shape {
    private final double radius;
  
    public Circle(double radius) {
        this.radius = radius;
    }
  
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

**Option 2 — `sealed`** (can extend but again restricted)

java

```java
public sealed class Rectangle extends Shape
    permits Square {
    // Square is the only one that can extend Rectangle
}
```

**Option 3 — `non-sealed`** (open for anyone to extend)

java

```java
public non-sealed class Triangle extends Shape {
    // anyone can extend Triangle freely
}
```

---

## The real power — with Switch Expression (Pattern Matching)

This is where sealed classes truly shine. Because the compiler **knows all possible subclasses**, it can check exhaustiveness:

java

```java
double getArea(Shape shape) {
    return switch (shape) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t  -> 0.5 * t.base() * t.height();
        // NO default needed! Compiler knows these are ALL cases
    };
}
```

If you **forget**`Triangle`:

```
Compiler error: switch is not exhaustive — Triangle not covered
```

You catch the bug at **compile time**, not runtime. That's the real win.

---

## Key characteristics to remember:


| Point                                            | Detail                                    |
| ------------------------------------------------ | ----------------------------------------- |
| `permits`keyword                                 | Lists all allowed subclasses              |
| Subclass must be`final`,`sealed`, or`non-sealed` | Mandatory — compiler enforces it         |
| Same package or module                           | Permitted classes must be in same package |
| Works best with Switch                           | Compiler checks all cases are handled     |
| Can be interface too                             | `sealed interface`is also valid           |

---

## Sealed Interface — also valid:

java

```java
public sealed interface Payment
    permits CreditCard, UPI, NetBanking {
}

public record CreditCard(String cardNumber) implements Payment {}
public record UPI(String upiId) implements Payment {}
public record NetBanking(String accountNo) implements Payment {}
```

Notice — `record` + `sealed interface` together is a very clean combination in modern Java.

---

## Real world use case (relevant to your project):

Think of your API responses:

java

```java
public sealed interface ApiResponse
    permits SuccessResponse, ErrorResponse {
}

public record SuccessResponse(Object data) implements ApiResponse {}
public record ErrorResponse(String message, int code) implements ApiResponse {}
```

Now when handling responses, the compiler forces you to handle **both cases** — no chance of missing the error case.

---

## Interview angle:

> *"Sealed classes bring algebraic data types to Java — they let you model a fixed, known set of possibilities and let the compiler verify you've handled all of them. Combined with pattern matching switch, they eliminate an entire class of runtime bugs."*
>
