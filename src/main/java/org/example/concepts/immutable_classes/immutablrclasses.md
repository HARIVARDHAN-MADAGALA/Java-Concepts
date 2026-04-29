1`1   # 🔒 Immutable Classes in Java

## 🧩 Definition
An **immutable class** is one whose **state (data) cannot be changed** after it’s created.

Once you create an object of it, you cannot modify its fields — ever.

---

## 💡 Real-world Analogy
Think of an immutable object like a **sealed envelope** — once you’ve written and sealed it,  
you can read what’s inside, but you can’t change the letter.

---

## 📘 Example: `String` is Immutable
```java
String s1 = "Hello";
String s2 = s1.concat(" World");
System.out.println(s1); // "Hello" (unchanged)
System.out.println(s2); // "Hello World"
```
Here, `concat()` didn’t modify `s1`; it created a *new object*.

---

## 🏗️ How to Create an Immutable Class
```java
public final class Student {
    private final String name;
    private final int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

---

## ✅ Rules for Making a Class Immutable
1. **Declare the class `final`**  
   → Prevents subclassing (which can alter behavior).

2. **Make all fields `private` and `final`**  
   → So they can be set only once.

3. **No setters**  
   → Only getters allowed.

4. **Initialize fields in the constructor**  
   → State is fixed at creation.

5. **If class has mutable fields (like `Date`, `List`, etc.),**  
   → Return *copies* instead of direct references.

---

## 🧱 Example with Mutable Field (Correct Way)
```java
import java.util.Date;

public final class Employee {
    private final String name;
    private final Date joinDate; // mutable field

    public Employee(String name, Date joinDate) {
        this.name = name;
        this.joinDate = new Date(joinDate.getTime()); // defensive copy
    }

    public String getName() {
        return name;
    }

    public Date getJoinDate() {
        return new Date(joinDate.getTime()); // return copy
    }
}
```

---

## ❌ Common Mistake — Breaking Immutability
If you don’t create defensive copies, your class *looks* immutable but isn’t.

### Example
```java
import java.util.Date;

public final class Employee {
    private final String name;
    private final Date joinDate; // mutable class!

    public Employee(String name, Date joinDate) {
        this.name = name;
        this.joinDate = joinDate; // ❌ direct reference
    }

    public String getName() {
        return name;
    }

    public Date getJoinDate() {
        return joinDate; // ❌ returning original Date
    }
}
```

### Test Code
```java
public class TestImmutable {
    public static void main(String[] args) {
        Date date = new Date();
        Employee emp = new Employee("Ravi", date);

        System.out.println("Before change: " + emp.getJoinDate());

        // Modify the original date object
        date.setTime(0);

        System.out.println("After change: " + emp.getJoinDate());
    }
}
```

### Output
```
Before change: Sat Nov 09 16:45:00 IST 2025
After change: Thu Jan 01 05:30:00 IST 1970
```

💥 The internal state changed — immutability **broken!**

---

## ✅ Corrected Version
```java
public final class Employee {
    private final String name;
    private final Date joinDate;

    public Employee(String name, Date joinDate) {
        this.name = name;
        this.joinDate = new Date(joinDate.getTime()); // ✅ defensive copy
    }

    public String getName() {
        return name;
    }

    public Date getJoinDate() {
        return new Date(joinDate.getTime()); // ✅ return copy
    }
}
```

Now, even if the original `Date` object changes, the `Employee` object stays safe.

---

## ⚙️ Benefits of Immutability
- Thread-safe (no synchronization needed)
- Easy to cache and reuse
- Safe to share across methods and threads
- Reliable for use in collections (like HashMap keys)

---

## 🧠 Key Takeaways
| Rule | Purpose |
|------|----------|
| `final` class | Prevent subclass modifications |
| `private final` fields | Fixed data after creation |
| No setters | No direct modification |
| Defensive copies | Protect from mutable references |

---

## 💬 Summary
Even if fields are `final`, immutability breaks if:
- You expose **mutable objects** directly, or
- You don’t return **defensive copies**.

So, a truly immutable class keeps its internal data 100% protected after creation.
