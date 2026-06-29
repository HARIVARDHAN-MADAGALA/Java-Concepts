# System Design Handbook

## Part 8.1 -- Fault Tolerance

------------------------------------------------------------------------

# Table of Contents

1.  What is Fault Tolerance?
2.  Types of Failures
3.  Hardware Failures
4.  Software Failures
5.  Human Errors
6.  Redundancy
7.  Failover
8.  Circuit Breaker
9.  Retry Pattern
10. Bulkhead Pattern
11. Disaster Recovery
12. Interview Questions
13. Cheat Sheet

------------------------------------------------------------------------

# 1. What is Fault Tolerance?

Fault Tolerance is the ability of a system to continue operating even
when one or more components fail.

Goal:

    Failure

    ↓

    Application Continues Running

------------------------------------------------------------------------

# 2. Types of Failures

Distributed systems commonly experience:

-   Hardware failures
-   Software failures
-   Network failures
-   Human errors

A good system assumes failures **will happen**.

------------------------------------------------------------------------

# 3. Hardware Failures

Examples

-   Disk failure
-   CPU failure
-   RAM failure
-   Power outage

Solutions

-   Replication
-   Backup servers
-   RAID
-   Cloud redundancy

------------------------------------------------------------------------

# 4. Software Failures

Examples

-   Memory leaks
-   Infinite loops
-   Crashes
-   Bugs
-   Deadlocks

Solutions

-   Monitoring
-   Health checks
-   Auto restart
-   Circuit breakers

------------------------------------------------------------------------

# 5. Human Errors

Examples

-   Wrong deployment
-   Accidental data deletion
-   Incorrect configuration

Solutions

-   CI/CD
-   Rollback
-   Backups
-   Approval workflows

------------------------------------------------------------------------

# 6. Redundancy

Never depend on a single component.

Example

    Load Balancer

    ├── Server 1
    ├── Server 2
    └── Server 3

If one server fails, others continue serving requests.

------------------------------------------------------------------------

# 7. Failover

Automatic switching to a healthy backup.

    Primary ❌

    ↓

    Secondary

    ↓

    New Primary

Failover minimizes downtime.

------------------------------------------------------------------------

# 8. Circuit Breaker

Prevents repeated calls to a failing service.

    Service A

    ↓

    Circuit Breaker

    ↓

    Service B

States

-   Closed
-   Open
-   Half Open

Popular library

-   Resilience4j

------------------------------------------------------------------------

# 9. Retry Pattern

Temporary failures may succeed later.

Strategies

-   Fixed Delay
-   Exponential Backoff
-   Retry Limit

Avoid infinite retries.

------------------------------------------------------------------------

# 10. Bulkhead Pattern

Isolate failures.

Example

    Payments

    ↓

    Dedicated Thread Pool

    Orders

    ↓

    Separate Thread Pool

One failure should not bring down the entire application.

------------------------------------------------------------------------

# 11. Disaster Recovery

Prepare for complete outages.

Techniques

-   Database backups
-   Cross-region replication
-   Infrastructure as Code
-   Recovery plans

Metrics

-   RTO (Recovery Time Objective)
-   RPO (Recovery Point Objective)

------------------------------------------------------------------------

# 12. Interview Questions

### What is Fault Tolerance?

Ability to continue operating despite failures.

### Difference between Retry and Circuit Breaker?

Retry attempts again.

Circuit Breaker stops repeated failing calls.

### Why is redundancy important?

To remove single points of failure.

### What is failover?

Automatic switching to backup resources.

------------------------------------------------------------------------

# 13. Cheat Sheet

    Failures

    ↓

    Hardware
    Software
    Human

    ↓

    Redundancy

    ↓

    Failover

    ↓

    Circuit Breaker

    ↓

    Retry

    ↓

    Bulkhead

    ↓

    Disaster Recovery

------------------------------------------------------------------------

Next Chapter: **Part 9.1 -- Monitoring & Observability**
