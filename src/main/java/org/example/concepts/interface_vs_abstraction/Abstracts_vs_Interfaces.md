# Abstract Class vs Interface vs Concrete (Normal) Class — Quick Reference

This document explains Java abstract classes: what makes a class abstract, allowed members (constructors, fields, concrete/abstract methods), and a clear comparison with interfaces and normal (concrete) classes. It includes code examples and a short decision checklist.

---

## 1. What makes a class "abstract"?

- Use the `abstract` keyword on the class declaration:

```java
public abstract class Shape {
    // ...
}
```

- An abstract class cannot be instantiated directly:

```java
Shape s = new Shape(); // compile error
```

- A class must be declared `abstract` if it declares any abstract method(s):

```java
public abstract class Shape {
    public abstract double area();
}
```

- You can also declare a class `abstract` even without abstract methods — commonly used to prevent direct instantiation and to provide shared behavior.

---

## 2. What an abstract class can contain

- Constructors: Yes. Abstract classes can define constructors which are called by concrete subclasses via `super(...)`.

```java
public abstract class Base {
    protected Base(String config) { }
}
```

- Fields (instance and static): Yes. Can be `public`, `protected`, `private`, `package-private` and `final` or mutable.

```java
protected int id;
private static final String TYPE = "BASE";
```

- Concrete methods (fully implemented): Yes. Abstract classes commonly include shared helper methods.

```java
public void log(String msg) { System.out.println(msg); }
```

- Abstract methods: Yes — declared without a body and must be implemented by concrete subclasses (unless subclass is also abstract).

```java
public abstract void render();
```

- Static methods: Yes — just like normal classes.

- Instance initializer blocks and static initializer blocks: Yes.

---

## 3. Key rules and behaviors

- Instantiation: You cannot instantiate an abstract class. Only concrete (non-abstract) subclasses can be instantiated.
- Inheritance: A class can extend only one (single) class (abstract or not). Abstract classes support single inheritance.
- Subclass obligations: A concrete subclass of an abstract class must implement all inherited abstract methods or itself be declared `abstract`.
- Visibility: Abstract methods can be `public`, `protected`, or package-private (not private). Private abstract methods are not allowed since they cannot be implemented by subclasses.

---

## 4. Example: Abstract class with constructor, fields, concrete & abstract methods

```java
public abstract class Animal {
    private final String name;
    protected int age;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    // concrete method
    public void sleep() {
        System.out.println(name + " is sleeping");
    }

    // abstract method
    public abstract void makeSound();
}

public class Dog extends Animal {
    public Dog(String name) { super(name); }

    @Override
    public void makeSound() { System.out.println("Woof"); }
}
```

---

## 5. Interface basics (brief) — differences introduced by modern Java

- Declaration uses `interface` keyword:

```java
public interface Flyable {
    void fly();
}
```

- Prior to Java 8 interfaces could only declare abstract methods (implicitly `public abstract`) and `public static final` constants.
- Since Java 8+ and 9+, interfaces may include:
  - `default` methods (concrete method with a body)
  - `static` methods
  - `private` methods (since Java 9) for internal reuse within the interface

- Interfaces cannot have instance fields (only constants). They do not hold instance state.
- A class can implement multiple interfaces (`implements A, B`) — multiple inheritance of type only.
- From Java 8 onwards, interfaces can provide method implementations via `default` and `static` methods but still differ from abstract classes in important ways (no instance fields, no constructors).

---

## 6. Concrete (normal) class basics

- A normal (concrete) class has no `abstract` modifier.
- It can be instantiated (assuming it has accessible constructors).
- It may implement interfaces and/or extend a (single) superclass.
- All methods in a concrete class are either fully implemented (concrete) or inherited abstract methods are implemented by it.

---

## 7. Side-by-side comparison

| Feature | Abstract Class | Interface | Concrete Class |
|---|---:|---|---|
| Keyword | `abstract class` | `interface` | (none) |
| Can be instantiated? | No | No | Yes |
| Can have constructors? | Yes | No | Yes |
| Can have instance fields? | Yes | No (only constants) | Yes |
| Can have concrete methods? | Yes | Yes (default/static/private since Java 8/9) | Yes |
| Can have abstract methods? | Yes | Yes (implicitly abstract pre-Java 8; now allowed for unspecified methods) | No (unless class is abstract) |
| Access modifiers on methods | public/protected/package-private | public (implicit) or private for helpers (since Java 9); default methods are public | public/protected/private |
| Multiple inheritance? | No (single class only) | Yes (multiple interfaces) | No (single class only) |
| Use-case | Shared implementation + optional abstract parts; partial base class | Pure API/type contract; multiple type inheritance; mix-in behaviors | Concrete implementation ready to use |

---

## 8. When to choose an abstract class vs interface vs concrete class

- Use an **interface** when:
  - You need to define a contract (API) and allow multiple, unrelated classes to implement it.
  - You want to allow multiple inheritance of type (implement many interfaces).
  - You do not need to store instance state in the type.

- Use an **abstract class** when:
  - You want to provide a partial implementation that is shared by multiple closely related classes.
  - You need to store common state (fields) and provide shared behavior (concrete methods).
  - You want to force subclasses to implement certain methods but also give helper methods and constructors.

- Use a **concrete class** when:
  - You have a complete implementation ready for instantiation.

---

## 9. Common patterns

- **Template method pattern**: often implemented using an abstract class that defines a high-level template method and abstract steps subclasses implement.

```java
public abstract class DataProcessor {
    public final void process() {
        read();
        transform();
        write();
    }
    protected abstract void read();
    protected abstract void transform();
    protected abstract void write();
}
```

- **Adapter / mix-in**: Use interfaces with default methods for optional behaviors; use abstract classes when behavior requires shared fields/state.

---

## 10. Quick checklist for converting class to abstract

- Does the class have methods that should not have a default implementation? Consider making them `abstract`.
- Do you want to prevent direct instantiation? Mark the class `abstract` and provide protected constructors.
- Do you want to share state and helpers across subclasses? Use `abstract class` with protected fields and concrete helper methods.

---

## 11. Example illustrating differences

```java
interface Logger {
    void log(String msg); // implied public abstract
}

abstract class AbstractLogger implements Logger {
    protected String prefix;
    public AbstractLogger(String prefix) { this.prefix = prefix; }
    public void info(String msg) { log(prefix + " " + msg); }
}

class ConsoleLogger extends AbstractLogger {
    public ConsoleLogger(String prefix) { super(prefix); }
    @Override
    public void log(String msg) { System.out.println(msg); }
}

// Usage
ConsoleLogger lg = new ConsoleLogger("[app]");
lg.info("started");
```

---

## 12. References & further reading

- Java Language Specification — Classes and Interfaces
- Oracle Java Tutorials: Interfaces and Abstract Classes
- Effective Java (Item: prefer interfaces for type, abstract classes for code reuse)

---

File created: `src/main/java/org/example/concepts/Interface_vs_Abstraction/Abstracts_vs_Interfaces.md`

