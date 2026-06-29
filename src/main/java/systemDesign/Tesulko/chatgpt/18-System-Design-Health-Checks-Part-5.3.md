# System Design Handbook

## Part 5.3 -- Health Checks

------------------------------------------------------------------------

# Table of Contents

1.  Why Health Checks?
2.  What is a Health Check?
3.  Active vs Passive Health Checks
4.  HTTP Health Checks
5.  TCP Health Checks
6.  Liveness vs Readiness
7.  Removing Unhealthy Servers
8.  Recovery
9.  Spring Boot & Kubernetes
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Health Checks?

A Load Balancer should never send requests to a failed server.

Health checks continuously verify whether backend servers are healthy.

------------------------------------------------------------------------

# 2. What is a Health Check?

A periodic check performed by the Load Balancer.

    Load Balancer

    ↓

    Health Check

    ↓

    Server

Healthy?

-   Yes → Receive traffic
-   No → Remove from rotation

------------------------------------------------------------------------

# 3. Active vs Passive Health Checks

## Active

The load balancer periodically sends requests.

    LB

    ↓

    GET /health

    ↓

    Server

Most common approach.

## Passive

No dedicated check.

The load balancer observes:

-   Connection failures
-   Timeouts
-   HTTP 5xx errors

Repeated failures mark the server unhealthy.

------------------------------------------------------------------------

# 4. HTTP Health Checks

A request is sent to a health endpoint.

Example

    GET /health

Expected

    200 OK

Anything else may indicate a problem.

------------------------------------------------------------------------

# 5. TCP Health Checks

Instead of HTTP, only verifies whether a TCP connection can be
established.

Useful for:

-   Databases
-   Redis
-   Message Brokers

------------------------------------------------------------------------

# 6. Liveness vs Readiness

## Liveness Probe

Checks:

"Is the application alive?"

If failed:

Restart the application.

## Readiness Probe

Checks:

"Can this instance receive requests?"

If failed:

Keep running but stop routing traffic.

------------------------------------------------------------------------

# 7. Removing Unhealthy Servers

    Users

    ↓

    Load Balancer

    ├── Server 1 ✅
    ├── Server 2 ❌
    └── Server 3 ✅

Server 2 is removed until it recovers.

This improves availability.

------------------------------------------------------------------------

# 8. Recovery

Health checks continue.

When the server becomes healthy again:

    Healthy

    ↓

    Added Back

    ↓

    Receives Traffic

------------------------------------------------------------------------

# 9. Spring Boot & Kubernetes

Spring Boot

Add Actuator:

    /actuator/health

Typical response

``` json
{
  "status":"UP"
}
```

Kubernetes

-   livenessProbe
-   readinessProbe

Cloud Load Balancers commonly use these endpoints.

------------------------------------------------------------------------

# 10. Interview Questions

### Why are health checks important?

To prevent requests from reaching failed servers.

### Difference between Liveness and Readiness?

Liveness checks whether the app should be restarted.

Readiness checks whether it should receive traffic.

### Which Spring Boot endpoint is commonly used?

`/actuator/health`

------------------------------------------------------------------------

# 11. Cheat Sheet

    Health Check

    ↓

    Healthy?

    ├── Yes → Route Traffic
    └── No  → Remove

    ----------------

    Liveness

    ↓

    Restart App

    ----------------

    Readiness

    ↓

    Stop Traffic

    ----------------

    Spring Boot

    ↓

    /actuator/health

------------------------------------------------------------------------

Next Chapter: **Part 6.1 -- Distributed Databases & Replication**
