# System Design Handbook

## Part 4.3 -- Cache Eviction Policies

------------------------------------------------------------------------

# Table of Contents

1.  Why Eviction Policies?
2.  LRU (Least Recently Used)
3.  LFU (Least Frequently Used)
4.  FIFO (First In First Out)
5.  LIFO (Last In First Out)
6.  MRU (Most Recently Used)
7.  Comparison Table
8.  Redis Eviction Policies
9.  Real-World Examples
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Eviction Policies?

A cache has limited memory.

When it becomes full, the system must decide:

> Which cached item should be removed?

This decision is made by an eviction policy.

------------------------------------------------------------------------

# 2. LRU (Least Recently Used)

Removes the item that has not been accessed for the longest time.

Example

    Cache Size = 3

    A  B  C

    Access A

    Order

    B C A

    Insert D

    Remove B

### Best For

-   Product Catalog
-   User Sessions
-   Web Applications

Advantages

-   Simple
-   Works well for most workloads

------------------------------------------------------------------------

# 3. LFU (Least Frequently Used)

Removes the item accessed the fewest times.

Example

    A -> 10 accesses

    B -> 2 accesses

    C -> 1 access

    Remove C

### Best For

-   Frequently requested reports
-   Recommendation engines

------------------------------------------------------------------------

# 4. FIFO (First In First Out)

Removes the oldest inserted item.

    Insert

    A

    B

    C

    Insert D

    Remove A

Simple but ignores access patterns.

------------------------------------------------------------------------

# 5. LIFO (Last In First Out)

Removes the most recently inserted item.

    Insert

    A

    B

    C

    Insert D

    Remove D

Rarely used in caching.

------------------------------------------------------------------------

# 6. MRU (Most Recently Used)

Removes the item used most recently.

Useful when recently accessed items are unlikely to be requested again.

Rare compared to LRU.

------------------------------------------------------------------------

# 7. Comparison

  Policy   Removes                 Best For
  -------- ----------------------- -----------------------
  LRU      Least Recently Used     Web Apps
  LFU      Least Frequently Used   Analytics
  FIFO     Oldest Entry            Simple Queues
  LIFO     Newest Entry            Special Cases
  MRU      Most Recently Used      Specialized Workloads

------------------------------------------------------------------------

# 8. Redis Eviction Policies

Common Redis policies:

-   volatile-lru
-   allkeys-lru
-   volatile-lfu
-   allkeys-lfu
-   volatile-random
-   allkeys-random
-   noeviction

In production, **allkeys-lru** is one of the most commonly used
policies.

------------------------------------------------------------------------

# 9. Real-World Examples

### LRU

-   Amazon product pages
-   User sessions

### LFU

-   Trending products
-   Frequently viewed reports

### FIFO

-   Simple queue processing

------------------------------------------------------------------------

# 10. Interview Questions

### Which cache eviction policy is most common?

LRU.

### Difference between LRU and LFU?

LRU considers **recency**.

LFU considers **frequency**.

### Why isn't FIFO ideal for caches?

Because it ignores how often or how recently data is accessed.

------------------------------------------------------------------------

# 11. Cheat Sheet

    LRU

    ↓

    Least Recently Used

    -------------------

    LFU

    ↓

    Least Frequently Used

    -------------------

    FIFO

    ↓

    Oldest Entry

    -------------------

    LIFO

    ↓

    Newest Entry

    -------------------

    MRU

    ↓

    Most Recently Used

------------------------------------------------------------------------

Next Chapter: **Part 5.1 -- Load Balancer Fundamentals**
