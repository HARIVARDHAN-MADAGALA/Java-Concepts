# ⚙️ Method Overriding in Java — Complete Notes

## 🧠 Definition
**Method Overriding** happens when a subclass defines a method that has:
- The same **name**
- The same **parameters**
- The same or **covariant return type**  
  as a method in its parent class.

It enables **runtime polymorphism** — the method that gets called is decided **at runtime**, based on the object type.

---

## 🔹 Rule 1: Same Name and Parameters
The overriding method must have **exactly the same signature**.

```java
class Parent {
    void show() {
        System.out.println("Parent show()");
    }
}

class Child extends Parent {
    @Override
    void show() {
        System.out.println("Child show()");
    }
}
```

---

## 🔹 Rule 2: Same or Covariant Return Type
The return type must be either:
- The same as the parent’s method, or
- A **subclass** of the parent’s return type.

```java
class Animal {}
class Dog extends Animal {}

class Parent {
    Animal getAnimal() { return new Animal(); }
}

class Child extends Parent {
    @Override
    Dog getAnimal() { return new Dog(); } // ✅ covariant return type
}
```

---

## 🔹 Rule 3: Access Modifier Cannot Be More Restrictive

| Parent Modifier | Allowed in Child | Not Allowed |
|-----------------|------------------|--------------|
| `public` | `public` | ❌ `protected`, `private`, default |
| `protected` | `protected`, `public` | ❌ `private` |
| default | default, protected, public | ❌ `private` |

```java
class Parent {
    public void show() {}
}
class Child extends Parent {
    protected void show() {} // ❌ compile-time error
}
```

---

## 🔹 Rule 4: Private Methods Cannot Be Overridden

Private methods are **not inherited**, so overriding doesn’t apply.

```java
class Parent {
    private void display() {}
}
class Child extends Parent {
    void display() { // new method, not overridden
        System.out.println("Child display");
    }
}
```

---

## 🔹 Rule 5: Static Methods Cannot Be Overridden
Static methods are **class-level**, not **object-level**.

```java
class Parent {
    static void greet() {
        System.out.println("Parent greet");
    }
}
class Child extends Parent {
    static void greet() { // method hiding
        System.out.println("Child greet");
    }
}
```

---

## 🔹 Rule 6: Final Methods Cannot Be Overridden
Final methods are locked for overriding.

```java
class Parent {
    final void run() {}
}
class Child extends Parent {
    void run() {} // ❌ compile-time error
}
```

---

## 🔹 Rule 7: Constructors Cannot Be Overridden
Constructors are **not inherited**, so overriding doesn’t apply.  
You can only call the parent constructor using `super()`.

---

## 🔹 Rule 8: Checked Exceptions Rules
A child’s overriding method can:
- Throw **fewer** checked exceptions,
- Throw **narrower (subclass)** exceptions,
- Or throw **no checked exceptions** at all.

```java
class Parent {
    void test() throws IOException {}
}
class Child extends Parent {
    @Override
    void test() throws FileNotFoundException {} // ✅ valid
}
```

❌ Invalid example:
```java
class Child extends Parent {
    @Override
    void test() throws Exception {} // ❌ broader exception
}
```

---

## 🔹 Rule 9: Must Be in Inheritance
Overriding works **only between superclass and subclass** relationships.

---

## 🔹 Rule 10: Use of @Override Annotation
The `@Override` annotation is **optional but recommended** to catch mistakes.

```java
@Override
void show() {} // If not actually overriding, compiler error
```

---

## 🔹 Rule 11: Runtime Polymorphism (Dynamic Dispatch)
At runtime, the **object type (not reference type)** decides which method executes.

```java
class Parent {
    void say() { System.out.println("Parent"); }
}
class Child extends Parent {
    void say() { System.out.println("Child"); }
}

public class Test {
    public static void main(String[] args) {
        Parent p = new Child();
        p.say();  // Output: "Child" ✅ (runtime polymorphism)
    }
}
```

---

## 🧾 Summary Table

| Rule | Description | Example |
|------|--------------|----------|
| 1 | Same name, parameters | `void show()` |
| 2 | Same or covariant return type | `Animal → Dog` |
| 3 | Access cannot be reduced | `public → protected` ❌ |
| 4 | Private methods not overridden | Hidden |
| 5 | Static methods not overridden | Method hiding |
| 6 | Final methods not overridden | Compile error |
| 7 | Constructors not overridden | Not applicable |
| 8 | Can throw fewer/narrower checked exceptions | `IOException → FileNotFoundException` |
| 9 | Must be in inheritance | Parent → Child |
| 10 | Use `@Override` | Compiler safety |
| 11 | Decided at runtime | Polymorphism |
