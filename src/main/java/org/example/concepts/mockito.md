# Mockito — Complete Guide (Origin to Modern)

---

## 1. What is Mockito?

Mockito is a **Java mocking framework** used in unit testing.
It allows you to create **fake (mock) objects** of dependencies so you can test your class in isolation — without needing real DB, real HTTP calls, real files etc.

- Created by **Szczepan Faber** in 2007
- Built on top of **EasyMock**
- Now the most widely used mocking framework in Java
- Current stable version: **Mockito 5.x**

---

## 2. Why Mockito?

```java
// without mock — needs real DB running
@Service
public class UserService {
    private UserRepository repo;  // real DB call

    public User getUser(int id) {
        return repo.findById(id); // hits DB
    }
}
```

```java
// with mock — no DB needed
UserRepository repo = mock(UserRepository.class);
when(repo.findById(1)).thenReturn(new User(1, "Hari"));

UserService service = new UserService(repo);
User user = service.getUser(1); // no DB — uses mock
```

---

## 3. Dependencies

```xml
<!-- JUnit 5 + Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>

<!-- mockito-junit-jupiter — integrates Mockito with JUnit 5 -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>

<!-- Spring Boot already includes both via -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 4. Core Concepts

| Concept | Meaning |
|---|---|
| **Mock** | Fake object — all methods return default values unless stubbed |
| **Stub** | Define what a mock method should return |
| **Spy** | Partial mock — real object but some methods overridden |
| **Verify** | Assert that a method was called with specific arguments |
| **Argument Captor** | Capture arguments passed to a mock method |
| **InOrder** | Verify methods were called in a specific order |

---

## 5. Creating Mocks

### 5.1 Programmatic (no annotations)
```java
UserRepository repo = Mockito.mock(UserRepository.class);
```

### 5.2 @Mock annotation
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repo;

    @InjectMocks
    UserService service;  // repo is injected into service automatically
}
```

### 5.3 MockitoAnnotations.openMocks (JUnit 4 style)
```java
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```

---

## 6. @Mock vs @InjectMocks

```java
@Mock
UserRepository repo;       // creates mock of UserRepository

@Mock
EmailService emailService; // creates mock of EmailService

@InjectMocks
UserService service;       // creates real UserService and injects mocks into it
```

- `@Mock` — creates a fake object
- `@InjectMocks` — creates the real object under test and injects all `@Mock` fields into it
- Injection happens via constructor → setter → field (in that priority order)

---

## 7. Stubbing — when().thenReturn()

Define what mock should return when a method is called:

```java
when(repo.findById(1)).thenReturn(new User(1, "Hari"));
when(repo.findAll()).thenReturn(List.of(new User(1, "Hari")));
when(repo.existsById(99)).thenReturn(false);
```

### Multiple return values
```java
// first call returns "Hari", second call returns "Vardhan"
when(repo.getName()).thenReturn("Hari").thenReturn("Vardhan");
```

### Throw exception
```java
when(repo.findById(99)).thenThrow(new RuntimeException("Not found"));
when(repo.findById(99)).thenThrow(ResourceNotFoundException.class);
```

### thenAnswer — dynamic response
```java
when(repo.findById(anyInt())).thenAnswer(invocation -> {
    int id = invocation.getArgument(0);
    return new User(id, "User_" + id);
});
```

### doReturn (alternative to thenReturn)
```java
// used for void methods or spies
doReturn(new User(1, "Hari")).when(repo).findById(1);
doThrow(new RuntimeException()).when(repo).deleteById(1);
doNothing().when(repo).deleteById(1);  // for void methods
```

---

## 8. Stubbing Void Methods

`when().thenReturn()` doesn't work on void methods — use `doNothing`, `doThrow`:

```java
// do nothing (default behavior anyway)
doNothing().when(emailService).sendEmail(anyString());

// throw exception on void method
doThrow(new RuntimeException("SMTP failed")).when(emailService).sendEmail("test@test.com");

// execute custom logic
doAnswer(invocation -> {
    System.out.println("Email sent to: " + invocation.getArgument(0));
    return null;
}).when(emailService).sendEmail(anyString());
```

---

## 9. Argument Matchers

Use when you don't care about exact value:

```java
when(repo.findById(anyInt())).thenReturn(new User());
when(repo.findByName(anyString())).thenReturn(new User());
when(repo.save(any(User.class))).thenReturn(new User());
when(repo.save(any())).thenReturn(new User());
```

### Common matchers

| Matcher | Matches |
|---|---|
| `anyInt()` | any int |
| `anyString()` | any String including null |
| `anyLong()` | any long |
| `any(Class)` | any object of that class |
| `any()` | any object |
| `eq(value)` | exact value |
| `isNull()` | null |
| `isNotNull()` | not null |
| `contains("text")` | string containing text |
| `startsWith("Hi")` | string starting with |
| `endsWith(".com")` | string ending with |
| `matches("regex")` | matches regex |

