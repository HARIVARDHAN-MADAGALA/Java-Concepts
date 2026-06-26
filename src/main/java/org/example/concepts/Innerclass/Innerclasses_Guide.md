# Java Inner Classes — Complete Guide

A comprehensive guide to Java inner classes: types, use cases, when to use them, and practical examples.

## Table of Contents
1. [What are Inner Classes?](#what-are-inner-classes)
2. [Types of Inner Classes](#types-of-inner-classes)
3. [Non-Static Inner Class (Member Inner Class)](#non-static-inner-class-member-inner-class)
4. [Static Inner Class (Nested Static Class)](#static-inner-class-nested-static-class)
5. [Local Inner Class](#local-inner-class)
6. [Anonymous Inner Class](#anonymous-inner-class)
7. [Comparison Table](#comparison-table)
8. [Best Practices](#best-practices)

---

## What are Inner Classes?

Inner classes are classes defined within other classes. They have special access to the outer class's members (including private ones) and can be used to logically group related classes.

```
Outer Class
    │
    ├── Non-Static Inner Class (Member Inner Class)
    ├── Static Inner Class (Nested Static Class)
    ├── Local Inner Class (inside methods/blocks)
    └── Anonymous Inner Class (unnamed, inline)
```

---

## Types of Inner Classes

### 1. Non-Static Inner Class (Member Inner Class)

**What it is**
- A regular class defined inside another class (not `static`).
- Has access to all members of the outer class, including private fields and methods.
- Each instance of the inner class is associated with an instance of the outer class.

**When to use**
- When the inner class needs access to the outer class's instance variables/methods.
- For tightly coupled, highly related functionality.
- Helper classes that logically belong to the outer class.

**Example**

```java
public class Outer {
    private String outerField = "Outer data";

    public class Inner {
        private String innerField = "Inner data";

        public void displayDetails() {
            System.out.println("Inner: " + innerField);
            System.out.println("Outer: " + outerField);  // can access outer class members
        }
    }

    public static void main(String[] args) {
        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();  // must create inner through outer instance
        inner.displayDetails();
    }
}
```

**Output**
```
Inner: Inner data
Outer: Outer data
```

**Pros & Cons**
- ✅ Can access outer class members (including private)
- ✅ Clear logical grouping
- ❌ Each inner instance holds a reference to outer instance (memory overhead)
- ❌ Syntax is verbose (`outer.new Inner()`)

---

### 2. Static Inner Class (Nested Static Class)

**What it is**
- A `static` class defined inside another class.
- Does NOT have direct access to instance members of the outer class (only static members).
- Can be instantiated without an outer class instance.
- Behaves like a regular class, just nested for organization.

**When to use**
- When the inner class doesn't need instance-level access to the outer class.
- For utility or helper classes that logically belong to the outer class.
- To avoid creating unnecessary outer class instances.
- Better memory efficiency since no implicit outer reference is held.

**Example**

```java
public class Database {
    private static String dbConnection = "jdbc:mysql://localhost";

    // Static inner class for connection management
    public static class ConnectionManager {
        public static void openConnection() {
            System.out.println("Connection opened: " + dbConnection);
        }

        public static void closeConnection() {
            System.out.println("Connection closed");
        }
    }

    public static void main(String[] args) {
        // Can create without outer instance
        Database.ConnectionManager.openConnection();
        Database.ConnectionManager.closeConnection();
    }
}
```

**Output**
```
Connection opened: jdbc:mysql://localhost
Connection closed
```

**Pros & Cons**
- ✅ No implicit outer reference (lower memory overhead)
- ✅ Can be instantiated without outer instance
- ✅ Cleaner syntax: `Outer.Inner inner = new Outer.Inner()`
- ❌ Cannot access instance members of outer class
- ✅ Can access only static members of outer class

---

### 3. Local Inner Class

**What it is**
- A class defined inside a method or block (if statement, for loop, etc.).
- Scoped to that method/block only.
- Can access local variables of the method (must be `final` or effectively `final`).
- Has access to outer class members.

**When to use**
- When a helper class is needed for a specific method only.
- When you need a class that uses local variables and outer members temporarily.
- Cleaner than passing many parameters or creating full inner classes for one-off use.

**Example**

```java
public class EventProcessor {
    private String eventType = "USER_ACTION";

    public void processEvent(String eventData) {
        final int maxRetries = 3;  // effectively final

        class EventValidator {
            void validate() {
                System.out.println("Validating: " + eventData);
                System.out.println("Event type: " + eventType);
                System.out.println("Max retries allowed: " + maxRetries);
            }
        }

        EventValidator validator = new EventValidator();
        validator.validate();
    }

    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        processor.processEvent("login_attempt");
    }
}
```

**Output**
```
Validating: login_attempt
Event type: USER_ACTION
Max retries allowed: 3
```

**Pros & Cons**
- ✅ Scoped to method (reduces namespace pollution)
- ✅ Can access local variables and outer members
- ✅ Good for short-lived, temporary helper classes
- ❌ Only accessible within the method
- ❌ Cannot be `public`, `protected` or `static`

---

### 4. Anonymous Inner Class

**What it is**
- An unnamed inner class defined inline, usually as an argument to a method.
- Often used to implement interfaces or extend classes.
- Scoped to a single use.
- Declared and instantiated in one expression.

**When to use**
- For one-off implementations of interfaces or abstract classes.
- Event listeners, callbacks, comparators.
- Functional interfaces (though lambdas are often preferred in modern Java).
- Quick implementations that don't warrant a separate named class.

**Example 1: Implementing an interface**

```java
interface Greeter {
    void greet(String name);
}

public class AnonymousClassExample {
    public static void main(String[] args) {
        // Anonymous inner class implementing Greeter
        Greeter greeter = new Greeter() {
            @Override
            public void greet(String name) {
                System.out.println("Hello, " + name + " from anonymous class!");
            }
        };

        greeter.greet("Alice");
    }
}
```

**Output**
```
Hello, Alice from anonymous class!
```

**Example 2: Event listener (common use case)**

```java
public class ButtonClickHandler {
    interface OnClickListener {
        void onClick(String buttonName);
    }

    public void attachClickListener(OnClickListener listener) {
        listener.onClick("Submit Button");
    }

    public static void main(String[] args) {
        ButtonClickHandler handler = new ButtonClickHandler();

        // Anonymous inner class for click listener
        handler.attachClickListener(new OnClickListener() {
            @Override
            public void onClick(String buttonName) {
                System.out.println("Button '" + buttonName + "' was clicked!");
                System.out.println("Processing user action...");
            }
        });
    }
}
```

**Output**
```
Button 'Submit Button' was clicked!
Processing user action...
```

**Example 3: Comparator (common use case)**

```java
import java.util.*;

public class AnonymousComparator {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9);

        // Anonymous inner class for custom comparator
        Collections.sort(numbers, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;  // descending order
            }
        });

        System.out.println("Sorted (descending): " + numbers);
    }
}
```

**Output**
```
Sorted (descending): [9, 8, 5, 2, 1]
```

**Modern Alternative: Lambda Expression** (Java 8+)

Instead of anonymous inner classes, lambdas are more concise:

```java
// Before (anonymous inner class)
Collections.sort(numbers, new Comparator<Integer>() {
    @Override
    public int compare(Integer a, Integer b) {
        return b - a;
    }
});

// After (lambda expression)
Collections.sort(numbers, (a, b) -> b - a);
```

**Pros & Cons**
- ✅ Inline, concise (for simple cases)
- ✅ Good for event handlers and callbacks
- ✅ Useful before lambdas were available
- ❌ Can quickly become unreadable for complex logic
- ❌ No reusability (used once)
- ✅ Lambda expressions are preferred in modern Java (cleaner syntax)

---

## Comparison Table

| Feature | Non-Static Inner | Static Inner | Local Inner | Anonymous Inner |
|---------|------------------|--------------|-------------|-----------------|
| **Syntax** | `class Inner` | `static class Inner` | Inside method | Inline, no name |
| **Access to outer instance members** | ✅ Yes | ❌ No | ✅ Yes | ✅ Yes |
| **Needs outer instance** | ✅ Yes | ❌ No | ✅ Yes | ✅ Yes |
| **Scope** | Class-level | Class-level | Method/block-level | Single use |
| **Can be public/protected** | ✅ Yes | ✅ Yes | ❌ No | ❌ No |
| **Memory overhead** | ⚠️ Higher (implicit reference) | ✅ Lower | ⚠️ Medium | ⚠️ Medium |
| **Use case** | Helper with state | Utility/helper | One-off in method | Event listener, callback |
| **Example** | UI component inside Form | Config manager | Data validation in method | Button click handler |

---

## Best Practices

1. **Prefer static inner classes** when you don't need outer instance access (lower memory footprint).

2. **Use non-static inner classes** only when you genuinely need access to outer instance members.

3. **Local inner classes** are useful for method-scoped helpers; consider refactoring if the method becomes too large.

4. **Prefer lambdas over anonymous inner classes** (Java 8+) for functional interfaces:
   ```java
   // ❌ Verbose
   button.setOnClickListener(new OnClickListener() {
       @Override
       public void onClick() {
           handleClick();
       }
   });

   // ✅ Cleaner (Java 8+)
   button.setOnClickListener(() -> handleClick());
   ```

5. **Name inner classes meaningfully** to reflect their purpose.

6. **Avoid excessive nesting** (inner classes within inner classes) — it reduces readability.

7. **Use static inner classes for factories or builders**:
   ```java
   public class User {
       private String name;
       private int age;

       public static class Builder {
           private String name;
           private int age;

           public Builder withName(String name) {
               this.name = name;
               return this;
           }

           public User build() {
               User user = new User();
               user.name = this.name;
               user.age = this.age;
               return user;
           }
       }
   }
   ```

---

## Real-World Examples

### Example 1: Collections & Comparators
```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
Collections.sort(names, (a, b) -> a.compareTo(b));  // lambda over anonymous inner
```

### Example 2: Event Handlers (Swing/Android)
```java
button.addActionListener(event -> {
    System.out.println("Button clicked!");
});
```

### Example 3: Thread Creation
```java
Thread thread = new Thread(() -> {
    System.out.println("Running in thread: " + Thread.currentThread().getName());
});
thread.start();
```

### Example 4: Builder Pattern with Static Inner Class
```java
User user = new User.Builder()
    .withName("John")
    .withAge(30)
    .build();
```

---

## Summary

| Type | Memory | Reusability | Complexity | Best For |
|------|--------|-------------|-----------|----------|
| **Non-Static Inner** | Higher | High | Medium | Tightly coupled helpers with state |
| **Static Inner** | Lower | High | Low | Utility classes, builders, factories |
| **Local Inner** | Medium | Low | Low | Method-scoped, temporary helpers |
| **Anonymous Inner** | Medium | None | High | Event handlers, callbacks (use lambdas instead) |

Choose inner classes based on **scope**, **access needs**, and **memory efficiency**. In modern Java 8+, prefer **lambdas** for simple functional implementations and **static inner classes** for reusable helpers.

