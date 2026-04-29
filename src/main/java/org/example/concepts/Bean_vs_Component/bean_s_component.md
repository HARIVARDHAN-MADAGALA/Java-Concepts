
# ☕ @Bean vs @Component in Spring Boot

---

## 🧩 1️⃣ @Component — Class Level Annotation

### 📜 Definition
`@Component` is used **on a class** to tell Spring:
> “Hey Spring, please create an object (bean) of this class and manage it in the application context.”

### ✅ Example
```java
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    public void sendEmail() {
        System.out.println("Email sent successfully!");
    }
}
```

✅ When Spring scans your packages (via `@ComponentScan`),  
it automatically creates a **bean** of `EmailService`.

You can now **inject** it anywhere:
```java
@Autowired
private EmailService emailService;
```

### 🔹 When to use `@Component`
- When **you write your own class** and want Spring to detect it automatically.
- Used with specialized annotations:
    - `@Service` → for service layer
    - `@Repository` → for DAO layer
    - `@Controller` → for web layer

---

## 🧩 2️⃣ @Bean — Method Level Annotation

### 📜 Definition
`@Bean` is used **inside a `@Configuration` class** to explicitly declare a bean method.  
Use it when you **don’t control the class** (e.g., from a library) or need **manual bean creation**.

### ✅ Example
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EmailService emailService() {
        return new EmailService(); // manually create and return the bean
    }
}
```

Spring calls this method, and the returned object is **registered as a bean**.

### 🔹 When to use `@Bean`
- When you want to **manually configure** a bean.
- When the class is **not your own** (e.g., a third-party library).
- When you need **custom initialization logic**.

---

## ⚖️ Key Differences Table

| Feature | `@Component` | `@Bean` |
|----------|---------------|----------|
| Where used | On a class | On a method inside `@Configuration` |
| Bean creation | Automatically (component scan) | Manually (explicit method) |
| Used for | Your own components | Third-party / configurable beans |
| Example | `@Component class MyService {}` | `@Bean public MyService myService() {}` |
| Requires `@ComponentScan`? | ✅ Yes | ❌ No |
| Flexibility | Automatic | Manual (more control) |

---

## 🧠 Shortcut to Remember
> **`@Component` → automatic bean**  
> **`@Bean` → manual bean**

---

### ✅ Real Example
```java
@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // class from Spring library
    }
}
```

Here, `RestTemplate` is not your own class, so you **can’t use @Component** —  
that’s why you use **`@Bean`** instead.

---

## 🧾 Summary

| Annotation | Level | Purpose | Typical Use Case |
|-------------|--------|-----------|----------------|
| `@Component` | Class | Marks a class as Spring-managed bean | Your own service or component |
| `@Bean` | Method | Declares a bean manually inside configuration | Library or third-party class |

---

> 🪶 **In short:**  
> `@Component` → Auto-detected  
> `@Bean` → Explicitly declared
