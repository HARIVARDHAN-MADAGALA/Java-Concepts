# System Design Handbook

## Part 7.1 -- Message Queues

------------------------------------------------------------------------

# Table of Contents

1.  Why Message Queues?
2.  What is a Message Queue?
3.  Synchronous vs Asynchronous Communication
4.  Producer & Consumer
5.  FIFO Queue
6.  Priority Queue
7.  Push vs Pull Model
8.  Kafka vs RabbitMQ
9.  Real-World Examples
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Message Queues?

Not every task needs an immediate response.

Example:

User places an order.

After the order we need to:

-   Send Email
-   Send SMS
-   Generate Invoice
-   Update Analytics

Doing everything immediately slows the response.

------------------------------------------------------------------------

# 2. What is a Message Queue?

A Message Queue stores tasks temporarily until another service processes
them.

    Client
      │
      ▼
    Order Service
      │
      ▼
    Message Queue
      │
     ├──► Email Service
     ├──► SMS Service
     └──► Analytics

------------------------------------------------------------------------

# 3. Synchronous vs Asynchronous Communication

## Synchronous

    Client
      │
      ▼
    Service A
      │
      ▼
    Service B

    Wait for response

Pros: - Simple

Cons: - Slow if downstream service is slow.

## Asynchronous

    Service A
      │
      ▼
    Queue
      │
      ▼
    Consumer

Service A returns immediately.

------------------------------------------------------------------------

# 4. Producer & Consumer

**Producer** publishes messages.

**Consumer** reads and processes messages.

    Producer
       │
       ▼
     Queue
       │
       ▼
    Consumer

------------------------------------------------------------------------

# 5. FIFO Queue

First In → First Out.

    A
    B
    C

    Output

    A
    B
    C

Useful for order-sensitive processing.

------------------------------------------------------------------------

# 6. Priority Queue

Higher-priority messages are processed first.

    Critical
    High
    Normal
    Low

Used in emergency systems.

------------------------------------------------------------------------

# 7. Push vs Pull Model

### Push

Broker sends messages to consumers.

### Pull

Consumer requests messages when ready.

Kafka commonly uses a pull model.

------------------------------------------------------------------------

# 8. Kafka vs RabbitMQ

  Feature      Kafka                  RabbitMQ
  ------------ ---------------------- -------------
  Model        Log                    Queue
  Best For     Event Streaming        Task Queues
  Ordering     Strong per partition   Queue order
  Throughput   Very High              High
  Replay       Yes                    Limited

------------------------------------------------------------------------

# 9. Real-World Examples

Kafka

-   User activity
-   Clickstream
-   Analytics
-   Event streaming

RabbitMQ

-   Email
-   SMS
-   Invoice generation
-   Background jobs

------------------------------------------------------------------------

# 10. Interview Questions

### Why use a Message Queue?

To decouple services and process tasks asynchronously.

### What is a Producer?

A service that publishes messages.

### What is a Consumer?

A service that processes messages.

### Which is better for event streaming?

Kafka.

### Which is commonly used for background jobs?

RabbitMQ.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Producer
       │
       ▼
    Queue
       │
       ▼
    Consumer

    ----------------

    Sync

    Wait

    ----------------

    Async

    Return Immediately

    ----------------

    Kafka

    Event Streaming

    ----------------

    RabbitMQ

    Task Queue

------------------------------------------------------------------------

Next Chapter: **Part 7.2 -- Pub/Sub, Poison Messages & Duplicate
Messages**
