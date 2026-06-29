# System Design Handbook

## Part 4.1 -- Caching Fundamentals

------------------------------------------------------------------------

# Table of Contents

1.  Why Caching?
2.  What is Cache?
3.  Cache Hit vs Cache Miss
4.  Time To Live (TTL)
5.  Cache Architecture
6.  In-Memory vs Distributed Cache
7.  Redis Introduction
8.  Cache Challenges
9.  Spring Boot Mapping
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Caching?

Databases are much slower than RAM.

If thousands of users repeatedly request the same data, querying the
database every time wastes CPU, disk I/O and network bandwidth.

A cache stores frequently accessed data closer to the application.

------------------------------------------------------------------------

# 2. What is Cache?

A cache is a temporary storage layer that keeps frequently used data in
memory.

    Client
       │
       ▼
    Application
       │
       ▼
    Cache
       │
    (Cache Hit?)
       │
     ┌─Yes────► Return Data
     │
     No
     │
     ▼
    Database
     │
     ▼
    Store in Cache
     │
     ▼
    Return Data

------------------------------------------------------------------------

# 3. Cache Hit vs Cache Miss

## Cache Hit

The requested data already exists in cache.

Benefits

-   Very fast
-   No database call

## Cache Miss

The data is absent.

Flow

    Cache Miss

    ↓

    Database

    ↓

    Update Cache

    ↓

    Return Response

------------------------------------------------------------------------

# 4. Time To Live (TTL)

Cache entries should not live forever.

TTL defines how long cached data remains valid.

Example

    Product Cache

    TTL = 10 minutes

After expiration, the next request reloads fresh data.

------------------------------------------------------------------------

# 5. Cache Architecture

    Users
      │
      ▼
    Application
      │
      ▼
    Redis Cache
      │
      ▼
    MySQL

Most requests stop at Redis.

Only cache misses reach MySQL.

------------------------------------------------------------------------

# 6. In-Memory vs Distributed Cache

## In-Memory Cache

-   Stored inside application memory
-   Very fast
-   Lost when application restarts

Examples

-   ConcurrentHashMap
-   Caffeine

## Distributed Cache

Shared by multiple servers.

Examples

-   Redis
-   Memcached

Suitable for microservices.

------------------------------------------------------------------------

# 7. Redis Introduction

Redis is an in-memory key-value database.

Why Redis?

-   Extremely fast
-   Supports TTL
-   Shared cache
-   High throughput

Common Uses

-   Product Cache
-   User Sessions
-   OTP Storage
-   Rate Limiting
-   Leaderboards

------------------------------------------------------------------------

# 8. Cache Challenges

Problems

-   Stale Data
-   Cache Invalidation
-   Memory Limits
-   Cache Stampede

Always decide:

-   What to cache?
-   When to refresh?
-   When to remove?

------------------------------------------------------------------------

# 9. Spring Boot Mapping

Enable caching

``` java
@EnableCaching
```

Cache a method

``` java
@Cacheable("products")
public Product getProduct(Long id)
```

Remove cache

``` java
@CacheEvict("products")
```

Update cache

``` java
@CachePut("products")
```

------------------------------------------------------------------------

# 10. Interview Questions

### Why use cache?

To reduce database load and improve response time.

### Why is Redis popular?

Because it stores data in memory and provides extremely low latency.

### What is TTL?

The lifetime of a cached item.

### What happens during a cache miss?

The application fetches data from the database, updates the cache and
returns the response.

------------------------------------------------------------------------

# 11. Cheat Sheet

    User
     │
     ▼
    Application
     │
     ▼
    Cache
     │
     ├── Hit  → Return
     │
     └── Miss
           │
           ▼
       Database
           │
           ▼
     Update Cache

    TTL → Expiration Time

    Redis → Distributed Cache

------------------------------------------------------------------------

Next Chapter: **Part 4.2 -- Cache Strategies (Read Through, Write
Through, Write Around, Write Back)**
