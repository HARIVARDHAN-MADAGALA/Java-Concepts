# System Design Handbook

## Part 6.4 -- CAP Theorem

------------------------------------------------------------------------

# Table of Contents

1.  Why CAP Theorem?
2.  What is CAP?
3.  Consistency
4.  Availability
5.  Partition Tolerance
6.  Why All Three Can't Be Guaranteed
7.  CP Systems
8.  AP Systems
9.  Real-World Examples
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why CAP Theorem?

Distributed systems run on multiple machines.

Network failures are inevitable.

CAP Theorem explains the trade-offs when such failures occur.

------------------------------------------------------------------------

# 2. What is CAP?

CAP stands for:

-   **C**onsistency
-   **A**vailability
-   **P**artition Tolerance

During a network partition, a distributed system can guarantee **either
Consistency or Availability**, but not both.

------------------------------------------------------------------------

# 3. Consistency

Every client sees the same latest data.

Example

    User A updates balance to 1000.

    ↓

    User B immediately reads 1000.

No stale reads.

------------------------------------------------------------------------

# 4. Availability

Every request receives a response.

The response may not always contain the latest data.

Example

    Server continues serving requests

    ↓

    Old value may be returned.

------------------------------------------------------------------------

# 5. Partition Tolerance

A partition occurs when servers cannot communicate.

    Server A  X  Server B

    (Network Failure)

The system should continue operating despite this failure.

------------------------------------------------------------------------

# 6. Why Can't We Have All Three?

Imagine two replicas.

    Client

    ↓

    Replica A   X   Replica B

If communication fails:

Choose **Consistency**

-   Reject requests until replicas synchronize.

Choose **Availability**

-   Continue serving requests, even if data differs.

You cannot fully guarantee both during the partition.

------------------------------------------------------------------------

# 7. CP Systems

Prioritize:

-   Consistency
-   Partition Tolerance

May reject requests.

Examples

-   HBase
-   ZooKeeper

Best for:

-   Banking
-   Distributed locks

------------------------------------------------------------------------

# 8. AP Systems

Prioritize:

-   Availability
-   Partition Tolerance

Allow temporary inconsistency.

Eventually replicas synchronize.

Examples

-   Cassandra
-   DynamoDB
-   Riak

Best for:

-   Social Media
-   Recommendation Systems
-   Analytics

------------------------------------------------------------------------

# 9. Real-World Examples

Banking

    Wrong Balance

    ❌ Never acceptable

    Choose CP

Instagram Likes

    Like count delayed

    ✔ Acceptable

    Choose AP

------------------------------------------------------------------------

# 10. Interview Questions

### What does CAP stand for?

Consistency, Availability and Partition Tolerance.

### Can a distributed system guarantee all three?

No, not during a network partition.

### Which databases are AP?

-   Cassandra
-   DynamoDB

### Which systems are CP?

-   ZooKeeper
-   HBase

### Why is Partition Tolerance mandatory?

Because network failures cannot be completely avoided in distributed
systems.

------------------------------------------------------------------------

# 11. Cheat Sheet

    CAP

    ↓

    Consistency
    Availability
    Partition Tolerance

    ----------------

    During Partition

    Choose

    CP

    or

    AP

    ----------------

    CP

    ↓

    Correct Data

    ----------------

    AP

    ↓

    Always Respond

    (Eventual Consistency)

------------------------------------------------------------------------

Next Chapter: **Part 7.1 -- Message Queues**
