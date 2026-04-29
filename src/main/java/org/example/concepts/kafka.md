# ⚙️ Apache Kafka Architecture — Complete Explanation

## 🧩 1️⃣ What is Apache Kafka?

Apache Kafka is a **distributed event streaming platform** used to **publish**, **subscribe**, **store**, and **process** real-time data streams.

Kafka is like a high-performance **message broker**, but designed for **massive scale** — millions of messages per second.

---

## 🧠 2️⃣ Core Idea

Kafka works like a **messaging system**, but it’s **distributed, persistent, fault-tolerant**, and **very fast**.  
Instead of sending messages directly between apps, they communicate **through Kafka topics**.

---

## 🧱 3️⃣ Kafka Main Components

| Component | Description |
|------------|-------------|
| 🧵 **Producer** | Application that **sends (publishes)** messages to Kafka topics. |
| 📦 **Broker** | Kafka **server** that stores messages. A cluster has multiple brokers. |
| 🧠 **Zookeeper / KRaft** | Manages cluster coordination and metadata (KRaft replaces ZooKeeper in newer Kafka versions). |
| 📜 **Topic** | Logical channel where messages are published. |
| ✂️ **Partition** | Each topic is divided into partitions to scale horizontally. |
| 📚 **Offset** | Sequential ID of a message inside a partition. |
| 👥 **Consumer** | Application that **reads (subscribes)** messages from topics. |
| 👨‍👩‍👦 **Consumer Group** | A group of consumers that share topic partitions (for load balancing). |

---

## ⚡ 4️⃣ Kafka Architecture Diagram (Conceptual)

```
                ┌─────────────────────────────┐
                │         PRODUCERS           │
                │   (publish messages)        │
                └────────────┬────────────────┘
                             │
                      [ TOPIC: orders ]
                             │
       ┌─────────────────────┴──────────────────────┐
       │                                            │
 [PARTITION 0]                               [PARTITION 1]
 Offset: 0,1,2...                            Offset: 0,1,2...
 Broker1                                     Broker2
       │                                            │
       └─────────────────────┬──────────────────────┘
                             │
                ┌────────────┴────────────┐
                │        CONSUMERS        │
                │ (read via consumer grp) │
                └─────────────────────────┘
```

---

## 🧩 5️⃣ Detailed Explanation of Each Component

### 🔹 **1. Producer**
- Sends messages to Kafka **topics**.
- Can choose the **partition** (round-robin, key-based, or manual).

Example:
```java
kafkaTemplate.send("orders", orderEvent);
```

---

### 🔹 **2. Topic**
- A **logical category** or feed name where messages are stored.
- Like a “channel” in pub-sub systems.

Example topics: `orders`, `payments`, `inventory`.

---

### 🔹 **3. Partition**
- Each topic is split into partitions for parallelism.
- Messages in a partition are **ordered**, but across partitions — not guaranteed.
- Each message inside a partition has an **offset** (like a line number).

---

### 🔹 **4. Broker**
- Kafka **server** that holds data for topics/partitions.
- A cluster = multiple brokers.
- Each broker can handle thousands of partitions.

---

### 🔹 **5. Consumer**
- Reads data from Kafka topics.
- Keeps track of the **offset** it has consumed.
- Multiple consumers can form a **Consumer Group**.

---

### 🔹 **6. Consumer Group**
- Each message goes to **only one** consumer within the group.
- Used for **scaling** — consumers share partitions.
- If one consumer fails, another takes over its partitions.

---

### 🔹 **7. Offset**
- Acts like a **pointer** — indicates the position of a message in a partition.
- Consumers commit offsets to remember where they left off.

---

### 🔹 **8. Zookeeper / KRaft**
- Zookeeper was used for cluster coordination in old Kafka (till v2.8).
- In **Kafka 3.0+**, Zookeeper is being replaced by **KRaft** (Kafka Raft Metadata mode).

---

## 🧠 6️⃣ Message Flow Summary

| Step | Description |
|------|--------------|
| 1️⃣ | Producer sends message to a **Topic**. |
| 2️⃣ | Kafka **Broker** stores the message in the topic’s **Partition**. |
| 3️⃣ | Message gets an **Offset** (unique ID). |
| 4️⃣ | Consumer **pulls** messages from the partition. |
| 5️⃣ | Consumer **commits offset** after successful processing. |

---

## ⚙️ 7️⃣ Example in Spring Boot

**Producer:**
```java
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void sendMessage(String msg) {
    kafkaTemplate.send("orders", msg);
}
```

**Consumer:**
```java
@KafkaListener(topics = "orders", groupId = "order-group")
public void consume(String message) {
    System.out.println("Received: " + message);
}
```

---

## 💡 8️⃣ Key Features of Kafka Architecture

| Feature | Description |
|----------|--------------|
| **Durability** | Messages are stored on disk (log-based storage). |
| **Scalability** | Add brokers and partitions easily. |
| **High Throughput** | Handles millions of messages/sec. |
| **Fault Tolerance** | Data replicated across brokers. |
| **Real-time** | Low latency, near-real-time streaming. |

---

## 🧾 9️⃣ Summary Diagram

| Component | Role |
|------------|------|
| Producer | Sends messages to a topic |
| Topic | Logical message category |
| Partition | Split of topic for parallelism |
| Broker | Kafka server storing data |
| Consumer | Reads messages |
| Consumer Group | Load balances messages |
| Offset | Position of message |
| ZooKeeper / KRaft | Manages cluster metadata |

---

## ✅ **In One Line (Interview Answer)**

> Apache Kafka is a **distributed, fault-tolerant, and high-throughput event streaming platform** based on **publish-subscribe architecture**, where producers write to **topics (with partitions)** and consumers read from them using **offsets and consumer groups** for scalability.
