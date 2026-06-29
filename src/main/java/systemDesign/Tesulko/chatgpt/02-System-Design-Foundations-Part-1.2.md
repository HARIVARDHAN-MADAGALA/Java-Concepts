# System Design Handbook

## Part 1.2 -- Core Components of System Design

------------------------------------------------------------------------

# Table of Contents

1.  Why Components Matter
2.  Client
3.  Server
4.  Database
5.  Cache
6.  Load Balancer
7.  Message Queue
8.  Monitoring & Observability
9.  Complete Architecture
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Components Matter

Every large application (Amazon, Netflix, Instagram, WhatsApp) is built
using a small set of reusable building blocks.

    Client
       ↓
    Load Balancer
       ↓
    Application Servers
       ↓
    Cache
       ↓
    Database

Understanding these blocks is more important than memorizing
technologies.

------------------------------------------------------------------------

# 2. Client

A client is anything that sends requests.

Examples:

-   Mobile App
-   Web Browser
-   Smart TV
-   ATM
-   Smart Watch

Example

    Browser

    ↓

    GET /products

    ↓

    Server

The client never directly talks to the database.

------------------------------------------------------------------------

# 3. Server

A server receives requests and executes business logic.

Example:

    User Login

    ↓

    Validate User

    ↓

    Generate JWT

    ↓

    Return Response

Responsibilities

-   Validation
-   Business Logic
-   Calling Database
-   Calling Other Services

------------------------------------------------------------------------

# 4. Database

Purpose

Store application data permanently.

Examples

-   Users
-   Orders
-   Products
-   Payments

```{=html}
<!-- -->
```
    Server

    ↓

    Database

    ↓

    Rows Returned

Without databases, application data disappears after restart.

------------------------------------------------------------------------

# 5. Cache

Problem

Database is slower than RAM.

Frequently requested data should not always hit the database.

Solution

Cache.

    Client

    ↓

    Server

    ↓

    Cache

    ↓

    Database

If data exists in cache:

    Cache Hit

Otherwise

    Cache Miss

    ↓

    Read Database

    ↓

    Update Cache

Benefits

-   Faster response
-   Lower DB load
-   Better scalability

Popular Technology

-   Redis

------------------------------------------------------------------------

# 6. Load Balancer

Problem

One server cannot handle millions of users.

Solution

Place a Load Balancer before servers.

    Users

    ↓

    Load Balancer

    ↙      ↓      ↘

    S1     S2      S3

Responsibilities

-   Distribute requests
-   Detect failed servers
-   Send traffic only to healthy servers

------------------------------------------------------------------------

# 7. Message Queue

Problem

Some tasks don't need an immediate response.

Example

User places order.

Immediately after order:

-   Send Email
-   Send SMS
-   Update Analytics

Instead of making the user wait:

    Order Service

    ↓

    Message Queue

    ↓

    Email Service

    SMS Service

    Analytics Service

Benefits

-   Faster user response
-   Loose coupling
-   Better scalability

Popular Technologies

-   Kafka
-   RabbitMQ

------------------------------------------------------------------------

# 8. Monitoring & Observability

After deployment, you must continuously monitor your application.

Track

-   CPU Usage
-   Memory Usage
-   Error Rate
-   Latency
-   Throughput
-   Logs

Without monitoring:

Problems remain invisible until customers complain.

Popular Tools

-   Prometheus
-   Grafana
-   ELK Stack

------------------------------------------------------------------------

# 9. Complete Architecture

    Client
       │
       ▼
    Load Balancer
       │
       ▼
    Application Servers
       │
       ├──────────────┐
       ▼              ▼
     Cache        Message Queue
       │              │
       ▼              ▼
    Database     Email/SMS Services

------------------------------------------------------------------------

# 10. Interview Questions

### Why shouldn't clients access databases directly?

Security, validation and business logic belong on the server.

### Why use cache?

To reduce database load and improve response time.

### Why use a message queue?

To process slow tasks asynchronously.

### What is the responsibility of a Load Balancer?

To distribute traffic and avoid server overload.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Client
       ↓
    Load Balancer
       ↓
    Server
       ↓
    Cache
       ↓
    Database

    Background Tasks

    ↓

    Message Queue

    ↓

    Worker Services

    Monitor Everything

------------------------------------------------------------------------

Next Chapter: **Part 1.3 -- Data Intensive vs Compute Intensive
Applications**
