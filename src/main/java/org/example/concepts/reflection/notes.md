# Reflection API in Java

---

## The Problem — Before Reflection

Normal Java is **compile-time bound** — you must know the class name, method name,
and field names at the time you write the code.

```java
Student s = new Student();   // class name known at compile time
s.getName();                 // method name known at compile time
```

But what if:
- You don't know the class name until **runtime** (loaded from config, plugin, file)?
- You want to **inspect** any object's fields/methods without knowing its type?
- You want to **call a private method** for testing purposes?
- You are building a **framework** (Spring, Hibernate, JUnit) that works with ANY class?

Normal Java can't do any of this — that's where **Reflection** comes in.

---

## What is Reflection?

Reflection is the ability of a program to **inspect and manipulate itself at runtime**.

With Reflection you can at runtime:
- Get class name, superclass, interfaces
- List all fields (including private)
- List all methods (including private)
- List all constructors
- Read / write field values
- Invoke methods dynamically
- Read annotations on classes, fields, methods

---

## How Reflection evolved (3 stages)

### Stage 1 — Inspecting a class (read only)
Explore fields, methods, constructors of any class at runtime.
→ see `Stage1_Inspect.java`

### Stage 2 — Accessing private fields and methods
Break encapsulation intentionally (used in testing and frameworks).
→ see `Stage2_PrivateAccess.java`

### Stage 3 — Reading annotations at runtime
This is how Spring (@Autowired, @Service), JUnit (@Test) actually work.
→ see `Stage3_Annotations.java`

---

## Where Reflection is used in real world

| Framework | How it uses Reflection |
|---|---|
| Spring | Reads @Autowired, @Component, injects dependencies |
| Hibernate / JPA | Reads @Entity, @Column to map fields to DB columns |
| JUnit | Finds methods annotated with @Test and invokes them |
| Jackson | Reads fields of any class to serialize/deserialize JSON |
| Lombok | Generates getters/setters by inspecting fields at compile time |

---

## Reflection — pros and cons

| | |
|---|---|
| ✅ Enables frameworks | Spring, Hibernate wouldn't exist without it |
| ✅ Dynamic behaviour | Load and use classes unknown at compile time |
| ❌ Breaks encapsulation | Can access private fields — use carefully |
| ❌ Slower than direct calls | JVM can't optimize reflective calls as well |
| ❌ No compile-time safety | Wrong method name → RuntimeException, not compile error |

---

## Key classes in java.lang.reflect

| Class | What it represents |
|---|---|
| `Class<?>` | Entry point — represents a loaded class |
| `Field` | A field (variable) of a class |
| `Method` | A method of a class |
| `Constructor` | A constructor of a class |
| `Annotation` | An annotation applied to a class/field/method |
