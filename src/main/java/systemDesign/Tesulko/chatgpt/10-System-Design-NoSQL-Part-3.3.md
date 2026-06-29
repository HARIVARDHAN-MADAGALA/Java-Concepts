# System Design Handbook

## Part 3.3 -- NoSQL Databases

------------------------------------------------------------------------

# Table of Contents

1.  Why NoSQL?
2.  What is NoSQL?
3.  SQL vs NoSQL Mindset
4.  CAP Perspective
5.  Advantages
6.  Limitations
7.  Real-World Examples
8.  Spring Boot Mapping
9.  Interview Questions
10. Cheat Sheet

------------------------------------------------------------------------

# 1. Why NoSQL?

Traditional SQL databases work well for many applications.

But modern systems like Instagram, WhatsApp and Netflix generate:

-   Billions of records
-   Massive traffic
-   Rapidly changing data
-   Flexible schemas

Scaling a single relational database becomes difficult.

NoSQL was introduced to solve these problems.

------------------------------------------------------------------------

# 2. What is NoSQL?

NoSQL means **Not Only SQL**.

It refers to databases that do not rely on the traditional relational
table model.

Examples:

-   MongoDB
-   Redis
-   Cassandra
-   Neo4j

------------------------------------------------------------------------

# 3. SQL vs NoSQL Mindset

## SQL

    Tables
     ↓
    Rows
     ↓
    Columns

Strong relationships and fixed schema.

## NoSQL

    Documents

    Key-Value

    Columns

    Graphs

Flexible storage models.

------------------------------------------------------------------------

# 4. When Should You Choose NoSQL?

Choose NoSQL when:

-   Horizontal scaling is required.
-   Schema changes frequently.
-   High read/write throughput is needed.
-   Relationships are simple.

Avoid NoSQL when:

-   Complex joins are required.
-   Strong ACID transactions are critical.

------------------------------------------------------------------------

# 5. Advantages

-   Horizontal scalability
-   Flexible schema
-   High availability
-   Fast writes
-   Distributed architecture

------------------------------------------------------------------------

# 6. Limitations

-   Limited joins
-   Eventual consistency (many systems)
-   Different query languages
-   Data duplication may occur

------------------------------------------------------------------------

# 7. Real-World Examples

## MongoDB

Store user profiles.

## Redis

Caching sessions.

## Cassandra

Large-scale event logging.

## Neo4j

Social network relationships.

------------------------------------------------------------------------

# 8. Spring Boot Mapping

Popular dependencies:

-   Spring Data MongoDB
-   Spring Data Redis

Example repositories:

``` java
MongoRepository<User, String>

CrudRepository<String, Object>
```

------------------------------------------------------------------------

# 9. Interview Questions

### What does NoSQL mean?

Not Only SQL.

### Why was NoSQL introduced?

To support flexible schemas and massive horizontal scalability.

### Is NoSQL replacing SQL?

No.

Most enterprise systems use both.

------------------------------------------------------------------------

# 10. Cheat Sheet

    SQL

    ↓

    Structured Tables

    Strong ACID

    Joins

    ------------------

    NoSQL

    ↓

    Flexible Schema

    Horizontal Scaling

    High Throughput

    Different Storage Models

------------------------------------------------------------------------

Next Chapter: **Part 3.4 -- Types of NoSQL Databases (Key-Value,
Document, Column-Family, Graph)**
