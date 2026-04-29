# 1. Records (Java 16, finalized)

## The Problem it solved

Before records, whenever you needed a simple **data carrier class** (a class that just holds data), you had to write a LOT of boilerplate:

java

```java
public class Student {
    private final String name;
    private final int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public boolean equals(Object o) { ... }

    @Override
    public int hashCode() { ... }

    @Override
    public String toString() { ... }
}
```

Just to hold `name` and `age` — you wrote **40+ lines**. And if you add a new field, you update constructor, getter, equals, hashCode, toString — **every single time**.

This is called **boilerplate code** — repetitive, error-prone, and adds no real logic.

---

## The Solution — Record

java

```java
public record Student(String name, int age) {}
```

That's it. **One line.**

Java automatically gives you:

* Private final fields
* Constructor
* Getters (but called `name()` not `getName()`)
* `equals()`
* `hashCode()`
* `toString()`

---

## How to use it:

java

```java
Student s = new Student("Harish", 25);

System.out.println(s.name());       // Harish
System.out.println(s.age());        // 25
System.out.println(s);              // Student[name=Harish, age=25]
```

---

## Key characteristics to remember:


| Point                    | Detail                                              |
| ------------------------ | --------------------------------------------------- |
| Immutable                | Fields are`final`— you can't change after creation |
| No setters               | By design — records are read-only data holders     |
| Can have methods         | You can add custom methods inside                   |
| Can implement interfaces | But**cannot extend**other classes                   |
| Cannot be extended       | Implicitly`final`                                   |

---

## Custom method inside record — allowed:

java

```java
public record Student(String name, int age) {
  
    public String upperCaseName() {
        return name.toUpperCase();
    }
}
```

---

## Compact Constructor — for validation:

java

```java
public record Student(String name, int age) {
  
    Student {  // no parentheses needed
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
}
```

This is called a **compact constructor** — you can add validation without rewriting the full constructor.

---

## Where you'll use this in real projects:

* **DTOs** (Data Transfer Objects) — exactly what you're using `AddressDto` for in your project!
* API request/response bodies
* Query result holders

Your `AddressDto` is a perfect candidate to become a record:

java

```java
// Before
public class AddressDto {
    private String street;
    private String city;
    // constructor, getters, equals, hashCode, toString...
}

// After
public record AddressDto(String street, String city) {}
```

---

## Interview angle:

> *"Records are not just syntactic sugar — they communicate **intent**. When you see a record, you immediately know this class is a pure data holder with no mutable state."*

That's a strong answer in interviews.
