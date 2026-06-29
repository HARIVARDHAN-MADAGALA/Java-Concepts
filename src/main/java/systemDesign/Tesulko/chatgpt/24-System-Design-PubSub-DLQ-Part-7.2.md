# System Design Handbook

## Part 7.2 -- Pub/Sub, Poison Messages & Duplicate Messages

------------------------------------------------------------------------

# Table of Contents

1.  Why Pub/Sub?
2.  Queue vs Pub/Sub
3.  Topics
4.  Fan-Out Messaging
5.  Kafka Consumer Groups
6.  RabbitMQ Exchanges
7.  Poison Messages
8.  Dead Letter Queue (DLQ)
9.  Duplicate Messages & Idempotency
10. Retry Strategies
11. Interview Questions
12. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Pub/Sub?

Sometimes the same event must be consumed by multiple services.

Example:

Order Placed

-   Send Email
-   Update Inventory
-   Notify Analytics
-   Generate Invoice

Instead of calling every service directly, publish one event.

------------------------------------------------------------------------

# 2. Queue vs Pub/Sub

## Queue

One message → One consumer.

    Producer
       │
     Queue
       │
    Consumer

## Publish / Subscribe

One message → Many subscribers.

    Publisher
        │
       Topic
     ┌──┼────┐
     ▼  ▼    ▼
    Email Analytics Inventory

------------------------------------------------------------------------

# 3. Topics

A Topic groups related events.

Examples

-   orders
-   payments
-   users

Publishers write to topics.

Subscribers receive matching events.

------------------------------------------------------------------------

# 4. Fan-Out Messaging

One event is copied to multiple consumers.

Example

    Order Created

    ↓

    Email

    Inventory

    Analytics

    Fraud Detection

------------------------------------------------------------------------

# 5. Kafka Consumer Groups

Consumers in the same group share work.

    Topic

    ↓

    Partition 1 → Consumer A

    Partition 2 → Consumer B

Different consumer groups each receive the event independently.

------------------------------------------------------------------------

# 6. RabbitMQ Exchanges

Exchange decides routing.

Types

-   Direct
-   Fanout
-   Topic
-   Headers

Fanout Exchange broadcasts to all bound queues.

------------------------------------------------------------------------

# 7. Poison Messages

A message that repeatedly fails processing.

Example

Invalid JSON.

Processing keeps failing.

Solution

Do not retry forever.

Move it elsewhere.

------------------------------------------------------------------------

# 8. Dead Letter Queue (DLQ)

Failed messages are moved to a DLQ.

    Queue

    ↓

    Retry

    ↓

    Still Fails

    ↓

    Dead Letter Queue

Later, engineers inspect and reprocess them.

------------------------------------------------------------------------

# 9. Duplicate Messages & Idempotency

Message brokers may deliver the same message more than once.

Consumers should be idempotent.

Example

Order #101 processed twice.

Instead of charging twice, detect the duplicate and ignore it.

------------------------------------------------------------------------

# 10. Retry Strategies

Common approaches

-   Fixed Delay
-   Exponential Backoff
-   Maximum Retry Count
-   DLQ after retries

------------------------------------------------------------------------

# 11. Interview Questions

### Difference between Queue and Pub/Sub?

Queue → One consumer.

Pub/Sub → Many subscribers.

### What is a DLQ?

A queue that stores messages that repeatedly fail.

### Why do duplicate messages occur?

Network failures, consumer crashes, acknowledgement issues.

### Why is idempotency important?

To safely process duplicate deliveries.

------------------------------------------------------------------------

# 12. Cheat Sheet

    Queue

    ↓

    One Consumer

    ----------------

    Pub/Sub

    ↓

    Many Consumers

    ----------------

    Poison Message

    ↓

    Move to DLQ

    ----------------

    Duplicates

    ↓

    Idempotent Consumer

    ----------------

    Retry

    ↓

    Backoff

------------------------------------------------------------------------

Next Chapter: **Part 8.1 -- Fault Tolerance**
