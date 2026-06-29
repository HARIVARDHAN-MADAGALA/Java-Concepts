# System Design Handbook

## Part 1.4 -- Functional vs Non-Functional Requirements

------------------------------------------------------------------------

# Table of Contents

1.  Why Requirements Matter
2.  Functional Requirements
3.  Non-Functional Requirements
4.  Amazon Case Study
5.  Scalability
6.  Availability
7.  Reliability
8.  Performance
9.  Security
10. Maintainability
11. Observability
12. SLA, SLO & SLI
13. Interview Questions
14. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Requirements Matter

Before designing any system, ask:

**What should the system do?** and

**How well should it do it?**

These two questions divide requirements into:

-   Functional Requirements
-   Non-Functional Requirements

------------------------------------------------------------------------

# 2. Functional Requirements

Functional requirements describe **what the system should do**.

Example: Amazon

-   User Registration
-   Login
-   Search Products
-   Filter Products
-   Add to Cart
-   Apply Coupons
-   Place Order
-   Make Payment
-   Track Order
-   Cancel Order

If a feature is visible to the user, it is usually a functional
requirement.

------------------------------------------------------------------------

# 3. Non-Functional Requirements

Non-functional requirements describe **how the system should behave**.

Examples:

-   Handle 10 million users
-   Respond within 200 ms
-   99.99% uptime
-   Secure customer data
-   Scale during sales
-   Recover from failures

These define the quality of the system.

------------------------------------------------------------------------

# 4. Amazon Case Study

## Functional

    Register
    Login
    Search
    Buy
    Pay
    Track
    Review

## Non-Functional

    10M+ Users
    <200 ms Response
    99.99% Availability
    Secure Payments
    Scalable on Sale Days

Notice:

Two systems can have exactly the same features but very different
non-functional requirements.

------------------------------------------------------------------------

# 5. Scalability

Definition

Ability of a system to handle increasing traffic without major redesign.

Types

-   Vertical Scaling
-   Horizontal Scaling

Question

Can today's system support 10× users tomorrow?

------------------------------------------------------------------------

# 6. Availability

Definition

Percentage of time a system remains operational.

Examples

    99%

    99.9%

    99.99%

    99.999%

Higher availability usually means higher cost.

------------------------------------------------------------------------

# 7. Reliability

Definition

Ability to consistently produce correct results.

Example

A banking transaction must never lose money.

Reliable systems avoid:

-   Data corruption
-   Duplicate transactions
-   Lost requests

------------------------------------------------------------------------

# 8. Performance

Performance measures how fast the system responds.

Metrics

-   Latency
-   Throughput
-   Response Time

Example

    Login < 200 ms
    Search < 300 ms

------------------------------------------------------------------------

# 9. Security

Security includes

-   Authentication
-   Authorization
-   Encryption
-   Secure APIs
-   Secure Storage

Example

Passwords should be hashed, not stored as plain text.

------------------------------------------------------------------------

# 10. Maintainability

Good systems are easy to:

-   Read
-   Test
-   Extend
-   Debug

Characteristics

-   Modular code
-   Clean architecture
-   Loose coupling

------------------------------------------------------------------------

# 11. Observability

A production system must answer:

-   Is it healthy?
-   Is it slow?
-   Which service failed?
-   Where is the bottleneck?

Observability includes

-   Logs
-   Metrics
-   Traces

Popular Tools

-   Prometheus
-   Grafana
-   ELK

------------------------------------------------------------------------

# 12. SLA, SLO & SLI

## SLA

Legal/business promise.

Example

99.9% uptime.

## SLO

Internal engineering objective.

Example

99.95% uptime target.

## SLI

Actual measured value.

Example

99.92% uptime measured this month.

Relationship

    SLI -> Measurement

    SLO -> Target

    SLA -> Contract

------------------------------------------------------------------------

# 13. Interview Questions

### Difference between Functional and Non-Functional Requirements?

Functional = What the system does.

Non-Functional = How well the system performs.

### Give examples of Non-Functional Requirements.

-   Scalability
-   Availability
-   Reliability
-   Performance
-   Security
-   Maintainability
-   Observability

### Why are Non-Functional Requirements important?

They determine whether a system can survive production traffic.

------------------------------------------------------------------------

# 14. Cheat Sheet

    Functional

    ↓

    Features

    ----------------

    Non-Functional

    ↓

    Quality

    ↓

    Scalability
    Availability
    Reliability
    Performance
    Security
    Maintainability
    Observability

------------------------------------------------------------------------

## Summary

Always gather requirements before discussing databases, caches or load
balancers.

Good System Design starts with understanding the problem, not selecting
technologies.

Next Chapter: **Part 2.1 -- DNS (Domain Name System)**
