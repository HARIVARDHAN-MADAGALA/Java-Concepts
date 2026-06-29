# System Design Handbook

## Part 4.2 -- Cache Strategies

------------------------------------------------------------------------

# Table of Contents

1.  Why Cache Strategies?
2.  Read Through Cache
3.  Write Through Cache
4.  Write Around Cache
5.  Write Back Cache
6.  Strategy Comparison
7.  Real-World Examples
8.  Spring Boot Mapping
9.  Interview Questions
10. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Cache Strategies?

Caching is not just about storing data.

The biggest question is:

**How should cache and database stay synchronized?**

Different applications solve this differently.

------------------------------------------------------------------------

# 2. Read Through Cache

Application checks cache first.

    Application
        │
        ▼
     Cache
     ┌──┴──┐
    Hit   Miss
     │      │
     ▼      ▼
    Data  Database
            │
            ▼
       Update Cache
            │
            ▼
          Return

### Advantages

-   Simple
-   Reduces database load

### Disadvantages

-   First request is slow

### Best For

-   Product Catalog
-   User Profiles

------------------------------------------------------------------------

# 3. Write Through Cache

Every write updates both cache and database.

    Application
       │
       ▼
    Cache
       │
       ▼
    Database

### Advantages

-   Cache always fresh

### Disadvantages

-   Slower writes

### Best For

-   Banking
-   Inventory

------------------------------------------------------------------------

# 4. Write Around Cache

Writes go directly to database.

Cache is updated only when data is read.

    Write

    ↓

    Database

    (Read later)

    ↓

    Cache

### Advantages

-   Cache stores only frequently used data

### Disadvantages

-   First read after write is slow

### Best For

-   Large datasets
-   Analytics

------------------------------------------------------------------------

# 5. Write Back Cache

Application writes only to cache.

Cache updates database later.

    Application
       │
       ▼
    Cache

    (Later)

    ↓

    Database

### Advantages

-   Very fast writes

### Disadvantages

-   Data loss possible if cache crashes before persistence

### Best For

-   Logging
-   Metrics
-   Gaming

------------------------------------------------------------------------

# 6. Comparison

  Strategy        Read Speed              Write Speed   Consistency
  --------------- ----------------------- ------------- -------------
  Read Through    Fast after first read   Normal        Good
  Write Through   Fast                    Slower        Strong
  Write Around    Fast after cache fill   Normal        Good
  Write Back      Very Fast               Very Fast     Eventual

------------------------------------------------------------------------

# 7. Real-World Examples

-   **Amazon Product Pages** → Read Through
-   **Bank Balance** → Write Through
-   **Analytics/Event Logs** → Write Back
-   **Large Product Imports** → Write Around

------------------------------------------------------------------------

# 8. Spring Boot Mapping

Read Through

``` java
@Cacheable("products")
```

Write Through

``` java
@CachePut("products")
```

Evict after update

``` java
@CacheEvict("products")
```

------------------------------------------------------------------------

# 9. Interview Questions

### Which strategy gives fastest writes?

Write Back.

### Which strategy keeps cache always consistent?

Write Through.

### Which strategy reduces unnecessary cache entries?

Write Around.

### Which strategy is most common?

Read Through.

------------------------------------------------------------------------

# 10. Cheat Sheet

    Read Through
    Read → Cache → DB

    -------------------

    Write Through
    Write → Cache + DB

    -------------------

    Write Around
    Write → DB
    Read → Cache

    -------------------

    Write Back
    Write → Cache
    Later → DB

------------------------------------------------------------------------

Next Chapter: **Part 4.3 -- Cache Eviction Policies (LRU, LFU, FIFO,
MRU, LIFO)**
