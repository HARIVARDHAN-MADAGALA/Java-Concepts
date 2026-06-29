# System Design Interview Handbook

## Case Study 1 -- Design a URL Shortener (TinyURL / Bit.ly)

------------------------------------------------------------------------

# Table of Contents

1.  Problem Statement
2.  Functional Requirements
3.  Non-Functional Requirements
4.  Capacity Estimation
5.  High-Level Architecture
6.  API Design
7.  Database Design
8.  Short URL Generation
9.  Redirect Flow
10. Caching
11. Scaling
12. Trade-offs
13. Interview Questions
14. Cheat Sheet

------------------------------------------------------------------------

# 1. Problem Statement

Design a service that converts:

https://example.com/very/long/url

into

https://tiny.ly/Ab3XyZ

When users open the short URL, they should be redirected to the original
URL.

------------------------------------------------------------------------

# 2. Functional Requirements

-   Create short URL
-   Redirect to original URL
-   Optional custom alias
-   URL expiration (optional)
-   Click analytics (optional)

------------------------------------------------------------------------

# 3. Non-Functional Requirements

-   Very low redirect latency
-   High availability
-   Massive read traffic
-   Horizontally scalable

------------------------------------------------------------------------

# 4. Capacity Estimation

Assume:

-   10 million new URLs/day
-   100 million redirects/day

Reads greatly outnumber writes.

------------------------------------------------------------------------

# 5. High-Level Architecture

    Client
      │
      ▼
    DNS
      │
      ▼
    Load Balancer
      │
      ▼
    API Servers
      │
      ├────► Redis Cache
      │
      ▼
    SQL / NoSQL Database

------------------------------------------------------------------------

# 6. API Design

Create

POST /api/v1/shorten

``` json
{
  "longUrl":"https://example.com/page"
}
```

Response

``` json
{
  "shortUrl":"https://tiny.ly/Ab3XyZ"
}
```

Redirect

GET /Ab3XyZ

→ HTTP 302 Redirect

------------------------------------------------------------------------

# 7. Database Design

  Column       Description
  ------------ --------------
  id           Primary Key
  short_code   Unique
  long_url     Original URL
  created_at   Timestamp
  expires_at   Optional

  : URLS

------------------------------------------------------------------------

# 8. Short URL Generation

Possible techniques:

-   Auto-increment ID + Base62 encoding
-   Random Base62 string
-   Hashing (with collision handling)

Base62 uses:

A-Z, a-z, 0-9

------------------------------------------------------------------------

# 9. Redirect Flow

    User

    ↓

    GET /Ab3XyZ

    ↓

    Redis

    ↓

    (Cache Miss)

    ↓

    Database

    ↓

    302 Redirect

    ↓

    Browser opens original URL

------------------------------------------------------------------------

# 10. Caching

Cache popular short URLs in Redis.

Benefits:

-   Lower database load
-   Faster redirects

TTL may be applied for inactive URLs.

------------------------------------------------------------------------

# 11. Scaling

-   Multiple API servers
-   Redis cluster
-   Read replicas
-   Database sharding
-   CDN for static assets
-   Load balancer

------------------------------------------------------------------------

# 12. Trade-offs

  Decision     Benefit         Cost
  ------------ --------------- -------------------
  Redis        Fast lookups    Cache consistency
  Random IDs   Hard to guess   Collision checks
  Base62       Short URLs      Encoding logic

------------------------------------------------------------------------

# 13. Interview Questions

-   Why Base62 instead of Base64?
-   Why use HTTP 302 instead of 301?
-   How do you avoid duplicate short codes?
-   How would you collect click analytics?
-   How would you support custom aliases?

------------------------------------------------------------------------

# 14. Cheat Sheet

    POST /shorten

    ↓

    Generate Code

    ↓

    Store Mapping

    ↓

    Redis + Database

    ----------------

    GET /code

    ↓

    Redis

    ↓

    Database (if miss)

    ↓

    302 Redirect
