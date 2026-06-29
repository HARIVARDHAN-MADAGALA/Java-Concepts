# System Design Handbook

## Part 6.3 -- Partitioning (Sharding)

------------------------------------------------------------------------

# Table of Contents

1.  Why Partitioning?
2.  What is Sharding?
3.  Horizontal vs Vertical Partitioning
4.  Partition by Key
5.  Hash-Based Partitioning
6.  Range-Based Partitioning
7.  Secondary Index Partitioning
8.  Hotspots
9.  Rebalancing & Consistent Hashing
10. Real-World Examples
11. Interview Questions
12. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Partitioning?

As data grows, a single database cannot efficiently store or process
everything.

Problems:

-   Huge tables
-   Slow queries
-   Storage limits
-   Write bottlenecks

Partitioning distributes data across multiple database servers.

------------------------------------------------------------------------

# 2. What is Sharding?

Sharding is a form of **horizontal partitioning** where data is split
across multiple databases (called shards).

    Users

    ↓

    Shard 1
    Shard 2
    Shard 3

Each shard stores only a subset of the data.

------------------------------------------------------------------------

# 3. Horizontal vs Vertical Partitioning

## Horizontal Partitioning

Split by rows.

    Users

    1 - 1,000,000  → Shard 1

    1,000,001 - 2,000,000 → Shard 2

------------------------------------------------------------------------

## Vertical Partitioning

Split by columns.

    User Table

    Personal Info

    ↓

    One Database

    Payment Info

    ↓

    Another Database

------------------------------------------------------------------------

# 4. Partition by Key

A key determines where data is stored.

Example

    User ID

    ↓

    Shard

    User 101

    ↓

    Shard 1

Simple and efficient.

------------------------------------------------------------------------

# 5. Hash-Based Partitioning

A hash function distributes data evenly.

Example

    hash(userId) % 3

    ↓

    Shard Number

Advantages

-   Even distribution
-   Reduces hotspots

Disadvantages

-   Difficult to rebalance

------------------------------------------------------------------------

# 6. Range-Based Partitioning

Store data based on ranges.

    1 - 1000

    ↓

    Shard 1

    1001 - 2000

    ↓

    Shard 2

Advantages

-   Easy range queries

Disadvantages

-   Can create hotspots

------------------------------------------------------------------------

# 7. Secondary Index Partitioning

Indexes may need separate partitioning.

Challenge:

Data may exist on one shard while indexes exist elsewhere.

Requires distributed query planning.

------------------------------------------------------------------------

# 8. Hotspots

Problem

Too many requests target one shard.

Example

    Celebrity User

    ↓

    Millions of Followers

    ↓

    Single Shard Overloaded

Solutions

-   Better partition keys
-   Hashing
-   Additional replicas

------------------------------------------------------------------------

# 9. Rebalancing & Consistent Hashing

When adding a new shard, data may need redistribution.

Consistent hashing minimizes movement.

    Old

    3 Shards

    ↓

    Add Shard

    ↓

    Only some keys move

------------------------------------------------------------------------

# 10. Real-World Examples

Instagram

-   User-based sharding

Amazon

-   Customer partitioning

Cassandra

-   Hash partitioning

MongoDB

-   Shard Keys

------------------------------------------------------------------------

# 11. Interview Questions

### Why is sharding needed?

To distribute data and scale horizontally.

### Difference between replication and sharding?

Replication copies data.

Sharding splits data.

### Which partitioning supports efficient range queries?

Range partitioning.

### What is a hotspot?

One shard receiving disproportionate traffic.

------------------------------------------------------------------------

# 12. Cheat Sheet

    Replication

    ↓

    Copies Data

    ----------------

    Sharding

    ↓

    Splits Data

    ----------------

    Horizontal

    ↓

    Rows

    ----------------

    Vertical

    ↓

    Columns

    ----------------

    Hash

    ↓

    Even Distribution

    ----------------

    Range

    ↓

    Easy Queries

------------------------------------------------------------------------

Next Chapter: **Part 6.4 -- CAP Theorem**
