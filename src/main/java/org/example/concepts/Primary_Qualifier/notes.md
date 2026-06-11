# @Primary and @Qualifier — Spring Bean Disambiguation

---

## Stage 1: The Problem — Multiple Beans of Same Type

Spring knows how to inject a bean by type. But what if you have **two beans of the same type**?

```java
@Component
public class EmailNotification implements Notification {
    public void send() { System.out.println("Sending Email"); }
}

@Component
public class SmsNotification implements Notification {
    public void send() { System.out.println("Sending SMS"); }
}
```

Now if you inject `Notification`:
```java
@Service
public class OrderService {

    @Autowired
    private Notification notification;  // ❌ Spring confused — which one?
}
```

Spring throws:
```
NoUniqueBeanDefinitionException: 
Expected single matching bean but found 2: emailNotification, smsNotification
```

---

## Stage 2: Solution 1 — @Primary

Mark one bean as the **default** choice when multiple candidates exist.

```java
@Component
@Primary  // ← this will be injected by default
public class EmailNotification implements Notification {
    public void send() { System.out.println("Sending Email"); }
}

@Component
public class SmsNotification implements Notification {
    public void send() { System.out.println("Sending SMS"); }
}
```

```java
@Service
public class OrderService {

    @Autowired
    private Notification notification;  // ✅ injects EmailNotification (Primary)
}
```

### Visual:
```
Spring finds → EmailNotification (@Primary), SmsNotification
@Autowired Notification → picks @Primary → EmailNotification ✅
```

### When to use @Primary:
- You have a default/most-used implementation
- Other beans are special cases used rarely

---

## Stage 3: Solution 2 — @Qualifier

Explicitly tell Spring **which exact bean** to inject by name.

```java
@Component
public class EmailNotification implements Notification {
    public void send() { System.out.println("Sending Email"); }
}

@Component
public class SmsNotification implements Notification {
    public void send() { System.out.println("Sending SMS"); }
}
```

```java
@Service
public class OrderService {

    @Autowired
    @Qualifier("emailNotification")  // ← exact bean name (camelCase of class name)
    private Notification notification;  // ✅ injects EmailNotification
}

@Service
public class AlertService {

    @Autowired
    @Qualifier("smsNotification")  // ← injects SmsNotification
    private Notification notification;  // ✅ injects SmsNotification
}
```

### Custom qualifier name:
```java
@Component("myEmail")  // custom bean name
public class EmailNotification implements Notification { }

// inject using custom name
@Autowired
@Qualifier("myEmail")
private Notification notification;
```

---

## Stage 4: @Primary vs @Qualifier Together

If both exist — **@Qualifier wins** over @Primary.

```java
@Component
@Primary
public class EmailNotification implements Notification { }  // default

@Component
public class SmsNotification implements Notification { }
```

```java
@Autowired
private Notification notification;  // gets EmailNotification (@Primary) ✅

@Autowired
@Qualifier("smsNotification")
private Notification notification;  // gets SmsNotification (@Qualifier wins) ✅
```

### Priority order:
```
@Qualifier  →  highest priority — always wins
@Primary    →  used only when no @Qualifier present
type match  →  fallback when only one bean exists
```

---

## Stage 5: @Qualifier in Constructor Injection (Recommended)

```java
@Service
public class OrderService {

    private final Notification notification;

    @Autowired
    public OrderService(@Qualifier("smsNotification") Notification notification) {
        this.notification = notification;
    }
}
```

Constructor injection is recommended over field injection — easier to test and no reflection needed.

---

## Stage 6: Custom @Qualifier Annotation

Instead of using string names (error-prone), create a type-safe custom qualifier.

```java
// define custom qualifier
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface EmailService { }

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface SmsService { }
```

```java
@Component
@EmailService  // ← tag with custom qualifier
public class EmailNotification implements Notification { }

@Component
@SmsService    // ← tag with custom qualifier
public class SmsNotification implements Notification { }
```

```java
@Service
public class OrderService {

    @Autowired
    @EmailService  // ✅ type-safe — no typo risk
    private Notification notification;
}
```

No string names → no typos → compile-time safety.

---

## Stage 7: Real World Example — DataSource

Most common real world use case — multiple DB connections:

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        // main DB
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost/maindb")
            .build();
    }

    @Bean
    @Qualifier("reportingDataSource")
    public DataSource reportingDataSource() {
        // reporting DB (read-only replica)
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost/reportdb")
            .build();
    }
}
```

```java
@Service
public class OrderService {

    @Autowired
    private DataSource dataSource;  // gets primaryDataSource (@Primary) ✅
}

@Service
public class ReportService {

    @Autowired
    @Qualifier("reportingDataSource")
    private DataSource dataSource;  // gets reportingDataSource ✅
}
```

---

## Final Summary

| | `@Primary` | `@Qualifier` |
|---|---|---|
| Purpose | Mark default bean | Pick specific bean by name |
| Where to put | On bean class/method | On injection point |
| Priority | Lower | Higher — always wins |
| Use when | One bean is used most of the time | Need specific bean at specific place |
| Risk | Only one can be @Primary per type | String name — typo risk (use custom qualifier) |
| Best practice | Use for default implementation | Use custom @Qualifier annotation for type safety |

---

## Quick Decision:

```
Only one bean of that type?
    → no annotation needed, Spring injects directly

Multiple beans, one is default?
    → @Primary on the default bean

Multiple beans, need specific one at specific place?
    → @Qualifier at injection point

Large project, many beans?
    → Custom @Qualifier annotation for type safety
```
