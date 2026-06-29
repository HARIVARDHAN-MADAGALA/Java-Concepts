# System Design Handbook

## Part 1.3 -- Data-Intensive vs Compute-Intensive Applications

------------------------------------------------------------------------

# Table of Contents

1.  Why This Classification Matters
2.  Data-Intensive Applications
3.  Compute-Intensive Applications
4.  Comparison
5.  Instagram Example
6.  Machine Learning Example
7.  Interview Thinking
8.  Cheat Sheet

------------------------------------------------------------------------

# 1. Why This Classification Matters

One of the biggest mistakes beginners make is trying to solve every
performance problem the same way.

Before optimizing, ask:

> **Where is the bottleneck?**

Is the application spending most of its time:

-   Moving data?
-   Or performing calculations?

This determines the architecture.

------------------------------------------------------------------------

# 2. Data-Intensive Applications

A data-intensive application spends most of its time reading, writing
and moving data.

Examples:

-   Instagram
-   WhatsApp
-   Facebook
-   Banking
-   Amazon Orders

Typical flow

    User
      ↓
    Server
      ↓
    Database

The challenge is not complex calculations.

The challenge is serving huge amounts of data quickly.

## Common Problems

-   Slow database queries
-   High network traffic
-   Database overload
-   Storage growth

## Common Solutions

-   Cache (Redis)
-   Database Indexing
-   Replication
-   Sharding
-   CDN
-   Better SQL queries

------------------------------------------------------------------------

# 3. Compute-Intensive Applications

These applications perform heavy computation on relatively small amounts
of data.

Examples

-   AI Model Training
-   Video Rendering
-   Image Processing
-   Scientific Simulations
-   Cryptography

Typical flow

    Input

    ↓

    CPU / GPU

    ↓

    Output

The bottleneck is computation.

## Common Solutions

-   Faster CPU
-   GPU
-   Parallel Processing
-   Better Algorithms
-   Multithreading

------------------------------------------------------------------------

# 4. Comparison

  Data Intensive               Compute Intensive
  ---------------------------- --------------------------
  Data movement is expensive   Computation is expensive
  Database is critical         CPU/GPU is critical
  Redis helps                  GPU helps
  Indexes matter               Algorithms matter
  Scaling storage              Scaling compute

------------------------------------------------------------------------

# 5. Instagram Example

Instagram mainly stores and retrieves:

-   Images
-   Videos
-   Likes
-   Comments
-   Followers

Problems

-   Millions of reads
-   Millions of writes
-   Huge storage

Optimizations

-   Cache
-   CDN
-   Replication
-   Sharding

Notice:

Adding a stronger CPU alone will not solve Instagram's scaling problems.

------------------------------------------------------------------------

# 6. Machine Learning Example

Suppose an AI model predicts whether an image contains a cat.

Input data is small.

The expensive part is:

-   Matrix multiplication
-   Neural network inference
-   GPU computation

Optimizations

-   Better GPU
-   Tensor optimization
-   Parallel execution

Database optimization has little impact here.

------------------------------------------------------------------------

# 7. Interview Thinking

Question:

"My application is slow."

Wrong answer:

> Add Redis.

Correct answer:

Ask:

1.  Where is time being spent?
2.  Database?
3.  Network?
4.  CPU?
5.  GPU?

Only then choose a solution.

------------------------------------------------------------------------

# Quick Rule

If time is lost moving data:

    Data Intensive

If time is lost performing calculations:

    Compute Intensive

------------------------------------------------------------------------

# Spring Boot Mapping

Data-intensive examples:

-   E-commerce
-   Banking
-   Social Media
-   CRM

Compute-intensive examples:

-   AI services
-   Image compression
-   Video transcoding

------------------------------------------------------------------------

# Interview Questions

### Why is Instagram data-intensive?

Because it primarily stores, retrieves and serves enormous amounts of
data.

### Why is AI model training compute-intensive?

Because most execution time is spent performing mathematical
computations.

### Can one application be both?

Yes.

Example:

YouTube

-   Serving videos → Data-intensive
-   Recommendation engine → Compute-intensive

------------------------------------------------------------------------

# Cheat Sheet

    Data Problem?

    ↓

    Cache
    Database
    Replication
    Sharding

    --------------------

    Compute Problem?

    ↓

    CPU
    GPU
    Algorithms
    Parallel Processing

------------------------------------------------------------------------

Next Chapter: **Part 1.4 -- Functional vs Non-Functional Requirements**
