# System Design Handbook

## Part 3.5 -- SQL vs NoSQL (Complete Comparison)

------------------------------------------------------------------------

# Table of Contents

1.  Why Compare SQL and NoSQL?
2.  SQL Overview
3.  NoSQL Overview
4.  Feature Comparison
5.  ACID vs BASE
6.  When to Choose SQL
7.  When to Choose NoSQL
8.  Real-World Architectures
9.  Spring Boot Mapping
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Compare SQL and NoSQL?

There is no "best" database.

The correct choice depends on the application's requirements.

Many modern systems use both.

------------------------------------------------------------------------

# 2. SQL Overview

Characteristics

-   Relational tables
-   Fixed schema
-   ACID transactions
-   Strong consistency
-   Joins

Examples

-   MySQL
-   PostgreSQL
-   Oracle
-   SQL Server

------------------------------------------------------------------------

# 3. NoSQL Overview

Characteristics

-   Flexible schema
-   Horizontal scaling
-   High availability
-   Different storage models
-   Massive throughput

Examples

-   MongoDB
-   Redis
-   Cassandra
-   Neo4j

------------------------------------------------------------------------

# 4. Feature Comparison

  Feature        SQL                    NoSQL
  -------------- ---------------------- ------------------------------
  Schema         Fixed                  Flexible
  Joins          Excellent              Limited
  Transactions   Strong                 Depends on DB
  Scaling        Vertical (primarily)   Horizontal
  Consistency    Strong                 Often Eventual
  Best For       Business apps          Large-scale distributed apps

------------------------------------------------------------------------

# 5. ACID vs BASE

## ACID

-   Atomicity
-   Consistency
-   Isolation
-   Durability

Used by relational databases.

Perfect for:

-   Banking
-   Payments
-   Inventory

------------------------------------------------------------------------

## BASE

-   Basically Available
-   Soft State
-   Eventual Consistency

Used by many NoSQL databases.

Perfect for:

-   Social Media
-   Analytics
-   Logging

------------------------------------------------------------------------

# 6. When Should You Choose SQL?

Choose SQL when:

-   Transactions are critical.
-   Data relationships are complex.
-   Data integrity is important.
-   Reporting requires joins.

Examples

-   Banking
-   HR
-   ERP
-   Payment Systems

------------------------------------------------------------------------

# 7. When Should You Choose NoSQL?

Choose NoSQL when:

-   Traffic is massive.
-   Schema changes frequently.
-   Horizontal scaling is required.
-   High read/write throughput is important.

Examples

-   Instagram
-   WhatsApp
-   Netflix
-   IoT Platforms

------------------------------------------------------------------------

# 8. Real-World Architectures

## Amazon

SQL

-   Orders
-   Payments
-   Inventory

NoSQL

-   Product Catalog
-   Recommendations
-   Session Cache

------------------------------------------------------------------------

## Netflix

SQL

-   Billing

NoSQL

-   Viewing History
-   User Preferences
-   Recommendations

------------------------------------------------------------------------

## Instagram

SQL

-   User Accounts

NoSQL

-   Feed Cache
-   Activity Streams
-   Media Metadata

------------------------------------------------------------------------

# 9. Spring Boot Mapping

SQL

``` java
JpaRepository<User,Long>
```

MongoDB

``` java
MongoRepository<User,String>
```

Redis

``` java
RedisTemplate<String,Object>
```

------------------------------------------------------------------------

# 10. Interview Questions

### Which database is better?

Neither.

Choose based on requirements.

### Can SQL and NoSQL be used together?

Yes.

This is common in microservices.

### Which supports stronger transactions?

SQL.

### Which scales horizontally more easily?

NoSQL.

------------------------------------------------------------------------

# 11. Cheat Sheet

    SQL

    ✔ ACID
    ✔ Joins
    ✔ Transactions
    ✔ Fixed Schema

    ------------------

    NoSQL

    ✔ Flexible Schema
    ✔ Horizontal Scaling
    ✔ High Throughput
    ✔ Massive Data

------------------------------------------------------------------------

## Key Takeaway

Don't ask:

> SQL or NoSQL?

Ask:

> Which database best solves this problem?

------------------------------------------------------------------------

Next Chapter: **Part 4.1 -- Caching Fundamentals**
