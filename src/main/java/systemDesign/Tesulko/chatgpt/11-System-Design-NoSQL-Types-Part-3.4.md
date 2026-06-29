# System Design Handbook

## Part 3.4 -- Types of NoSQL Databases

------------------------------------------------------------------------

# Table of Contents

1.  Why Multiple NoSQL Types?
2.  Key-Value Databases
3.  Document Databases
4.  Column-Family Databases
5.  Graph Databases
6.  Comparison Table
7.  Real-World Examples
8.  Spring Boot Mapping
9.  Interview Questions
10. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Multiple NoSQL Types?

No single database is ideal for every problem.

Different applications have different requirements:

-   Fast lookups
-   Flexible documents
-   Massive write throughput
-   Relationship traversal

This led to different categories of NoSQL databases.

------------------------------------------------------------------------

# 2. Key-Value Databases

Stores data as:

    Key  --->  Value

Example

    user:101

    ↓

    {
     name:"Hari",
     age:26
    }

Characteristics

-   Extremely fast
-   Simple lookup
-   Perfect for caching

Popular Databases

-   Redis
-   Amazon DynamoDB (KV mode)

Use Cases

-   Sessions
-   Cache
-   Shopping Cart
-   OTP Storage

------------------------------------------------------------------------

# 3. Document Databases

Stores JSON-like documents.

Example

``` json
{
  "id":101,
  "name":"Hari",
  "skills":[
      "Java",
      "Spring Boot"
  ]
}
```

Characteristics

-   Flexible schema
-   Nested objects
-   Easy evolution

Popular Databases

-   MongoDB
-   Couchbase

Use Cases

-   User Profiles
-   CMS
-   Product Catalog

------------------------------------------------------------------------

# 4. Column-Family Databases

Instead of storing data row-by-row, they optimize storage by columns.

Designed for:

-   Huge datasets
-   Massive writes
-   Distributed systems

Popular Databases

-   Cassandra
-   HBase

Use Cases

-   IoT
-   Event Logs
-   Analytics
-   Time-Series Data

------------------------------------------------------------------------

# 5. Graph Databases

Data is represented as:

    Nodes

    ↓

    Relationships

    ↓

    Nodes

Example

    Hari

    ↓

    Friend

    ↓

    Ravi

    ↓

    Friend

    ↓

    Akhil

Popular Databases

-   Neo4j
-   Amazon Neptune

Use Cases

-   Social Networks
-   Fraud Detection
-   Recommendation Engines

------------------------------------------------------------------------

# 6. Comparison

  Type            Best For        Example
  --------------- --------------- -----------
  Key-Value       Cache           Redis
  Document        Flexible JSON   MongoDB
  Column-Family   Big Data        Cassandra
  Graph           Relationships   Neo4j

------------------------------------------------------------------------

# 7. Real-World Examples

Redis

-   Login sessions
-   OTP
-   Cache

MongoDB

-   User Profiles
-   Product Catalog

Cassandra

-   Netflix viewing history
-   IoT Sensors

Neo4j

-   Facebook Friends
-   LinkedIn Connections

------------------------------------------------------------------------

# 8. Spring Boot Mapping

Spring Data modules

-   Spring Data Redis
-   Spring Data MongoDB

Example

``` java
MongoRepository<User,String>
```

Redis Example

``` java
RedisTemplate<String,Object>
```

------------------------------------------------------------------------

# 9. Interview Questions

### Which NoSQL database is best for caching?

Redis.

### Which NoSQL database is best for social networks?

Graph databases like Neo4j.

### Which NoSQL database is best for JSON documents?

MongoDB.

### Which NoSQL database handles huge write throughput?

Cassandra.

------------------------------------------------------------------------

# 10. Cheat Sheet

    Key-Value

    ↓

    Redis

    Cache

    -------------------

    Document

    ↓

    MongoDB

    JSON

    -------------------

    Column

    ↓

    Cassandra

    Big Data

    -------------------

    Graph

    ↓

    Neo4j

    Relationships

------------------------------------------------------------------------

Next Chapter: **Part 3.5 -- SQL vs NoSQL (Complete Comparison)**
