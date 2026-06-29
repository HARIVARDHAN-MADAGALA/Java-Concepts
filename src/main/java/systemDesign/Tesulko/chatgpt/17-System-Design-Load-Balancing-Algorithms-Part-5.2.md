# System Design Handbook

## Part 5.2 -- Load Balancing Algorithms

------------------------------------------------------------------------

# Table of Contents

1.  Why Algorithms Matter
2.  Round Robin
3.  Weighted Round Robin
4.  Least Connections
5.  Least Response Time
6.  IP Hash
7.  Geo-Based Routing
8.  Comparison
9.  Real-World Usage
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Algorithms Matter?

A Load Balancer needs a rule to decide **which server should receive the
next request**.

That rule is called a **Load Balancing Algorithm**.

------------------------------------------------------------------------

# 2. Round Robin

Requests are distributed one by one.

    Req1 → S1
    Req2 → S2
    Req3 → S3
    Req4 → S1

### Pros

-   Very simple
-   Even distribution

### Cons

-   Assumes all servers are equally powerful.

------------------------------------------------------------------------

# 3. Weighted Round Robin

Servers receive traffic based on assigned weights.

    S1 Weight = 3
    S2 Weight = 2
    S3 Weight = 1

    Pattern:
    S1 S1 S1 S2 S2 S3

Useful when servers have different CPU/RAM.

------------------------------------------------------------------------

# 4. Least Connections

Send the next request to the server handling the fewest active
connections.

    S1 = 120
    S2 = 40
    S3 = 75

    Next Request → S2

Best for long-lived connections.

------------------------------------------------------------------------

# 5. Least Response Time

Chooses the server responding the fastest.

Factors:

-   Active connections
-   Response latency

Useful when server performance differs.

------------------------------------------------------------------------

# 6. IP Hash

Client IP determines the server.

    192.168.1.10 → S2
    192.168.1.10 → S2

Useful when session affinity is required.

------------------------------------------------------------------------

# 7. Geo-Based Routing

Traffic is routed based on geographic location.

    India Users → Mumbai

    Europe Users → Frankfurt

    US Users → Virginia

Benefits

-   Lower latency
-   Better user experience

------------------------------------------------------------------------

# 8. Comparison

  Algorithm             Best For             Limitation
  --------------------- -------------------- ---------------------
  Round Robin           Equal servers        Ignores server load
  Weighted RR           Different hardware   Static weights
  Least Connections     Long requests        Connection tracking
  Least Response Time   Performance          Monitoring overhead
  IP Hash               Sticky sessions      Uneven distribution
  Geo Routing           Global apps          Geo infrastructure

------------------------------------------------------------------------

# 9. Real-World Usage

-   Nginx: Round Robin (default), Least Connections, IP Hash
-   AWS ALB: Intelligent HTTP routing
-   CDNs: Geo-based routing
-   Gaming: Least latency routing

------------------------------------------------------------------------

# 10. Interview Questions

### Which algorithm is the default in many load balancers?

Round Robin.

### Which algorithm suits unequal server capacities?

Weighted Round Robin.

### Which algorithm is best for WebSocket connections?

Least Connections.

### Which algorithm helps keep a user on the same server?

IP Hash.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Round Robin
    ↓

    Equal Rotation

    ----------------

    Weighted RR
    ↓

    Power-based Rotation

    ----------------

    Least Connections
    ↓

    Fewest Active Users

    ----------------

    Least Response Time
    ↓

    Fastest Server

    ----------------

    IP Hash
    ↓

    Same Client → Same Server

    ----------------

    Geo Routing
    ↓

    Nearest Region

------------------------------------------------------------------------

Next Chapter: **Part 5.3 -- Health Checks**
