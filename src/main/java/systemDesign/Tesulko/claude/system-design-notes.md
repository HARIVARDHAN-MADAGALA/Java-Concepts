# System Design — Complete Notes
*Source: TLE/Telescope System Design course (~5 hrs) — condensed interview-prep notes*

---

## Table of Contents
1. [What is System Design — The Alien Bank Story](#1-what-is-system-design)
2. [Core Components of a System](#2-core-components)
3. [Data-Intensive vs Compute-Intensive Apps](#3-data-vs-compute-intensive)
4. [Functional vs Non-Functional Requirements](#4-functional-vs-non-functional)
5. [DNS](#5-dns)
6. [APIs — Types & Communication](#6-apis)
7. [REST API Deep Dive](#7-rest-api-deep-dive)
8. [SQL Databases](#8-sql-databases)
9. [NoSQL Databases](#9-nosql-databases)
10. [Caching](#10-caching)
11. [Load Balancers](#11-load-balancers)
12. [Replication](#12-replication)
13. [Partitioning (Sharding)](#13-partitioning-sharding)
14. [CAP Theorem](#14-cap-theorem)
15. [Message Queues](#15-message-queues)
16. [Fault Tolerance](#16-fault-tolerance)
17. [Monitoring & Observability](#17-monitoring--observability)
18. [Case Study: Video Streaming System Design](#18-case-study-video-streaming-system)
19. [Quick Revision Table](#19-quick-revision-table)

---

## 1. What is System Design

> "Anyone can write code that works. System design is what makes it work for a million people at once."

A **system = components + a common goal**. Design = picking the right components for your requirements and improving the system as load grows.

### The "Alien Bank" thought experiment (5 issues → 5 concepts)

A single cashier serves customers (deposit/withdraw → receipt). As load grows, 5 problems appear, each mapping to a real system-design concept:

| # | Problem | Bank Fix | System Design Concept |
|---|---------|----------|------------------------|
| 1 | Cashier is slow (10 min/customer) | Train cashier to be faster | **Improve code quality** (better algorithms/DSA, reduce loop complexity) |
| 2 | Customer count keeps growing | Bigger desk, cash-counting machine, pre-filled forms | **Vertical scaling** (upgrade the single server) |
| 3 | Customers still wait too long (single counter) | Add a 2nd counter | **Horizontal scaling** (add more servers) |
| 4 | Two counters keep separate, unsynced records → data mismatch | Shared ledger between counters | **Centralized database** |
| 5 | One counter idle, other overloaded (gate is near counter 1) | Add a "middleman" who routes customers based on load | **Load balancer** |

Mapping back to architecture:
- Customers → **requests/traffic**
- Counter → **server**
- Cashier (the worker) → **application code**
- Middleman → **load balancer**
- Shared ledger → **centralized database**

```
Customers --> [Load Balancer] --> Counter 1 (Server + App code) --\
                                 --> Counter 2 (Server + App code) --> [Centralized DB]
```

Vertical scaling has a hard ceiling (you can't upgrade a single box forever) — that's why horizontal scaling + load balancing become unavoidable once traffic grows.

---

## 2. Core Components

Every modern app revolves around **data** (image / video / audio / text). The core components that move and serve that data:

```
[Client: web/mobile] <--HTTP/TCP/IP--> [Application Code / Server]
                                            |
                                            v
                                    [Database] <-- stores/retrieves data
                                            ^
                                            |
                                   [Cache] -- frequently used data, faster reads
                                            |
[Load Balancer] --> distributes requests across multiple [Servers]
                                            |
                              [Message Queue] --> async work (notifications, SMS, etc.)
                                            |
                            [Monitoring & Logs] --> observe health of everything above
```

- **Database** — persistent storage (SQL/NoSQL).
- **Application code / API layer** — hides DB complexity, exposes endpoints (GET/POST/etc.).
- **Client application** — web/mobile/ATM — consumes APIs.
- **Cache** — fast-access layer for frequently requested data, sits between app code and DB.
- **Load balancer** — distributes client requests across multiple servers; checks server health.
- **Message queue** — handles async work between services (e.g., order service → SMS service) without blocking the caller.
- **Monitoring & logs** — tracks errors, performance, and health across every component above.

**Why an application/API layer exists at all:** if every client had to know the DB schema and write raw queries, that's unscalable and insecure. The API layer hides table/document structure and exposes safe, versioned operations instead.

---

## 3. Data vs Compute-Intensive

The first question before solving *any* latency/scaling problem: **is this feature data-intensive or compute-intensive?** Fixing the wrong layer wastes money and doesn't solve the real bottleneck.

| | Data-Intensive | Compute-Intensive |
|---|---|---|
| Bottleneck | Moving/storing/fetching data | Heavy calculation |
| Fix by upgrading | DB, cache, network, server config | CPU/GPU, parallelism, algorithms |
| Examples | Instagram feed, WhatsApp messages, bank transactions, analytics dashboards, log processing | Image/video processing, ML training, simulations, cryptography |
| Key worries | Read speed, safe storage, concurrent users, server/DB/network failure | Computation speed, parallelization, cost of compute, CPU vs GPU choice |
| Typical solutions | Caching, sharding, replication, data consistency strategies | Parallel processing, better algorithms, GPU offload |

**Trick to classify a feature:** if time is lost in *data movement* → data-intensive. If time is lost in *computation* → compute-intensive.

A single app can be both depending on the feature: **YouTube** is data-intensive for *serving videos*, but compute-intensive for *generating recommendations*.

---

## 4. Functional vs Non-Functional

**Functional Requirements** = the actual features (what the system *does*).
Example (Amazon-like e-commerce): register → login → browse/search/filter products → add to cart → apply coupon → place order → pay → track order.

**Non-Functional Requirements** = how well the system performs those features. Generic categories with example targets:

| NFR | Meaning | Example target |
|---|---|---|
| Scalability | Handle growing load (vertical/horizontal) | Handle 10x traffic on sale days |
| Availability | System uptime, governed by SLA/SLO | 99.9% uptime/year |
| Reliability | No data loss | Zero transaction loss |
| Performance | Latency under load (P90/P95/P99) | Response < 200 ms |
| Security | AuthN/AuthZ + encryption, modular code | Encrypted data at rest/in transit |
| Observability | Logging/monitoring after deployment | Full request tracing |

> SLA (Service Level Agreement) / SLO (Service Level Objective) — contractual promises about uptime (e.g., cloud provider guarantees max downtime; breaching it can be legally actionable).

---

## 5. DNS

**Problem DNS solves:** browsers/machines communicate via **IP addresses**, not domain names — and there are 350M+ domains, far too many to store in any single browser or server (and IPs change over time). So we need a distributed lookup system.

### Terminology
- **Domain**: `telescope.com`
- **Subdomain**: `docs.telescope.com`, `course.telescope.com` (domain acts as umbrella)
- **TLD (Top-Level Domain)**: `.com`, `.net`, `.gov`, `.in`, `.edu`, etc.
- **Zone**: a domain + all its subdomains, managed together by an authoritative name server.

### Resolution flow

```
Browser/OS
   |
   v
ISP --> DNS Resolver  (checks its own cache first)
   |
   v
1) Root Server (13 logical root servers, A–M, run by ~12 orgs)
   --> doesn't know telescope.com, but knows which server handles ".com" TLDs
   |
   v
2) TLD Server (handles all ".com")
   --> returns IP of the Authoritative Name Server for telescope.com
   |
   v
3) Authoritative Name Server (configured via GoDaddy/Hostinger/etc.)
   --> returns the actual IP of telescope.com
   |
   v
Browser makes the HTTP request to that IP
```

This 3-hop lookup happens only on a cache miss. Caching exists at **3 levels**: DNS resolver cache, OS cache, browser cache — so repeat visits skip the full chain.

---

## 6. APIs

**API = Application Programming Interface** — a contract that lets one application use another's data/functionality without direct DB access (avoids security risk + duplicated effort). Two main use cases:
1. App A exposes endpoints so App B can reuse its data/logic (e.g., Swiggy/Uber using Google Maps APIs).
2. Within one app, backend exposes APIs that its own frontend (web/mobile) consumes.

### The 5 major API types

| Type | Format | Notes |
|---|---|---|
| **REST** | JSON | Most widely used; simple, low overhead, easy to scale |
| **SOAP** | XML | Legacy systems; XML is verbose vs JSON, still seen in old enterprise systems |
| **GraphQL** | Query language (like SQL) | Single endpoint; client specifies exactly what data it needs |
| **gRPC** | Protocol Buffers | Built by Google; very compact/fast — common for internal microservice-to-microservice calls (low latency) |
| **WebSocket** | Persistent bi-directional channel | Used for chat, live notifications, real-time quizzes — server can push data without being asked (unlike REST) |

---

## 7. REST API Deep Dive

### JSON basics
Key-value pairs; allowed value types: **string, number, boolean, object (nested), array, null**.

### Endpoint anatomy: `METHOD + PATH`
```
GET    /users            -> list users
GET    /users/1          -> get user with id=1
POST   /users            -> create user (body = new user JSON)
PUT    /users/1          -> replace ENTIRE user record (missing fields -> null/default!)
PATCH  /users/1          -> partially update specific fields only
DELETE /users/1          -> delete user with id=1
```
**PUT vs PATCH — important interview point:** PUT replaces the whole resource (anything you omit gets wiped), PATCH updates only what you send. Default to PATCH for partial updates.

### Nested resources vs filtering
- **Nesting** — used when there's a clear, direct relationship: `/blogs/{id}/comments`, `/users/{id}/comments`.
- **Filtering / query params** — used for complex/optional relationships, search, sort, pagination: `/blogs?sort=desc`, `/blogs?q=java`.
- Don't pass sensitive data via path or query params (they're exposed in logs/URLs) — use the **request body** instead (e.g., login credentials).

### Passing data — 3 channels
| Channel | Use for |
|---|---|
| Path | Unique identifiers (id, slug) |
| Query params | Filtering, sorting, search, pagination |
| Body | Sensitive data, full payloads (create/update) |

### HTTP Status Codes (must-know set)

| Code | Meaning | When |
|---|---|---|
| 200 | OK | Successful GET/general success |
| 201 | Created | New entity created (POST) |
| 204 | No Content | Success but nothing to return (e.g., DELETE) |
| 301 | Permanent Redirect | |
| 302 | Temporary Redirect | |
| 400 | Bad Request | Validation/body issue |
| 401 | Unauthorized | Not authenticated |
| 403 | Forbidden | Authenticated but not allowed (e.g., no course access) |
| 404 | Not Found | Resource/route doesn't exist |
| 500 | Internal Server Error | Backend bug/crash — don't leak details to client, log it instead |

### Response body convention
**Always wrap responses in an object, never return a bare array.**
```json
// ❌ Not recommended
[ {"id":1,"name":"A"}, {"id":2,"name":"B"} ]

// ✅ Recommended — easy to extend later (pagination, counts, etc.)
{
  "users": [ {"id":1,"name":"A"}, {"id":2,"name":"B"} ],
  "count": 2
}
```
Reason: if you need to add metadata later (total count, pagination cursor), you don't have to break the existing contract.

---

## 8. SQL Databases

Relational DB = **tables (entities)** with **rows (records)** and **columns (attributes)**. Table names are conventionally **plural** (`users`, `posts`, `comments`).

### Constraints

| Constraint | Purpose | Example |
|---|---|---|
| `UNIQUE` | No duplicate values in column | `username` |
| `NOT NULL` | Field is mandatory | `first_name` |
| `PRIMARY KEY` | Unique row identifier, used for joins | `id` |
| `CHECK` | Custom validation rule | password length ≥ 8, phone = numeric only |
| `FOREIGN KEY` | References another table's PK — builds relationships | `posts.author_id -> authors.id` |
| `DEFAULT` | Fallback value when none provided | `role = 'student'` by default |

### Joins / Relationships

```
One-to-Many:   users(1) ----> posts(many)        -- one author writes many posts
Many-to-One:   posts(many) ----> users(1)         -- same relationship, other direction
Many-to-Many:  students <----> courses            -- needs a JUNCTION table
                 students_courses(student_id, course_id)
One-to-One:    content(id, type) ----> videos / audios / blogs (separate tables, single shared id)
```

**Many-to-many** always needs a **junction/bridge table** holding pairs of foreign keys (e.g., `student_id`, `course_id`).

**One-to-one** is used when different content types (video/audio/text) need very different storage/processing — keep a lean `content` table (searchable metadata) with an FK into the type-specific table, instead of one bloated table with lots of unused columns per row.

### Why SQL doesn't scale infinitely
Relational integrity means relationships must be maintained correctly even as you scale horizontally — across multiple servers this becomes a heavy coordination problem. That's the main motivation to consider NoSQL.

---

## 9. NoSQL Databases

"NoSQL" isn't one technology — it's a category, like saying "not Java" covers many languages. Picked when:
- You need easier **horizontal scaling**.
- Your data is **schemaless** (different records, different shape).
- You want to **avoid heavy entity relationships**.

### SQL vs NoSQL — when to pick which

| Pick SQL when... | Pick NoSQL when... |
|---|---|
| Strong consistency required (payments, transactions) | Scalability/speed is the priority |
| Schema is stable | Schema is unstructured/evolving |
| Relationships matter a lot | Document stands on its own — minimal joins |

> Rule of thumb: **Consistency > Availability → SQL. Availability/Scalability > strict Consistency → NoSQL.**

### Types of NoSQL

| Type | Structure | Used by | Good for |
|---|---|---|---|
| **Key-Value** | unique key → any value (string/JSON/blob/array) | Redis-style caches, cookies | Cache, sessions |
| **Columnar** | reads/writes column-wise instead of row-wise | Google BigQuery, Amazon Redshift, Databricks | Analytics/aggregation (e.g., avg of one column across millions of rows) — slower to write, very fast to read selectively |
| **Graph** | nodes (entities) + edges (relationships), both can have properties | Neo4j-style (Gremlin/SPARQL/Cypher) | Pattern detection, recommendation engines, fraud detection |
| **Document** | JSON-like documents, schemaless, no relationships needed | MongoDB, CouchDB | Logging, flexible profiles, mixed content types |

Real-world examples mentioned: Netflix → Cassandra, Amazon → DynamoDB, Meta → HBase, Uber → MongoDB, Twitter/X → Redis (caching/timelines).

---

## 10. Caching

**Why:** hitting the DB for every request (even static homepage data) multiplies fast — e.g., 3 courses × 4 underlying table/CDN hits = ~24 requests per homepage view × thousands of daily users = huge unnecessary load and latency. Cache sits between app and DB to serve "hot" data fast.

### Key terms
- **Cache hit** — data found in cache (fast).
- **Cache miss** — data not in cache, must fetch from DB (slow), then usually populate cache.
- **TTL (Time To Live)** — how long an entry stays before expiring.
- Cache is intentionally **smaller** than the DB (otherwise searching it would be as slow as the DB itself) and must be **refreshed** as "hot" data changes (e.g., trending course changes from Java to AI).

### Caching strategies

| Strategy | Reads | Writes | Notes / Use case |
|---|---|---|---|
| **Read-Through (RTC)** | via cache (cache pulls from DB on miss) | direct to DB (cache untouched) | Cache stays full of *only* what users actually request |
| **Write-Through (WTC)** | — | write to cache first, then DB | Cache always has the freshest data — good for stock prices |
| **Write-Around (WAC)** | via cache (miss → cache fetches from DB) | direct to DB, bypassing cache | Cache only gets "interesting" data on read, not at write time — used by Twitter/X (don't cache every tweet at creation, only when read) |
| **Write-Back (WBC)** | via cache | write to cache first (fast ack), DB updated **async** later | Trades consistency for speed — used in high-write apps like Swiggy/Zomato order-status updates |

```
RTC:  Client -> Backend -> Cache --(miss)--> DB
                                 <--(value)--
WTC:  Client -> Backend -> Cache -> DB   (write goes through cache)
WAC:  Write: Client -> Backend -> DB (direct)
      Read:  Client -> Backend -> Cache --(miss)--> DB --> updates Cache
WBC:  Write: Client -> Backend -> Cache (ack immediately) --async--> DB
```

### Cache eviction policies

| Policy | Removes | Example |
|---|---|---|
| **LRU** (Least Recently Used) | Data not accessed in a while | Old phone model search drops off once new model launches |
| **MRU** (Most Recently Used) | Data just used, unlikely to be needed again soon | Already-applied coupon code; already-streamed video portion |
| **LFU** (Least Frequently Used) | Data accessed rarely overall | One-time search vs frequently revisited categories |
| **FIFO** | Oldest-inserted entry | Simple capacity-based eviction |
| **LIFO** | Most recently inserted entry | Stack-like eviction (less common, fewer standard use cases) |

---

## 11. Load Balancers

A load balancer sits between clients and a pool of servers. Two jobs:
1. **Pick which server** handles the next request (via an algorithm).
2. **Health-check** servers so it never routes to a dead/unhealthy one.

### Routing algorithms

| Algorithm | How it picks | Weakness |
|---|---|---|
| **Round Robin** | Cycles servers in order: S1, S2, S3, S1... | Ignores server capacity — unfair if servers differ in specs |
| **Weighted Round Robin** | Same as RR but higher-spec servers get proportionally more requests | Needs accurate weight config |
| **Geo-based** | Routes by user's location to nearest server | More state to manage (region DBs, failover between regional servers); VPNs can mislead it |
| **Least Connections** | Sends to the server with fewest active connections | Doesn't account for request "weight" (heavy vs light calls); load balancer must track live connection counts |
| **Least Time / Least Response Time** | Sends to server with lowest average response time | Expensive to calculate continuously; sensitive to spikes |
| **IP Hash** | Hashes client IP → maps to a server range | Hard to scale/rebalance when servers are added/removed; mostly used in legacy/monolithic apps — modern systems prefer JWT-based session handling instead of sticky IP routing |

> In practice, production load balancers use **hybrid combinations** of these algorithms, not just one.

### Health checks

| Mode | How |
|---|---|
| **Passive** | Observe real traffic responses, don't add load |
| **Active** | Periodically send synthetic requests to probe health |

Key health-check parameters:
- **Interval** — how often to check (e.g., every 5s)
- **Timeout** — how long to wait before flagging slow response
- **Threshold** — N consecutive failures → mark unhealthy; N consecutive successes → mark healthy again

---

## 12. Replication

**Why replicate:** avoid single point of failure, increase availability, place data closer to users (lower latency), increase read throughput.

### Single-Leader Replication
All **writes** go to one **leader**; leader pushes changes to **follower** nodes.

```
Client -- write --> Leader --> Follower 1
                            --> Follower 2
```

- **Sync replication**: leader waits for ALL followers to confirm before acking the client.
  - ✅ Followers always consistent; easy leader failover.
  - ❌ Slow; one slow follower blocks everything → impractical at scale.
- **Async replication**: leader acks client immediately, replicates to followers in background.
  - ✅ Fast, non-blocking.
  - ❌ Followers can serve **stale data** temporarily (consistency trade-off).
  - **This is the practical default in real systems.**

**Adding a new follower:** take a full snapshot of an existing follower/leader (full state dump), then apply incremental "diff" snapshots for anything that changed during the copy, then connect it live once caught up. (`FS image` = full metadata snapshot, `edit log` = incremental changes — same idea HDFS NameNode uses.)

**Leader failover:** pick the follower with the most recent updates (by timestamp) as new leader; repoint remaining followers to it.

### Multi-Leader Replication
Multiple data centers, each with its own leader + followers; leaders sync with each other.

```
[DC1: Leader1 -> Followers]  <--sync-->  [DC2: Leader2 -> Followers]
```
- ✅ Tolerant to a single leader's failure; supports concurrent/collaborative writes (like simultaneous doc editing).
- ❌ **Write conflicts** possible when the same data is changed in two DCs simultaneously.

**Conflict resolution strategies:**
1. **Last Write Wins (LWW)** — timestamp decides.
2. **Replica/Leader priority** — higher-ID leader's write always wins regardless of timing.
3. **Manual resolution** — like a Git merge conflict; surface both versions and let the user/app decide.

### Leaderless Replication
No leader — every write/read goes to **multiple nodes directly**. Used by Dynamo, Riak, Cassandra.

**Quorum** — for `N` total nodes:
- Read quorum: must hear from **> N/2** nodes.
- Write quorum: must get confirmation from **> N/2** nodes.
- This avoids waiting for *every* node while still ensuring majority agreement before declaring success.

---

## 13. Partitioning (Sharding)

**Why partition (vs. just replicate):** a single node can't hold infinite data, and search/index performance degrades as one node's dataset grows huge.

**Two hard rules of partitioning:**
1. Combining all partitions must reconstruct 100% of the data (no loss).
2. Data should be **evenly distributed** across partitions (avoid overload on one node).

### Partitioning strategies

| Strategy | How | Risk |
|---|---|---|
| **By Key (range)** | e.g., user IDs 1–50,000 → Partition 1, 50,001–100,000 → Partition 2 | Can create **hotspots** if one range gets disproportionate traffic (e.g., all US users land in one partition) |
| **By Hash of Key** | Hash the key, map hash ranges to partitions | Still vulnerable to hotspots if hash distribution skews |
| **Secondary Index per partition** | Each partition keeps its own index (e.g., by color) — used by Cassandra, Elasticsearch | Query still has to be broadcast to *every* partition (even ones with no matches) — wasteful |
| **Global Secondary Index** | One centralized index across all partitions, tells you exactly which partition(s) hold the match | Reads get fast & targeted; **writes get slower** (must update the global index too) |

**Hotspot** = a partition receiving disproportionately more traffic than others — a real operational risk; must be monitored and re-balanced.

---

## 14. CAP Theorem

In a distributed/partitioned system, you can only fully guarantee **2 of 3**:

```
        Consistency (C)
           /\
          /  \
         /    \
        /  CAP \
       /--------\
Availability(A)  Partition Tolerance(P)
```

| Term | Meaning |
|---|---|
| **C — Consistency** | Every read gets the latest write (no stale data) |
| **A — Availability** | Every request gets *a* response (even if not the latest data) |
| **P — Partition Tolerance** | System keeps working even if network between nodes breaks |

| Combo | What you get | Practical meaning |
|---|---|---|
| **CA** | Consistent + Available | Only possible with a single node (no real partitioning) — not realistic at scale |
| **CP** | Consistent + Partition-tolerant | Sacrifice availability — node with stale data **refuses to answer** until synced. Used for **banking/financial transactions**. |
| **AP** | Available + Partition-tolerant | Sacrifice consistency — node serves whatever it has (possibly stale). Used for **social media feeds** (Instagram-like) where a 1–2s staleness is acceptable. |

> Since real distributed systems always have partitioning (P is mandatory at scale), the real-world choice is always **C vs A** — i.e., CP vs AP.

---

## 15. Message Queues

### Sync vs Async — the core distinction
- **Sync**: caller waits for the result (needed for things with hard dependencies, e.g., process payment before confirming order).
- **Async**: fire-and-forget; caller moves on immediately (e.g., sending confirmation email/SMS after an order — a 1-minute delay doesn't matter).

```
Order Service --(sync)--> Inventory Update      [must happen immediately]
Order Service --(async, via Queue)--> Email/SMS Service   [can lag, can retry]
```

### Why use a Queue instead of calling the service directly
- Producer doesn't block waiting on a possibly-slow/down consumer.
- Queue absorbs failures, handles retries, and buffers bursts of load.
- Decouples producer and consumer completely.

**Producer (Publisher)** → puts messages in queue. **Consumer (Subscriber)** → takes messages out.

### Queue ordering models
| Model | Behavior |
|---|---|
| **Strict FIFO** | If one message fails, the whole queue blocks — **not recommended** |
| **Unordered Queue** | Skip the failed one, keep processing others — standard practice |
| **Priority Queue** | Messages tagged with priority; high-priority processed first regardless of arrival order |

### Delivery models
| Model | Who initiates |
|---|---|
| **Pull** | Consumer asks the queue for work when it's free |
| **Push** | Queue pushes work to the consumer as soon as it arrives |

### Pub/Sub model
Publishers → Queue/Broker → multiple Subscribers. The broker tracks subscriber health and routes only to alive/healthy ones.

### Failure handling concepts
- **DLQ (Dead Letter Queue)** — failed messages get moved here instead of blocking the main queue; used for retry/analysis/reporting later.
- **Poison message** — a message that repeatedly fails processing (bad/invalid data) and just wastes consumer resources if retried blindly.
- **Duplicate handling** — once a message is successfully processed, it must be removed/marked so it's never processed twice (idempotency concern).

### When to use a Message Queue
✅ Async workflows, decoupling, analytics/logging pipelines, deferred/batch jobs (e.g., nightly reports), can double as a load-distribution mechanism.
❌ Real-time/low-latency requirements, anything needing immediate acknowledgment, or low-traffic scenarios (queues add operational cost — don't pay for what you don't need).

---

## 16. Fault Tolerance

Three categories of faults:

| Category | Nature | Examples | Mitigation |
|---|---|---|---|
| **Hardware** | Random, often abrupt | Disk full, server crash, network cable damage, power issue, overheating | Redundancy — multiple servers/DBs so one failure doesn't take down the system |
| **Software** | Deterministic (reproducible), not random | Bad exception handling, missed edge cases, config mismatches between dev/prod, merge conflicts, performance regressions | Good testing, careful config management, code review |
| **Human** | Unpredictable | Mistakes by developers/ops | Discipline, no shortcut "band-aid" fixes, proper review process — but humans remain essential (accountability for AI-generated code too) |

---

## 17. Monitoring & Observability

Two areas to watch: **API behavior** and **machine/hardware health**.

### API Monitoring
| Metric | Meaning |
|---|---|
| **Throughput** | Requests handled per second — alert before hitting capacity (e.g., alert at 8-9k/10k limit) |
| **Error codes** | Track volume of 400s/500s/3xxs; alert on spikes; log for RCA |
| **Health checks** | Track 200-response rate via active/passive checks |
| **Latency (Percentiles)** | Don't trust raw **average** — it's skewed by outliers. Use **Pxx** instead. |

**Percentiles explained:** `P90 = 12s` means 90% of requests complete in ≤12s. If `P50 = 4s` but `P90 = 12s`, that's a red flag — something is making the slowest 40% of requests much worse, worth investigating/optimizing.

### Machine Monitoring
| Metric | Watch for |
|---|---|
| CPU usage | Alert above a threshold (e.g., 75%) |
| Memory usage | Alert near capacity (e.g., 90%) |
| Disk I/O | Bottlenecks in read/write |
| Network I/O | Bandwidth saturation |

---

## 18. Case Study: Video Streaming System

**Scope for this example:** only "how does a video get from source to client" — ignore login, ratings, etc. (Always clarify scope with the interviewer first.)

### Key concepts

- **FPS (Frames Per Second)**: 30fps → video split into 30 images/sec; 60fps is smoother but bigger in size.
- **Why not download the whole video first?** A 1-hour 30fps video can be ~2GB+. Downloading it all before playback = high latency + wasted bandwidth if the user stops watching early.
- **Streaming protocols (over TCP, since order matters)**: **RTMP** (Real-Time Messaging Protocol) and **RTSP** (Real-Time Streaming Protocol) — split video into **segments**, client **pulls** segments incrementally instead of downloading everything.
- **Resolution** affects segment size a lot: 4K >> 1080p > 720p > 480p > 240p > 144p. Different client screens (TV, laptop, mobile, watch) don't all need 4K.

### Adaptive Bitrate Streaming (ABR)
Source video is pre-encoded into **multiple resolutions × multiple segments**. Client switches resolution dynamically based on current network throughput:

```
Network 300 Mbps -> stream 4K segments
Network drops to 10 Mbps -> switch down to 480p segments
Network recovers to 150 Mbps -> switch up to 1080p
```
Rule: if `network throughput > current segment bitrate` → upgrade quality. If `network throughput < current segment bitrate` → downgrade quality. This is exactly the "Auto" quality option you see on YouTube/Netflix players.

### Architecture

```
[Upload Server] --> [CDN: raw source video]
                          |
                          v
                 [Transformation Service]  (splits into segments)
                          |
                          v
                  [Priority Queue]  (why priority? e.g., trending/live content first)
                          |
                          v
        [Worker: 1080p] [Worker: 720p] [Worker: 480p] ... (parallel transcoding)
                          |
                          v
                  [Priority Queue]  (collect transcoded segments)
                          |
                          v
            [Distributed CDN]  (regional edge servers — India, US, etc.)
                          |
                          v
                    [Client device]  (pulls segments, ABR logic picks quality)
```

### Back-of-envelope math (worked example from the video)
Source: 20-minute, 4K@60fps video = **50 GB**.

- Segments: 20 min × 60 = 1200 seconds → split into **1200 segments** (1 segment ≈ 1 second of video).
- Per-segment size by resolution (50GB / 1200, scaled down per resolution):

| Resolution | Approx. total size | Per-segment size |
|---|---|---|
| 4K | 50 GB | ~41.6 MB |
| 1080p | 20 GB | ~16.6 MB |
| 720p | ~10 GB | ~8.3 MB |
| 480p | ~5 GB | ~4.17 MB |
| 240p | ~2.5 GB | ~2.08 MB |
| **Total (all resolutions combined)** | | **~72.65 MB per segment-second** |

- For 100 concurrent users: ~87.5 GB total storage need → fine on a single server.
- For 100,000 users: same single server is no longer enough → need more servers, caching at CDN/browser level, and the full pipeline above (queues + workers + distributed CDN).

**Takeaway:** the exact diagram will vary person to person — what interviewers care about is the **reasoning and trade-offs**, not a single "correct" diagram. Always size your design with rough math (segment size × resolution count × concurrent users) to justify *why* you need each component.

---

## 19. Quick Revision Table

| Topic | One-line recall |
|---|---|
| Vertical vs Horizontal scaling | Upgrade one server vs add more servers |
| Data vs Compute intensive | Bottleneck in data movement vs in calculation |
| DNS resolution order | Root → TLD → Authoritative Name Server |
| REST PUT vs PATCH | PUT replaces whole resource, PATCH updates partial fields |
| SQL vs NoSQL | Strong relationships/consistency vs flexible schema/scale |
| Cache strategies | RTC=read-only, WTC=write-first, WAC=cache-on-read-only, WBC=async write |
| Cache eviction | LRU/MRU/LFU/FIFO/LIFO — pick based on access pattern |
| LB algorithms | Round Robin, Weighted RR, Geo, Least Connections, Least Time, IP Hash |
| Replication | Single-leader (simple, async preferred), Multi-leader (conflict risk), Leaderless (quorum-based) |
| Partitioning | By key/hash (hotspot risk), secondary index (per-partition vs global) |
| CAP theorem | Pick 2 of Consistency/Availability/Partition-tolerance — in practice it's C vs A |
| Message Queue | Async decoupling; FIFO/Priority; Pub/Sub; DLQ for failures |
| Fault types | Hardware (random) / Software (deterministic) / Human (unpredictable) |
| Monitoring | Use percentiles (P90/P99), not averages, for latency |
| Streaming case study | Segment + ABR (adaptive bitrate) based on network throughput |

---

*Timestamps reference (for re-watching specific sections):*
```
00:00:00 Introduction              02:08:34 NoSQL Databases
00:03:24 Hello System Design       02:27:31 Cache
00:05:33 Alien Bank Example        02:53:14 Load Balancer
00:19:34 Components of System      03:23:25 Replication
00:35:22 Types of Applications     03:44:26 Partition
00:48:18 Functional/Non-Functional 03:57:20 CAP Theorem
00:56:17 DNS                       04:03:40 Message Queue
01:08:25 APIs                      04:27:32 Fault/Error
01:21:04 RESTful Request/Response  04:34:39 Monitoring & Observability
01:47:25 Database (SQL)            04:42:50 Streaming System Case Study
```
