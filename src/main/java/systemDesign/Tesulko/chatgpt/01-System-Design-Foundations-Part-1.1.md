# System Design Handbook

## Part 1.1 -- Introduction to System Design & Thinking Like a System Designer

> Based on the uploaded course transcript, expanded with
> interview-oriented explanations.

------------------------------------------------------------------------

# Table of Contents

1.  What is System Design?
2.  Why Do We Need System Design?
3.  Software Engineer vs System Designer
4.  The Goal of System Design
5.  Thinking Like a System Designer
6.  Alien Bank Example
7.  Key Lessons
8.  Vertical vs Horizontal Scaling (Introduction)
9.  Common Mistakes
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. What is System Design?

Most developers think:

> "If my code works, my application is complete."

That is only the beginning.

Imagine you build a Spring Boot API.

    GET /users

It works perfectly with 100 users.

Now imagine **10 million users** calling the same API at the same time.

Will your application still work?

That question is answered by **System Design**.

**Definition**

System Design is the process of designing software systems that remain
scalable, reliable, available and performant even under massive traffic.

------------------------------------------------------------------------

# 2. Why Do We Need System Design?

Small application

    100 Users
        ↓
    Spring Boot
        ↓
    MySQL

Everything works.

Large application

    10 Million Users
          ↓
    Spring Boot
          ↓
    MySQL

Problems appear:

-   Slow responses
-   Database overload
-   Server crashes
-   Timeouts
-   High CPU usage

System Design exists to solve these problems.

------------------------------------------------------------------------

# 3. Software Engineer vs System Designer

Software Engineer asks:

> How do I implement this feature?

System Designer asks:

> How will this feature behave when 10 million users use it
> simultaneously?

------------------------------------------------------------------------

# 4. Goal of System Design

The goal is **not drawing boxes**.

The goal is:

-   Handle more users
-   Handle more requests
-   Handle more data
-   Handle failures
-   Grow without rewriting the application

------------------------------------------------------------------------

# 5. Thinking Like a System Designer

Before selecting technologies, ask:

1.  What problem am I solving?
2.  How many users?
3.  What is more important?
    -   Speed?
    -   Reliability?
    -   Availability?
    -   Cost?
4.  Where will data be stored?
5.  What happens if one server fails?

Only then choose technologies like Redis, Kafka or Kubernetes.

------------------------------------------------------------------------

# 6. Alien Bank Example

Imagine an **Alien Bank**.

Initially:

    Customers
        ↓
    Cashier

Everything works.

## Problem 1

Too many customers.

Solution:

Improve the cashier.

Software equivalent:

-   Better algorithms
-   Better code
-   Better data structures

------------------------------------------------------------------------

## Problem 2

Cashier cannot become infinitely fast.

Solution:

Upgrade the machine.

Software equivalent:

**Vertical Scaling**

    Small Server
          ↓
    Bigger Server

------------------------------------------------------------------------

## Problem 3

Even the biggest server becomes insufficient.

Solution:

Add more servers.

Software equivalent:

**Horizontal Scaling**

    Server 1
    Server 2
    Server 3

------------------------------------------------------------------------

## Problem 4

Multiple counters maintain different data.

Solution:

Shared database.

    Counter 1
          \
        Database
          /
    Counter 2

------------------------------------------------------------------------

## Problem 5

Customers always choose one counter.

Solution:

Load Balancer.

    Customers
         ↓
    Load Balancer
       ↙   ↘
    Server1 Server2

------------------------------------------------------------------------

# 7. Lessons

  Alien Bank        System Design
  ----------------- --------------------
  Faster cashier    Better code
  Better machine    Vertical Scaling
  More counters     Horizontal Scaling
  Shared register   Database
  Manager           Load Balancer

------------------------------------------------------------------------

# 8. Vertical vs Horizontal Scaling

## Vertical Scaling

Increase resources of one server.

-   More CPU
-   More RAM
-   More Disk

Pros: - Easy

Cons: - Hardware limit

## Horizontal Scaling

Add more servers.

Pros: - Better scalability - Better fault tolerance

Cons: - More complexity

------------------------------------------------------------------------

# 9. Common Mistakes

-   Learning Redis before understanding why cache exists.
-   Learning Kafka before understanding asynchronous processing.
-   Memorizing definitions instead of understanding problems.

------------------------------------------------------------------------

# 10. Interview Questions

### What is System Design?

Designing systems that continue to work efficiently as users, traffic
and data grow.

### Why do we need multiple servers?

To distribute traffic, improve availability and remove single points of
failure.

### Why do we need a Load Balancer?

To distribute requests across healthy servers.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Users Increase
          ↓
    Application Slows
          ↓
    Optimize Code
          ↓
    Vertical Scaling
          ↓
    Horizontal Scaling
          ↓
    Shared Database
          ↓
    Load Balancer

------------------------------------------------------------------------

**End of Part 1.1**

Next: Components of System Design
