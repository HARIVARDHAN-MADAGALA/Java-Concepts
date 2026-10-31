# CompletableFuture - Correct Usage (Interview Notes)

`CompletableFuture` is **not meant for wrapping every method call**. Its
purpose is to run **independent, time-consuming tasks asynchronously**
and combine their results.

## 1. runAsync() (No return value)

Use when the task doesn't return anything.

``` java
CompletableFuture<Void> future =
    CompletableFuture.runAsync(() -> {
        System.out.println("Sending email...");
    });

future.join();
```

------------------------------------------------------------------------

## 2. supplyAsync() (Returns a value) ✅ Most common

``` java
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> {
        return "Hari";
    });

System.out.println(future.get());
```

------------------------------------------------------------------------

## 3. Calling Slow APIs in Parallel

### Without CompletableFuture

``` java
String employee = employeeService.getEmployee();
String salary = salaryService.getSalary();
```

This waits for the first call to finish before starting the second.

### With CompletableFuture

``` java
CompletableFuture<String> emp =
    CompletableFuture.supplyAsync(() -> employeeService.getEmployee());

CompletableFuture<String> sal =
    CompletableFuture.supplyAsync(() -> salaryService.getSalary());

System.out.println(emp.get());
System.out.println(sal.get());
```

Both API calls execute in parallel, reducing the total waiting time.

------------------------------------------------------------------------

## 4. Combining Two Futures

``` java
CompletableFuture<String> first =
    CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<String> second =
    CompletableFuture.supplyAsync(() -> " Hari");

CompletableFuture<String> result =
    first.thenCombine(second, (a, b) -> a + b);

System.out.println(result.get());
```

**Output**

``` text
Hello Hari
```

------------------------------------------------------------------------

## 5. Transforming the Result

``` java
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> "hari")
                     .thenApply(String::toUpperCase);

System.out.println(future.get());
```

**Output**

``` text
HARI
```

------------------------------------------------------------------------

## 6. Running Another Task After Completion

``` java
CompletableFuture.supplyAsync(() -> "Java")
                 .thenAccept(System.out::println);
```

------------------------------------------------------------------------

# Real Spring Boot Example

Suppose your API needs:

-   Employee details (2 seconds)
-   Salary details (2 seconds)
-   Address details (2 seconds)

### Without CompletableFuture

``` text
Employee -> 2 sec
Salary   -> 2 sec
Address  -> 2 sec

Total = 6 sec
```

### With CompletableFuture

``` java
CompletableFuture<Employee> emp =
    CompletableFuture.supplyAsync(() -> employeeService.getEmployee());

CompletableFuture<Salary> sal =
    CompletableFuture.supplyAsync(() -> salaryService.getSalary());

CompletableFuture<Address> addr =
    CompletableFuture.supplyAsync(() -> addressService.getAddress());

Employee employee = emp.get();
Salary salary = sal.get();
Address address = addr.get();
```

``` text
Employee ----2 sec----
Salary   ----2 sec----
Address  ----2 sec----

Total ≈ 2 sec
```

------------------------------------------------------------------------

# Common CompletableFuture Methods

-   `runAsync()`
-   `supplyAsync()`
-   `thenApply()`
-   `thenAccept()`
-   `thenCompose()`
-   `thenCombine()`
-   `allOf()`
-   `anyOf()`
-   `exceptionally()`
-   `handle()`
-   `whenComplete()`

------------------------------------------------------------------------

# Interview Answer

**When do you use CompletableFuture?**

> I use `CompletableFuture` when multiple independent tasks are
> time-consuming---such as database queries, REST API calls, or
> microservice calls---and they can execute in parallel. This improves
> responsiveness by reducing the total execution time compared to
> running the tasks sequentially.
