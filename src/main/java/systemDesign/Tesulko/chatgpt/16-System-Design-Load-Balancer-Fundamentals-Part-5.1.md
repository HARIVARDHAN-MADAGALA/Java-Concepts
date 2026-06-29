# System Design Handbook

## Part 5.1 -- Load Balancer Fundamentals

------------------------------------------------------------------------

# Table of Contents

1.  Why Load Balancers?
2.  Single Server Problem
3.  What is a Load Balancer?
4.  Request Flow
5.  Layer 4 vs Layer 7
6.  Reverse Proxy
7.  Sticky Sessions
8.  Popular Load Balancers
9.  Spring Boot Deployment
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Load Balancers?

A single server has limits.

Problems:

-   CPU overload
-   Memory exhaustion
-   Network bottlenecks
-   Single point of failure

When traffic grows, one server is not enough.

------------------------------------------------------------------------

# 2. Single Server Problem

    Users
      │
      ▼
    Spring Boot Server
      │
      ▼
    Database

If the server crashes:

    Application Down

------------------------------------------------------------------------

# 3. What is a Load Balancer?

A Load Balancer sits between clients and servers.

    Users
       │
       ▼
    Load Balancer
     ┌──┼──┐
     ▼  ▼  ▼
    S1 S2 S3

Responsibilities

-   Distribute traffic
-   Detect failed servers
-   Improve availability
-   Prevent overload

------------------------------------------------------------------------

# 4. Request Flow

    Client
      │
      ▼
    DNS
      │
      ▼
    Load Balancer
      │
      ├──► Server 1
      ├──► Server 2
      └──► Server 3
              │
              ▼
          Database

------------------------------------------------------------------------

# 5. Layer 4 vs Layer 7

## Layer 4 (Transport)

Works with TCP/UDP.

Decisions based on:

-   IP
-   Port

Examples

-   AWS NLB
-   HAProxy (L4 mode)

------------------------------------------------------------------------

## Layer 7 (Application)

Works with HTTP/HTTPS.

Can inspect:

-   URL
-   Headers
-   Cookies
-   Hostname

Examples

-   Nginx
-   AWS ALB

------------------------------------------------------------------------

# 6. Reverse Proxy

Clients never communicate directly with servers.

    Client
       │
       ▼
    Reverse Proxy / Load Balancer
       │
       ▼
    Application Servers

Benefits

-   Security
-   SSL Termination
-   Load Distribution
-   Caching

------------------------------------------------------------------------

# 7. Sticky Sessions

Normally requests can go to any server.

Sticky sessions send a user's requests to the same server.

Useful when session state is stored locally.

Modern microservices usually prefer shared session storage (Redis)
instead.

------------------------------------------------------------------------

# 8. Popular Load Balancers

-   Nginx
-   HAProxy
-   AWS Application Load Balancer (ALB)
-   AWS Network Load Balancer (NLB)
-   Azure Load Balancer

------------------------------------------------------------------------

# 9. Spring Boot Deployment

    Users
       │
       ▼
    Nginx
     ┌──┼──┐
     ▼  ▼  ▼
    Spring Boot
    Spring Boot
    Spring Boot
          │
          ▼
    MySQL

------------------------------------------------------------------------

# 10. Interview Questions

### Why use a Load Balancer?

To distribute requests and improve availability.

### Difference between L4 and L7?

L4 routes using network information.

L7 routes using HTTP/application information.

### What is a reverse proxy?

A server that receives client requests and forwards them to backend
servers.

### Why avoid sticky sessions?

They reduce flexibility and can create uneven load.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Users
      │
      ▼
    Load Balancer
     ┌──┼──┐
     ▼  ▼  ▼
    S1 S2 S3

    L4 → TCP/UDP
    L7 → HTTP/HTTPS

    Popular:
    Nginx
    HAProxy
    AWS ALB
    AWS NLB

------------------------------------------------------------------------

Next Chapter: **Part 5.2 -- Load Balancing Algorithms**
