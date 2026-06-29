# System Design Interview Handbook

## Case Study 2 -- Design Pastebin

------------------------------------------------------------------------

# Table of Contents

1.  Problem Statement
2.  Functional Requirements
3.  Non-Functional Requirements
4.  Capacity Estimation
5.  High-Level Architecture
6.  API Design
7.  Database Design
8.  Paste Creation Flow
9.  Paste Retrieval Flow
10. Caching
11. Scaling
12. Trade-offs
13. Interview Questions
14. Cheat Sheet

------------------------------------------------------------------------

# 1. Problem Statement

Design a service similar to Pastebin where users can store text and
share it using a short URL.

Example:

Input:

    Hello World!

Output:

    https://paste.ly/aB12Xz

Opening the URL should display the stored text.

------------------------------------------------------------------------

# 2. Functional Requirements

-   Create a paste
-   Retrieve a paste
-   Optional custom URL
-   Optional expiration time
-   Public/Private visibility
-   Delete a paste (optional)

------------------------------------------------------------------------

# 3. Non-Functional Requirements

-   High availability
-   Low read latency
-   Horizontally scalable
-   Durable storage
-   Millions of pastes

------------------------------------------------------------------------

# 4. Capacity Estimation

Assumptions:

-   5 million new pastes/day
-   50 million reads/day
-   Average paste size: 5 KB

Read traffic is much higher than write traffic.

------------------------------------------------------------------------

# 5. High-Level Architecture

    Users
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
    Database

------------------------------------------------------------------------

# 6. API Design

Create Paste

POST /api/v1/pastes

``` json
{
  "content":"Hello World",
  "expireAfter":"7d"
}
```

Response

``` json
{
  "url":"https://paste.ly/aB12Xz"
}
```

Retrieve Paste

GET /aB12Xz

Delete Paste

DELETE /api/v1/pastes/{id}

------------------------------------------------------------------------

# 7. Database Design

  Column       Description
  ------------ ----------------
  id           Primary Key
  short_code   Unique
  content      Paste Text
  visibility   Public/Private
  expires_at   Expiration
  created_at   Timestamp

  : PASTES

------------------------------------------------------------------------

# 8. Paste Creation Flow

    User

    ↓

    POST /pastes

    ↓

    Generate Short Code

    ↓

    Store in Database

    ↓

    Cache Metadata (optional)

    ↓

    Return URL

------------------------------------------------------------------------

# 9. Paste Retrieval Flow

    User

    ↓

    GET /aB12Xz

    ↓

    Redis Cache

    ↓

    (Cache Miss)

    ↓

    Database

    ↓

    Display Paste

Expired pastes should return:

    404 Not Found

------------------------------------------------------------------------

# 10. Caching

Cache frequently accessed pastes.

Benefits:

-   Lower database load
-   Faster response time

Apply TTL to match paste expiration.

------------------------------------------------------------------------

# 11. Scaling

-   Multiple API servers
-   Redis cluster
-   Database read replicas
-   Sharding by paste ID
-   CDN for static assets
-   Load balancer

------------------------------------------------------------------------

# 12. Trade-offs

  Decision          Benefit              Cost
  ----------------- -------------------- ------------------------
  Redis Cache       Fast reads           Cache invalidation
  Sharding          Horizontal scaling   Operational complexity
  Expiring pastes   Saves storage        Cleanup jobs

------------------------------------------------------------------------

# 13. Interview Questions

-   How would you generate unique paste URLs?
-   How would you automatically delete expired pastes?
-   SQL or NoSQL for Pastebin?
-   How would you support very large pastes?
-   How would you track paste views?

------------------------------------------------------------------------

# 14. Cheat Sheet

\`\`\` POST /pastes │ Generate Code │ Store Content │ Redis + Database

------------------------------------------------------------------------

GET /code │ Redis │ Database (if miss) │ Display Paste

------------------------------------------------------------------------

Expiration

↓

TTL + Cleanup Job
