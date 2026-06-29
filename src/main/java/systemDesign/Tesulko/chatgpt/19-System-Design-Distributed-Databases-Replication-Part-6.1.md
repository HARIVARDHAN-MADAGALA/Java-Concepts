# System Design Handbook

## Part 6.1 -- Distributed Databases & Replication

------------------------------------------------------------------------

# Table of Contents

1.  Why Distributed Databases?
2.  What is Replication?
3.  Primary (Leader) & Replica
4.  Read and Write Flow
5.  Benefits
6.  Challenges
7.  Synchronous vs Asynchronous Replication
8.  Failover
9.  Real-World Examples
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Distributed Databases?

A single database server eventually becomes a bottleneck.

Problems:

-   Too many read requests
-   Hardware limits
-   Single point of failure
-   Maintenance downtime

Solution:

Use multiple database servers.

------------------------------------------------------------------------

# 2. What is Replication?

Replication means maintaining copies of the same data on multiple
database servers.

    Primary Database

       │

    Replicates

       ▼

    Replica 1

    Replica 2

------------------------------------------------------------------------

# 3. Primary (Leader) & Replica

Primary (Leader)

-   Accepts writes
-   Inserts
-   Updates
-   Deletes

Replica

-   Receives copied data
-   Mainly serves read requests

------------------------------------------------------------------------

# 4. Read and Write Flow

    Application

    Write
     │
     ▼
    Primary Database
     │
     ▼
    Replicate
     │
     ├────► Replica 1
     └────► Replica 2

    Read Requests
         │
         ├──► Replica 1
         └──► Replica 2

Benefits:

-   Faster reads
-   Reduced load on primary

------------------------------------------------------------------------

# 5. Benefits

-   High availability
-   Better read scalability
-   Backup copies
-   Disaster recovery

------------------------------------------------------------------------

# 6. Challenges

-   Replication lag
-   Network failures
-   Replica consistency
-   Failover complexity

------------------------------------------------------------------------

# 7. Synchronous vs Asynchronous Replication

## Synchronous

Primary waits until replica confirms.

Pros

-   Strong consistency

Cons

-   Slower writes

------------------------------------------------------------------------

## Asynchronous

Primary responds immediately.

Replication happens later.

Pros

-   Faster writes

Cons

-   Temporary replication lag

------------------------------------------------------------------------

# 8. Failover

If Primary fails:

    Primary ❌

    ↓

    Replica promoted

    ↓

    New Primary

Applications continue with minimal downtime.

------------------------------------------------------------------------

# 9. Real-World Examples

MySQL

-   Primary + Read Replicas

PostgreSQL

-   Streaming Replication

MongoDB

-   Replica Sets

AWS RDS

-   Multi-AZ Replication
-   Read Replicas

------------------------------------------------------------------------

# 10. Interview Questions

### Why use replication?

To improve availability and read scalability.

### Which database accepts writes?

Primary (Leader).

### Why can replicas return stale data?

Because asynchronous replication may not have completed.

### What is failover?

Automatically promoting a replica when the primary fails.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Writes

    ↓

    Primary

    ↓

    Replication

    ↓

    Replica 1
    Replica 2

    Reads

    ↓

    Replicas

    ----------------

    Sync

    ↓

    Strong Consistency

    ----------------

    Async

    ↓

    Fast Writes
    Possible Lag

------------------------------------------------------------------------

Next Chapter: **Part 6.2 -- Replication Algorithms (Single Leader, Multi
Leader, Leaderless)**
