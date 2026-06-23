# Variable Length Arguments (Varargs) in Java

## What It Is
Allows a method to accept **zero or more arguments** of the same type — no need for overloaded methods.

## Syntax
```java
returnType methodName(dataType... variableName)
```
Inside the method, `variableName` behaves like an **array**.

## Example
```java
static void show(int... numbers) {
    System.out.println("Count: " + numbers.length);
    for (int n : numbers) System.out.print(n + " ");
}

show(10);           // Count: 1
show(1, 2, 3, 4);  // Count: 4
show();             // Count: 0
```

## Internal Working
At compile time, varargs is converted to an array:
```java
show(1, 2, 3);           // you write
show(new int[]{1, 2, 3}); // compiler generates
```

## Rules
- Only **one** varargs parameter per method
- Must be the **last** parameter

```java
void test(int... nums, String name) { } // ❌ varargs must be last
void test(String name, int... nums) { } // ✅ correct
```

## With Normal Parameters
```java
static void greet(String message, String... names) {
    for (String n : names) System.out.println(message + " " + n);
}

greet("Hello", "Hari", "Vardhan"); // Hello Hari, Hello Vardhan
```

## Varargs vs Array

| Feature       | Varargs          | Array                  |
|---------------|------------------|------------------------|
| Declaration   | `int... nums`    | `int[] nums`           |
| Caller syntax | `show(1, 2, 3)`  | `show(new int[]{1,2})` |
| Flexibility   | ✅ High          | ❌ Fixed size          |

Both are same internally — varargs is just **syntactic sugar**.

## Overloading with Varargs
```java
void show(int a, int b) { }  // specific match — preferred
void show(int... a) { }      // varargs — fallback
```
`show(10, 20)` calls the **non-varargs** version (more specific match).

## Method Resolution Priority
When no exact match is found, Java follows this order:
1. Exact match
2. Type promotion (widening) — `byte → short → int → long → float → double`
3. Autoboxing — `int → Integer`
4. Varargs — `int...`

```java
void show(long a)   { } // widening
void show(Integer a){ } // autoboxing
void show(int... a) { } // varargs

show(10); // picks widening (long) — highest priority after exact match
```

## Interview Answer
> Varargs allow a method to accept any number of arguments of the same type, declared using `...`, treated as an array internally, and must be the last parameter.
