# 🧩 `Optional` — Full Family (Java 8 → 17)

`java.util.Optional<T>` helps avoid `NullPointerException` by explicitly modeling the “maybe a value exists” case.

---

## ⚙️ 1️⃣ Creation Methods

| Method | Description | Example |
|--------|--------------|----------|
| `Optional.of(value)` | Creates an Optional with non-null value. Throws NPE if `null`. | `Optional.of("Hari")` |
| `Optional.ofNullable(value)` | Creates Optional that can be empty if value is null. | `Optional.ofNullable(maybeNull)` |
| `Optional.empty()` | Creates an empty Optional (no value). | `Optional.empty()` |

---

## 🔍 2️⃣ Checking Presence

| Method | Returns | Description | Example |
|--------|----------|-------------|----------|
| `isPresent()` | `boolean` | True if value exists. | `if (opt.isPresent())` |
| `isEmpty()` (Java 11+) | `boolean` | True if no value present. | `if (opt.isEmpty())` |

---

## 🧠 3️⃣ Retrieving the Value

| Method | Behavior | Example |
|--------|-----------|----------|
| `get()` | Returns value if present; throws `NoSuchElementException` if empty. | `opt.get()` |
| `orElse(defaultVal)` | Returns value if present, else given default. | `opt.orElse("Unknown")` |
| `orElseGet(Supplier)` | Lazily computes default using lambda if empty. | `opt.orElseGet(() -> "Default")` |
| `orElseThrow()` | Throws `NoSuchElementException` if empty. | `opt.orElseThrow()` |
| `orElseThrow(Supplier)` | Throws custom exception if empty. | `opt.orElseThrow(() -> new RuntimeException("Missing"))` |

**🧩 Rule of Thumb:**
> Use `get()` only when you’re 100% sure value is present.  
> Prefer `orElse`, `orElseGet`, or `orElseThrow` otherwise.

---

## 🔄 4️⃣ Transforming the Value

| Method | Description | Example |
|--------|--------------|----------|
| `map(Function)` | Applies transformation if present, returns new Optional. | `opt.map(String::toUpperCase)` |
| `flatMap(Function)` | Same as map, but function itself returns Optional (used for nested Optionals). | `opt.flatMap(User::getAddress)` |
| `filter(Predicate)` | Returns same Optional if predicate true; else empty. | `opt.filter(name -> name.length() > 3)` |

---

## ⚡ 5️⃣ Performing Side Effects

| Method | Description | Example |
|--------|--------------|----------|
| `ifPresent(Consumer)` | Executes code if value exists. | `opt.ifPresent(System.out::println)` |
| `ifPresentOrElse(Consumer, Runnable)` (Java 9+) | Executes one block if present, another if empty. | `opt.ifPresentOrElse(System.out::println, () -> System.out.println("Empty"))` |

---

## 🧩 6️⃣ Equality & Utility

| Method | Description |
|--------|--------------|
| `equals(Object obj)` | Compares two Optionals. |
| `hashCode()` | Returns hash code based on value or 0 if empty. |
| `toString()` | Returns `"Optional[value]"` or `"Optional.empty"` |

---

## 🧩 7️⃣ Stream Integration (Java 9+)

| Method | Description | Example |
|--------|--------------|----------|
| `stream()` | Converts Optional into 0 or 1 element Stream (useful in pipelines). | `opt.stream().forEach(System.out::println)` |

---

### ✅ Summary

`Optional` is not just a null-safety wrapper — it enforces better design by making *absence* of values explicit.  
Use it to:
- Avoid `null` checks
- Compose functional chains safely
- Write cleaner, more predictable APIs
