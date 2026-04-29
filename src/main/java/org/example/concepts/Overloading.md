# ⚙️ Method Overloading (Java)

## 📌 Definition

**Method Overloading** means having **multiple methods with the same name** but **different parameter lists** in the **same class or its subclasses**.

👉 The **compiler** decides which method to call.
👉 Hence, it is **Compile-Time Polymorphism (Static Binding)**.

---

## 🧩 Rules for Method Overloading

### 1️⃣ Same Method Name

All overloaded methods **must have the same name**.

```java
add(int a, int b)
add(double a, double b)
```

---

### 2️⃣ Different Parameter List (**Mandatory**)

Methods must differ by:

* Number of parameters
* Type of parameters
* Order of parameters

```java
sum(int a, int b)
sum(int a, int b, int c)
```

---

### 3️⃣ Return Type Does NOT Matter

You **cannot overload** a method **only by changing return type**.

❌ Invalid:

```java
int add(int a)
double add(int a) // compile-time error
```

---

### 4️⃣ Access Modifier Does NOT Matter

Access modifiers do not affect overloading.

```java
public void show(int a)
private void show(double a)
```

✅ Valid

---

### 5️⃣ Exceptions Do NOT Matter

Overloaded methods may throw:

* Checked exceptions
* Unchecked exceptions
* No exceptions

All are valid.

---

### 6️⃣ Can Exist in Same Class or Subclass

A subclass can overload methods of its superclass.

---

### 7️⃣ Method Signature Matters

**Method Signature = Method Name + Parameter List**

❌ Not part of signature:

* Return type
* Access modifier
* Exceptions

---

### 8️⃣ Automatic Type Promotion Applies

If an exact match is not found, Java promotes types:

```
byte → short → int → long → float → double
char → int → long → float → double
```

---

### 9️⃣ Varargs Are Considered Last

If no exact match exists, the **varargs method** is chosen.

```java
show(int a, int b)
show(int... a)
```

---

## 🧠 Example

```java
class OverloadExample {

    void show(int a) {
        System.out.println("int method");
    }

    void show(double a) {
        System.out.println("double method");
    }

    void show(int a, int b) {
        System.out.println("two int params");
    }

    public static void main(String[] args) {
        OverloadExample obj = new OverloadExample();

        obj.show(10);       // int → exact match → "int method"
        obj.show(10.5);     // double → exact match → "double method"
        obj.show(10, 20);   // → "two int params"
        obj.show('a');      // char → promoted to int → "int method"
    }
}
```

---

## ⚠️ Invalid Overloading Example

```java
class InvalidExample {

    int sum(int a) {
        return a;
    }

    // ❌ Compile-time error (only return type differs)
    double sum(int a) {
        return a;
    }
}
```

---

## ⚖️ Method Overloading vs Method Overriding

| Feature          | Overloading            | Overriding                              |
| ---------------- | ---------------------- | --------------------------------------- |
| Binding          | Compile-time           | Runtime                                 |
| Polymorphism     | Static                 | Dynamic                                 |
| Class relation   | Same class or subclass | Subclass only                           |
| Method signature | Must be different      | Must be same                            |
| Return type      | Can differ             | Same or covariant                       |
| Access modifier  | No restriction         | Cannot reduce visibility                |
| Exceptions       | Any                    | Cannot throw broader checked exceptions |

---

## 💡 Quick Summary

### ✅ Valid Overloading

✔ Same method name
✔ Different parameter list
✔ Any return type
✔ Any access modifier
✔ Any exceptions

### ❌ Invalid Overloading

✖ Only return type differs
✖ Same parameter list exactly

---

## 🎯 Interview One-Liner

> **Method overloading is resolved at compile time based on method signature — method name and parameter list.**
