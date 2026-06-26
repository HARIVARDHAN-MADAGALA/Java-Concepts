# Java Exception Handling - Complete Deep Dive

## 1. What is an Exception?
An exception is an event that interrupts the normal flow of program execution.

## 2. Exception Hierarchy
- Throwable
  - Error
  - Exception
    - RuntimeException

## 3. Checked vs Unchecked Exceptions

### Checked
Must be handled or declared.
Examples:
- IOException
- SQLException

### Unchecked
Runtime exceptions.
Examples:
- NullPointerException
- ArithmeticException
- ArrayIndexOutOfBoundsException

## 4. try Block

```java
try {
    int x = 10 / 0;
}
```

## 5. catch Block

```java
try {
    int x = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println(e.getMessage());
}
```

## 6. finally Block

Runs whether exception occurs or not.

```java
try {
    System.out.println("Work");
} finally {
    System.out.println("Cleanup");
}
```

## 7. try + catch

```java
try {
    int x = 10 / 0;
} catch (ArithmeticException e) {
    e.printStackTrace();
}
```

## 8. try + finally

```java
try {
    System.out.println("Business Logic");
} finally {
    System.out.println("Release Resources");
}
```

## 9. try + catch + finally

```java
try {
    int x = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Handled");
} finally {
    System.out.println("Always Executes");
}
```

## 10. Multiple catch

```java
try {
} catch (IOException e) {
} catch (SQLException e) {
} catch (Exception e) {
}
```

Rule:
Specific exceptions first, generic last.

## 11. Can we use Exception and RuntimeException?

```java
catch(RuntimeException e) {}
catch(Exception e) {}
```

Valid.

```java
catch(Exception e) {}
catch(RuntimeException e) {}
```

Compile Error.

## 12. Nested try

```java
try {
    try {
        int x = 10 / 0;
    } catch (ArithmeticException e) {
    }
} catch (Exception e) {
}
```

## 13. throw

Used to explicitly throw an exception.

```java
throw new IllegalArgumentException("Invalid");
```

Use when:
- Input validation
- Business rules

## 14. throws

Declares possible exceptions.

```java
public void readFile() throws IOException {
}
```

Use when caller should decide handling.

## 15. throw vs throws

| throw | throws |
|---------|---------|
| Creates exception | Declares exception |
| Inside method | Method signature |
| One object | Multiple exceptions |

## 16. Exception Propagation

```java
A() -> B() -> C()
```

If C throws exception, it travels upward until handled.

## 17. try-with-resources

```java
try (BufferedReader br =
        new BufferedReader(new FileReader("a.txt"))) {

}
```

Automatically closes resources.

## 18. AutoCloseable

```java
class MyResource implements AutoCloseable {
    public void close() {
        System.out.println("Closed");
    }
}
```

## 19. Closeable vs AutoCloseable

| Closeable | AutoCloseable |
|------------|---------------|
| IO package | Lang package |
| IOException | Exception |
| Narrow | General |

## 20. Suppressed Exceptions

If close() throws exception after main exception.

```java
Throwable[] arr = ex.getSuppressed();
```

## 21. Custom Exception

```java
class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String msg) {
        super(msg);
    }
}
```

## 22. Spring Boot Examples

### Controller

```java
@GetMapping("/{id}")
public User get(@PathVariable Long id) {
    return service.get(id);
}
```

### Service

```java
public User get(Long id) {
    return repo.findById(id)
        .orElseThrow(() ->
            new RuntimeException("User not found"));
}
```

### Global Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }
}
```

## 23. Best Practices

1. Catch specific exceptions.
2. Use try-with-resources.
3. Don't swallow exceptions.
4. Log exceptions.
5. Create custom exceptions for business rules.
6. Use global exception handling in Spring Boot.
7. Don't use Exception everywhere.

## 24. Common Mistakes

### Bad

```java
catch(Exception e) {
}
```

### Bad

```java
catch(Exception e) {
    System.out.println("Error");
}
```

### Bad

```java
throw new Exception();
```

for business validations.

## 25. Interview Questions

1. Difference between Error and Exception?
2. Checked vs Unchecked?
3. throw vs throws?
4. Can finally be skipped?
5. What is try-with-resources?
6. What are suppressed exceptions?
7. Can we have try without catch?
8. Can we have try without finally?
9. Why RuntimeException exists?
10. How does Spring Boot handle exceptions globally?

## Quick Rule

- Handle if you can recover.
- Declare if caller should handle.
- Use RuntimeException for business validation failures.
- Use checked exceptions for recoverable external failures.
- Use try-with-resources for files, streams, DB resources.
