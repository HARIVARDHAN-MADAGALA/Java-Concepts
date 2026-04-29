# ⚙️ Cyclic Dependency in Spring (and How to Prevent It)

## 🧩 1️⃣ What is a Cyclic Dependency?

A **cyclic dependency** (or **circular dependency**) happens when two or more Spring beans **depend on each other** — directly or indirectly.

So Spring doesn’t know **which bean to create first**.

### 💡 Example (Direct Cycle)

```java
@Component
public class A {
    @Autowired
    private B b;
}

@Component
public class B {
    @Autowired
    private A a;
}
```

🧠 Here:
- Bean **A** depends on **B**
- Bean **B** depends on **A**

➡️ Spring tries to create `A` → needs `B` → tries to create `B` → needs `A` → deadlock!

Result: ❌
```
BeanCurrentlyInCreationException:
Error creating bean with name 'a': Requested bean is currently in creation
```

### 💡 Example (Indirect Cycle)

```java
A → B → C → A
```

Even if it’s indirect, Spring still can’t resolve the chain.

## 🧱 2️⃣ Why This Happens

In Spring’s **dependency injection**, when it creates beans at startup, it tries to **inject dependencies before the beans are fully initialized.**

So if bean A and B depend on each other, neither can be fully created first.

## 🧠 3️⃣ When It’s Common

| Occurs in | Example |
|------------|----------|
| **Constructor Injection** | A depends on B, and B depends on A in constructors |
| **Field Injection** | Mutual `@Autowired` fields |
| **Service Layers** | When service classes call each other (Service A → B → A) |

## ⚙️ 4️⃣ How to **Prevent or Fix** It

### ✅ **1️⃣ Prefer Constructor Injection for clarity**

Constructor injection exposes circular dependencies at **startup** — so you’ll catch the issue early.

```java
@Component
public class A {
    private final B b;
    public A(B b) { this.b = b; }
}
```

💡 **Field injection hides it**, constructor injection exposes it.

### ✅ **2️⃣ Break the dependency chain**

If A and B depend on each other, check if one of them **can call the other through an interface or event** instead of a direct injection.

Example fix:
```java
@Component
public class A {
    @Autowired
    private B b;
}

@Component
public class B {
    // remove A dependency — use an event or callback instead
}
```

**Principle:** High-level modules should not depend on low-level ones directly (Dependency Inversion).

### ✅ **3️⃣ Use `@Lazy` on one side**

If both must reference each other, you can delay one bean’s initialization until it’s needed.

```java
@Component
public class A {
    @Autowired
    private B b;
}

@Component
public class B {
    @Autowired
    @Lazy
    private A a;
}
```

🧠 This tells Spring:
> “Don’t create A immediately — inject it only when needed.”

So Spring creates B first and injects A later on demand.

### ✅ **4️⃣ Use Setter Injection Instead of Constructor**

If both must depend on each other, constructor injection fails because both need to be created upfront.

So, use setter-based injection for one of them.

```java
@Component
public class A {
    private B b;

    @Autowired
    public void setB(B b) {
        this.b = b;
    }
}
```

✅ Now Spring can:
- Create `A` first (without `B`)
- Then create `B`
- Then call `A.setB(b)` later

### ✅ **5️⃣ Redesign Your Architecture**

Circular dependencies often mean **tight coupling** or **bad layering**.

Try to:
- Introduce a third class or interface that both depend on
- Use event listeners or message queues for cross-service communication
- Move shared logic into a utility or service layer

💡 Example:
Instead of `UserService` ↔ `OrderService`, create `UserOrderManager` to mediate between them.

### ✅ **6️⃣ Check for `@Configuration` Bean Cycles**

Sometimes the issue is in config classes:

```java
@Configuration
public class AppConfig {
    @Bean
    public A a() { return new A(b()); }

    @Bean
    public B b() { return new B(a()); } // ❌ cycle
}
```

🧠 Fix:
Inject through method parameters:
```java
@Bean
public A a(B b) { return new A(b); }

@Bean
public B b() { return new B(); }
```

## 🧾 5️⃣ Summary Table

| Problem | Cause | Fix |
|----------|--------|-----|
| Field-level circular dependency | A → B and B → A | Use `@Lazy` or setter injection |
| Constructor-level circular dependency | A → B → A | Redesign or use setter |
| Config class cycle | Beans referencing each other | Pass as method arguments |
| Service circular call | Tight coupling | Redesign or use mediator |

## ⚡ 6️⃣ Real Interview One-Liner

> A **cyclic dependency** occurs when two or more Spring beans depend on each other, causing Spring to get stuck in an infinite creation loop.  
> You can **prevent it** using `@Lazy`, **setter injection**, or by **refactoring** your design to remove the circular reference.
