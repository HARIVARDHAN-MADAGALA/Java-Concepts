# System Design Handbook

## Part 2.2 -- APIs & Communication

------------------------------------------------------------------------

# Table of Contents

1.  What is an API?
2.  Request & Response
3.  REST API
4.  SOAP API
5.  GraphQL
6.  gRPC
7.  WebSockets
8.  When to Use What?
9.  Interview Questions
10. Cheat Sheet

------------------------------------------------------------------------

# 1. What is an API?

API (Application Programming Interface) is a contract that allows two
applications to communicate.

Example:

    Mobile App
        |
        | HTTP Request
        v
    Spring Boot API
        |
        v
    Database

The client never accesses the database directly.

------------------------------------------------------------------------

# 2. Request & Response

A client sends a request.

    GET /users/101

Server processes it and sends back a response.

``` json
{
  "id":101,
  "name":"Hari"
}
```

Every API interaction follows:

    Client
       ↓
    Request
       ↓
    Server
       ↓
    Response

------------------------------------------------------------------------

# 3. REST API

REST is the most common style for web APIs.

HTTP Methods

  Method   Purpose
  -------- -------------------------
  GET      Read Data
  POST     Create Data
  PUT      Replace Entire Resource
  PATCH    Update Partial Resource
  DELETE   Remove Resource

Example

    GET /products

    POST /products

    DELETE /products/5

Advantages

-   Simple
-   Stateless
-   JSON Support
-   Easy to scale

------------------------------------------------------------------------

# 4. SOAP API

SOAP uses XML and follows strict standards.

Example

    Client

    ↓

    SOAP Envelope

    ↓

    Server

Advantages

-   Strong security
-   Transactions
-   Enterprise systems

Disadvantages

-   Verbose XML
-   Slower than REST

Common Uses

-   Banking
-   Insurance
-   Legacy Enterprise Systems

------------------------------------------------------------------------

# 5. GraphQL

Instead of multiple endpoints, GraphQL exposes a single endpoint.

REST

    GET /user

    GET /orders

    GET /address

GraphQL

    POST /graphql

Client requests exactly the required fields.

Benefits

-   Avoids over-fetching
-   Avoids under-fetching

------------------------------------------------------------------------

# 6. gRPC

gRPC uses Protocol Buffers instead of JSON.

    Client

    ↓

    Protocol Buffers

    ↓

    Server

Benefits

-   Very fast
-   Small payload
-   Excellent for microservices

Typical Use

Backend service to backend service communication.

------------------------------------------------------------------------

# 7. WebSockets

HTTP

    Request

    ↓

    Response

    ↓

    Connection Closed

WebSocket

    Client

    ⇅

    Server

    Persistent Connection

Use Cases

-   Chat
-   Live Scores
-   Stock Market
-   Multiplayer Games

------------------------------------------------------------------------

# 8. When to Use What?

  Technology   Best Use
  ------------ -------------------------
  REST         Public APIs
  SOAP         Enterprise & Banking
  GraphQL      Flexible UI Queries
  gRPC         Microservices
  WebSocket    Real-time Communication

------------------------------------------------------------------------

# Interview Questions

### Why is REST popular?

Simple, stateless, JSON-based and easy to scale.

### Why use GraphQL?

Client requests only the required data.

### Why is gRPC faster?

Binary Protocol Buffers instead of JSON.

### When should WebSockets be used?

When real-time bidirectional communication is required.

------------------------------------------------------------------------

# Cheat Sheet

    REST
    ↓

    Simple CRUD APIs

    ----------------

    SOAP
    ↓

    Enterprise

    ----------------

    GraphQL
    ↓

    Flexible Queries

    ----------------

    gRPC
    ↓

    Microservices

    ----------------

    WebSocket
    ↓

    Real-time Apps

------------------------------------------------------------------------

Next Chapter: **Part 2.3 -- REST API Deep Dive (HTTP, Status Codes,
Request/Response, Best Practices)**
