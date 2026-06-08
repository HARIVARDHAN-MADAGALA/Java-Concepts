# Serialization & Deserialization in Java

---

## The Problem — Before Serialization

Java objects live only in **memory (RAM)**. When your program stops:
- The object is gone
- You can't send it over a network
- You can't save it to a file or database

```
JVM Memory:
  Employee { id=1, name="Hari", salary=75000 }
  ↓ program stops
  ❌ object lost forever
```

---

## The Solution — Serialization

**Serialization** = converting a Java object → stream of bytes (so it can be saved/sent)
**Deserialization** = converting bytes back → Java object

```
Object  ──serialize──►  bytes  ──save to file / send over network──►  bytes  ──deserialize──►  Object
```

---

## How it evolved (4 stages)

### Stage 1 — Basic Serialization (Java built-in)
Implement `Serializable`, use `ObjectOutputStream` / `ObjectInputStream`
→ see `Stage1_BasicSerialization.java`

### Stage 2 — transient keyword
Mark sensitive fields (password, salary) to be skipped during serialization
→ see `Stage2_Transient.java`

### Stage 3 — serialVersionUID
Controls version compatibility between serialized data and the class definition
→ see `Stage3_SerialVersionUID.java`

### Stage 4 — Custom Serialization
Override `writeObject` / `readObject` to control exactly what gets serialized
→ see `Stage4_CustomSerialization.java`

---

## Where Serialization is used in real world

| Use case | Example |
|---|---|
| Save object to file | Game save state, user preferences |
| Send object over network | RMI, old Java EE |
| HTTP Session storage | Servlet containers store session objects |
| Caching | Redis, Memcached store serialized objects |
| Message queues | Kafka, RabbitMQ serialize message payloads |
| Deep copy trick | Serialize + deserialize = true deep copy |

---

## Key rules to remember

| Rule | Detail |
|---|---|
| Must implement `Serializable` | Marker interface — no methods to implement |
| `static` fields are NOT serialized | They belong to class, not object |
| `transient` fields are NOT serialized | Skipped intentionally |
| All fields must be serializable | Or mark them `transient` |
| `serialVersionUID` should be declared | Avoids `InvalidClassException` on version mismatch |

---

## serialVersionUID — why it matters

```java
private static final long serialVersionUID = 1L;
```

- JVM uses this to verify sender and receiver have the same class definition
- If you add a field and don't declare serialVersionUID → JVM auto-generates one → old saved bytes become incompatible → `InvalidClassException` at runtime
- Always declare it explicitly to control version compatibility yourself
