# System Design Handbook

## Part 10.1 -- System Design Case Study: Video Streaming Application

------------------------------------------------------------------------

# Table of Contents

1.  Problem Statement
2.  Functional Requirements
3.  Non-Functional Requirements
4.  High-Level Architecture
5.  Upload Flow
6.  Video Processing Pipeline
7.  Video Playback Flow
8.  Storage Design
9.  Database Design
10. Cache & CDN
11. Message Queue
12. Scalability & Fault Tolerance
13. Monitoring
14. Trade-offs
15. Interview Tips
16. Cheat Sheet

------------------------------------------------------------------------

# 1. Problem Statement

Design a scalable video streaming platform similar to YouTube or
Netflix.

Users should be able to:

-   Upload videos
-   Watch videos
-   Like videos
-   Comment
-   Search videos

------------------------------------------------------------------------

# 2. Functional Requirements

-   User authentication
-   Upload videos
-   Stream videos
-   Search videos
-   Like & comment
-   Recommendations

------------------------------------------------------------------------

# 3. Non-Functional Requirements

-   Millions of concurrent users
-   High availability
-   Low latency playback
-   Fault tolerance
-   Horizontal scalability
-   Secure video delivery

------------------------------------------------------------------------

# 4. High-Level Architecture

                  Users
                    │
                    ▼
              DNS / CDN
                    │
                    ▼
             Load Balancer
                    │
                    ▼
              API Gateway
                    │
     ┌──────────────┼────────────────┐
     ▼              ▼                ▼
    Auth Service  Video Service   Search Service
                    │
                    ▼
              Message Queue
                    │
          ┌─────────┴─────────┐
          ▼                   ▼
     Video Transcoder   Thumbnail Service
          │                   │
          └─────────┬─────────┘
                    ▼
             Object Storage
                    │
                    ▼
                  CDN
                    │
                    ▼
                 Viewers

------------------------------------------------------------------------

# 5. Upload Flow

1.  User uploads video.
2.  API Gateway authenticates request.
3.  Video metadata stored in database.
4.  Raw video stored in object storage.
5.  Upload event sent to message queue.
6.  Transcoding starts asynchronously.

------------------------------------------------------------------------

# 6. Video Processing Pipeline

    Upload

    ↓

    Kafka / RabbitMQ

    ↓

    Transcoding Workers

    ↓

    1080p
    720p
    480p

    ↓

    Thumbnail Generation

    ↓

    CDN

Multiple resolutions support adaptive streaming.

------------------------------------------------------------------------

# 7. Video Playback Flow

    User

    ↓

    DNS

    ↓

    CDN

    (Cache Hit?)

    ↓

    Origin Storage

    ↓

    Video Segments

    ↓

    Player

Popular videos are served directly from the CDN.

------------------------------------------------------------------------

# 8. Storage Design

Object Storage

-   Videos
-   Thumbnails

SQL Database

-   Users
-   Comments
-   Likes
-   Metadata

NoSQL

-   Recommendations
-   Watch history

Redis

-   Trending videos
-   Sessions
-   Frequently viewed metadata

------------------------------------------------------------------------

# 9. Database Design

SQL Tables

-   Users
-   Videos
-   Comments
-   Likes
-   Playlists

Indexes

-   video_id
-   uploader_id
-   upload_date

------------------------------------------------------------------------

# 10. Cache & CDN

Redis Cache

-   Video metadata
-   Trending videos

CDN

-   Video chunks
-   Images
-   Static assets

Benefits

-   Reduced latency
-   Reduced origin load

------------------------------------------------------------------------

# 11. Message Queue

Used for:

-   Video transcoding
-   Thumbnail generation
-   Notifications
-   Analytics

Services communicate asynchronously.

------------------------------------------------------------------------

# 12. Scalability & Fault Tolerance

-   Multiple application servers
-   Load balancers
-   Read replicas
-   Database sharding
-   Auto scaling
-   Health checks
-   Circuit breakers
-   Retries
-   Multi-region deployment

------------------------------------------------------------------------

# 13. Monitoring

Monitor:

-   Upload success rate
-   Playback latency
-   CDN hit ratio
-   Error rate
-   Queue length
-   CPU & Memory
-   Storage utilization

Tools

-   Prometheus
-   Grafana
-   ELK
-   Jaeger

------------------------------------------------------------------------

# 14. Trade-offs

  Decision      Benefit         Cost
  ------------- --------------- ------------------------
  CDN           Fast playback   Higher cost
  Redis         Low latency     Cache consistency
  Sharding      Scalability     Operational complexity
  Replication   Availability    Replication lag

------------------------------------------------------------------------

# 15. Interview Tips

When asked to design a streaming service:

1.  Clarify requirements.
2.  Estimate scale.
3.  Draw high-level architecture.
4.  Explain data flow.
5.  Discuss storage.
6.  Add cache.
7.  Add load balancer.
8.  Add message queue.
9.  Explain scaling.
10. Explain trade-offs.

------------------------------------------------------------------------

# 16. Cheat Sheet

    Users
      │
      ▼
    CDN
      │
      ▼
    Load Balancer
      │
      ▼
    API Gateway
      │
      ▼
    Video Service
      │
      ▼
    Message Queue
      │
      ▼
    Transcoding
      │
      ▼
    Object Storage
      │
      ▼
    CDN
      │
      ▼
    Viewer

    Components Used:
    ✔ DNS
    ✔ CDN
    ✔ Load Balancer
    ✔ Cache
    ✔ SQL
    ✔ NoSQL
    ✔ Redis
    ✔ Kafka/RabbitMQ
    ✔ Replication
    ✔ Sharding
    ✔ Monitoring
    ✔ Fault Tolerance

------------------------------------------------------------------------

# Congratulations!

You have completed the core System Design handbook based on the course
outline. Continue practicing by designing systems such as URL Shortener,
Chat Application, Ride Sharing, Food Delivery, and E-commerce to
reinforce these concepts.