### ⚠️ Mixing matchers rule
If you use ANY matcher, ALL arguments must use matchers:
```java
// ❌ wrong — mixing exact value with matcher
when(repo.findByNameAndAge("Hari", anyInt())).thenReturn(user);

// ✅ correct — all matchers
when(repo.findByNameAndAge(eq("Hari"), anyInt())).thenReturn(user);
```

---

## 10. Verify — asserting method calls

```java
// verify method was called exactly once (default)
verify(repo).findById(1);

// verify exact number of times
verify(repo, times(3)).findById(anyInt());

// verify never called
verify(repo, never()).deleteById(anyInt());

// verify at least / at most
verify(repo, atLeast(1)).findAll();
verify(repo, atMost(3)).findAll();
verify(repo, atLeastOnce()).findAll();
```

### verifyNoMoreInteractions
```java
verify(repo).findById(1);
verifyNoMoreInteractions(repo); // fails if any other method was called on repo
```

### verifyNoInteractions
```java
verifyNoInteractions(emailService); // fails if any method was called at all
```

---

## 11. Argument Captor

Capture what argument was passed to a mock method:

```java
@Captor
ArgumentCaptor<User> userCaptor;

// or programmatically
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

service.createUser("Hari", 28);

verify(repo).save(captor.capture());
User savedUser = captor.getValue();

assertEquals("Hari", savedUser.getName());
assertEquals(28, savedUser.getAge());
```

### Multiple calls — getAllValues
```java
service.createUser("Hari", 28);
service.createUser("Vardhan", 25);

verify(repo, times(2)).save(captor.capture());
List<User> allUsers = captor.getAllValues();
assertEquals("Hari", allUsers.get(0).getName());
assertEquals("Vardhan", allUsers.get(1).getName());
```

---

## 12. Spy — Partial Mock

Spy wraps a **real object** — real methods are called unless you override:

```java
List<String> realList = new ArrayList<>();
List<String> spyList = Mockito.spy(realList);

spyList.add("Hari");         // real method called
spyList.add("Vardhan");      // real method called

verify(spyList, times(2)).add(anyString());
assertEquals(2, spyList.size()); // real size

// override specific method
doReturn(100).when(spyList).size(); // now size() returns 100
```

### @Spy annotation
```java
@Spy
List<String> list = new ArrayList<>();
```

### ⚠️ Spy stubbing — use doReturn not when/thenReturn
```java
// ❌ dangerous with spy — calls real method first
when(spyList.size()).thenReturn(100);

// ✅ safe with spy
doReturn(100).when(spyList).size();
```

---

## 13. InOrder — Verify Call Order

```java
InOrder inOrder = inOrder(repo, emailService);

inOrder.verify(repo).save(any(User.class));         // must be called first
inOrder.verify(emailService).sendEmail(anyString()); // then this
```

---

## 14. Mockito with JUnit 5

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repo;

    @Mock
    EmailService emailService;

    @InjectMocks
    UserService service;

    @Test
    void shouldReturnUserById() {
        when(repo.findById(1)).thenReturn(Optional.of(new User(1, "Hari")));

        User user = service.getUser(1);

        assertEquals("Hari", user.getName());
        verify(repo).findById(1);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getUser(99));
    }
}
```

---

## 15. Mockito with Spring Boot — @MockBean

Used in Spring Boot integration tests — replaces Spring bean with mock:

```java
@SpringBootTest
class UserServiceIntegrationTest {

    @MockBean
    UserRepository repo;   // replaces real bean in Spring context

    @Autowired
    UserService service;   // real Spring bean with mocked repo

    @Test
    void test() {
        when(repo.findById(1)).thenReturn(Optional.of(new User(1, "Hari")));
        User user = service.getUser(1);
        assertEquals("Hari", user.getName());
    }
}
```

### @Mock vs @MockBean

| | `@Mock` | `@MockBean` |
|---|---|---|
| Framework | Mockito only | Spring Boot Test |
| Spring context | ❌ not loaded | ✅ loaded |
| Speed | ✅ fast | ❌ slow (loads context) |
| Use when | pure unit test | Spring integration test |

---

## 16. Mocking Static Methods (Mockito 3.4+)

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>  <!-- needed for static mocking -->
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

```java
try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
    mocked.when(LocalDate::now).thenReturn(LocalDate.of(2024, 1, 1));

    LocalDate today = LocalDate.now();
    assertEquals(LocalDate.of(2024, 1, 1), today);
}
// after try block — static mock is removed automatically
```

---

## 17. Mocking Constructors (Mockito 3.4+)

```java
try (MockedConstruction<UserService> mocked =
        Mockito.mockConstruction(UserService.class,
            (mock, context) -> {
                when(mock.getUser(1)).thenReturn(new User(1, "Hari"));
            })) {

    UserService service = new UserService(); // returns mock
    assertEquals("Hari", service.getUser(1).getName());
}
```

---

## 18. Resetting Mocks

```java
Mockito.reset(repo); // clears all stubbing and verifications
```

⚠️ Generally a code smell — if you need reset, consider splitting into separate tests.

---

## 19. Default Return Values of Mocks

When a method is not stubbed, mock returns:

| Type | Default |
|---|---|
| `int`, `long`, `double` | `0` |
| `boolean` | `false` |
| `Object` | `null` |
| `Collection` | empty collection |
| `Optional` | `Optional.empty()` |

---

## 20. Strict Stubbing (Mockito 2.x+)

Mockito strict stubbing detects:
- **Unnecessary stubbing** — stubbed but never used
- **Stubbing argument mismatch**

```java
@ExtendWith(MockitoExtension.class) // strict stubbing enabled by default in JUnit 5
class Test {
    @Mock UserRepository repo;

