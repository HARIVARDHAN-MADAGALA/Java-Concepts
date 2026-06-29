# System Design Handbook

## Part 6.2 -- Replication Algorithms

------------------------------------------------------------------------

# Table of Contents

1.  Why Replication Algorithms?
2.  Single Leader Replication
3.  Multi Leader Replication
4.  Leaderless Replication
5.  Quorum
6.  Conflict Resolution
7.  Comparison
8.  Real-World Examples
9.  Interview Questions
10. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Replication Algorithms?

Replication copies data to multiple servers.

The important question is:

**Who accepts writes?**

Different systems answer this differently.

------------------------------------------------------------------------

# 2. Single Leader Replication

Only one node accepts writes.

              Writes
                 │
                 ▼
             Leader DB
            /    |    \
           ▼     ▼     ▼
       Replica Replica Replica

    Reads → Leader or Replicas

### Advantages

-   Simple
-   Strong consistency (with sync replication)

### Disadvantages

-   Leader is a bottleneck for writes.

Examples

-   MySQL
-   PostgreSQL

------------------------------------------------------------------------

# 3. Multi Leader Replication

Multiple leaders accept writes.

    Leader A  ⇄  Leader B
        │             │
     Replicas     Replicas

Useful when applications write from multiple regions.

### Advantages

-   Better write availability
-   Regional writes

### Challenges

-   Conflict resolution

Examples

-   Active-Active databases

------------------------------------------------------------------------

# 4. Leaderless Replication

No leader exists.

Clients write directly to multiple replicas.

    Client

     ├──► Replica 1
     ├──► Replica 2
     └──► Replica 3

Reads combine data from replicas.

Examples

-   Cassandra
-   Amazon DynamoDB
-   Riak

------------------------------------------------------------------------

# 5. Quorum

Suppose:

    N = 3 replicas

    Write to W = 2

    Read from R = 2

Rule

    R + W > N

This increases the probability of reading the latest value.

Example

    N = 3
    R = 2
    W = 2

    2 + 2 > 3 ✔

------------------------------------------------------------------------

# 6. Conflict Resolution

With multiple writers, conflicts can occur.

Common techniques:

-   Last Write Wins
-   Version Numbers
-   Vector Clocks
-   Application-specific merge logic

------------------------------------------------------------------------

# 7. Comparison

  Algorithm       Writes             Reads         Complexity
  --------------- ------------------ ------------- ------------
  Single Leader   One Leader         Replicas      Low
  Multi Leader    Multiple Leaders   Replicas      Medium
  Leaderless      Any Replica        Any Replica   High

------------------------------------------------------------------------

# 8. Real-World Examples

MySQL - Single Leader

PostgreSQL - Primary + Replicas

Cassandra - Leaderless + Quorum

Amazon DynamoDB - Leaderless

------------------------------------------------------------------------

# 9. Interview Questions

### Which replication model is simplest?

Single Leader.

### Which supports multi-region writes?

Multi Leader.

### Which databases commonly use leaderless replication?

Cassandra and DynamoDB.

### What is Quorum?

A read/write rule ensuring sufficient replicas participate to improve
consistency.

------------------------------------------------------------------------

# 10. Cheat Sheet

    Single Leader
    ↓

    1 Writer
    Many Readers

    ----------------

    Multi Leader
    ↓

    Many Writers

    ----------------

    Leaderless
    ↓

    Any Replica

    ----------------

    Quorum

    R + W > N

------------------------------------------------------------------------

Next Chapter: **Part 6.3 -- Partitioning (Sharding)**