    @Test
    void test() {
        when(repo.findById(1)).thenReturn(new User()); // ❌ UnnecessaryStubbingException if not used
    }
}
```

To use lenient stubbing:
```java
lenient().when(repo.findById(1)).thenReturn(new User()); // no strict check
```

---

## 21. Mockito Annotations Summary

| Annotation | Purpose |
|---|---|
| `@Mock` | Creates a mock object |
| `@Spy` | Creates a spy (partial mock) |
| `@InjectMocks` | Creates real object, injects mocks |
| `@Captor` | Creates ArgumentCaptor |
| `@MockBean` | Spring Boot — replaces bean with mock |
| `@SpyBean` | Spring Boot — replaces bean with spy |
| `@ExtendWith(MockitoExtension.class)` | Enables Mockito annotations in JUnit 5 |

---

## 22. BDDMockito — Behavior Driven Style

Alternative API using `given/when/then` language:

```java
import static org.mockito.BDDMockito.*;

// given
given(repo.findById(1)).willReturn(Optional.of(new User(1, "Hari")));

// when
User user = service.getUser(1);

// then
then(repo).should().findById(1);
assertEquals("Hari", user.getName());
```

| Standard Mockito | BDDMockito |
|---|---|
| `when().thenReturn()` | `given().willReturn()` |
| `when().thenThrow()` | `given().willThrow()` |
| `doNothing().when()` | `willDoNothing().given()` |
| `verify()` | `then().should()` |

---

## 23. Common Mistakes

### 1. Stubbing after actual call
```java
service.getUser(1);                              // called first
when(repo.findById(1)).thenReturn(new User());   // ❌ too late — stub after use
```

### 2. Mocking final classes without mockito-inline
```java
// String is final — needs mockito-inline dependency
mock(String.class); // ❌ without mockito-inline
```

### 3. Not using @ExtendWith
```java
// @Mock won't work without this
@ExtendWith(MockitoExtension.class) // ✅ required
```

### 4. Stubbing wrong argument
```java
when(repo.findById(1)).thenReturn(user);
service.getUser(2); // calls findById(2) — stub doesn't match — returns null
```

---

## 24. Evolution Timeline

| Version | Key Feature Added |
|---|---|
| Mockito 1.x | Core mocking, stubbing, verify |
| Mockito 2.x | Strict stubbing, final class mocking (opt-in) |
| Mockito 3.x | Java 11 support, mockito-inline (static/final) |
| Mockito 4.x | Java 17 support, removed deprecated APIs |
| Mockito 5.x | Java 21 support, mockito-inline merged into core, default strict stubbing |

---

## 25. Full Example — Real World

```java
// Service
@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepo, EmailService emailService) {
        this.orderRepo = orderRepo;
        this.emailService = emailService;
    }

    public Order placeOrder(int userId, String item) {
        Order order = new Order(userId, item);
        Order saved = orderRepo.save(order);
        emailService.sendEmail("Order placed: " + saved.getId());
        return saved;
    }
}
```

```java
// Test
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepo;
    @Mock EmailService emailService;
    @InjectMocks OrderService service;

    @Captor ArgumentCaptor<Order> orderCaptor;
    @Captor ArgumentCaptor<String> emailCaptor;

    @Test
    void shouldPlaceOrderAndSendEmail() {
        Order saved = new Order(1, 101, "Laptop");
        when(orderRepo.save(any(Order.class))).thenReturn(saved);
        doNothing().when(emailService).sendEmail(anyString());

        Order result = service.placeOrder(101, "Laptop");

        // verify order saved with correct data
        verify(orderRepo).save(orderCaptor.capture());
        assertEquals(101, orderCaptor.getValue().getUserId());
        assertEquals("Laptop", orderCaptor.getValue().getItem());

        // verify email sent with correct content
        verify(emailService).sendEmail(emailCaptor.capture());
        assertTrue(emailCaptor.getValue().contains("Order placed"));

        // verify result
        assertEquals(1, result.getId());
    }

    @Test
    void shouldThrowWhenRepoFails() {
        when(orderRepo.save(any())).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> service.placeOrder(101, "Laptop"));
        verifyNoInteractions(emailService); // email should not be sent if save fails
    }
}
```

---

## Interview One-Liner
> Mockito is a Java mocking framework that creates fake objects of dependencies, allowing unit tests to run in isolation without real DB, HTTP, or file system calls. It supports stubbing, verification, spying, argument capturing, and static mocking.
