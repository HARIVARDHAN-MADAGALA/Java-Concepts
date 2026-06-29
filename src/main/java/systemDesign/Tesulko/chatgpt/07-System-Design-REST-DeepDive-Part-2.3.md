# System Design Handbook

## Part 2.3 -- REST API Deep Dive

------------------------------------------------------------------------

# Table of Contents

1.  What is REST?
2.  REST Principles
3.  HTTP Request Lifecycle
4.  HTTP Methods
5.  URI Design
6.  Path Variables vs Query Parameters
7.  Request Headers
8.  Request Body
9.  Response Body
10. HTTP Status Codes
11. Idempotency
12. REST Best Practices
13. Spring Boot Mapping
14. Interview Questions
15. Cheat Sheet

------------------------------------------------------------------------

# 1. What is REST?

REST (Representational State Transfer) is an architectural style for
designing web APIs.

REST APIs communicate over HTTP and usually exchange JSON.

    Client
       │
    HTTP Request
       │
    Server
       │
    JSON Response

------------------------------------------------------------------------

# 2. REST Principles

-   Client-Server Architecture
-   Stateless Communication
-   Uniform Resource Identification
-   Cacheable Responses
-   Layered Architecture

The most important interview point is **statelessness**.

Each request must contain everything required to process it.

------------------------------------------------------------------------

# 3. HTTP Request Lifecycle

    Client

    ↓

    DNS

    ↓

    Load Balancer

    ↓

    Spring Boot API

    ↓

    Business Logic

    ↓

    Database

    ↓

    JSON Response

    ↓

    Client

------------------------------------------------------------------------

# 4. HTTP Methods

  Method   Purpose          Example
  -------- ---------------- ------------------
  GET      Read             GET /users
  POST     Create           POST /users
  PUT      Replace          PUT /users/10
  PATCH    Partial Update   PATCH /users/10
  DELETE   Remove           DELETE /users/10

------------------------------------------------------------------------

# 5. URI Design

Good

    /users
    /users/101
    /orders/25/items

Bad

    /getUsers
    /createUser
    /deleteUser

Resources should be nouns, not verbs.

------------------------------------------------------------------------

# 6. Path Variable vs Query Parameter

Path Variable

    GET /users/101

Spring Boot

``` java
@GetMapping("/users/{id}")
```

Query Parameter

    GET /users?page=1&size=20

Used for filtering, searching and pagination.

------------------------------------------------------------------------

# 7. Request Headers

Headers carry metadata.

Examples

    Authorization

    Content-Type

    Accept

    User-Agent

------------------------------------------------------------------------

# 8. Request Body

Used mainly with POST, PUT and PATCH.

Example

``` json
{
  "name":"Hari",
  "age":26
}
```

------------------------------------------------------------------------

# 9. Response Body

Example

``` json
{
  "id":101,
  "name":"Hari"
}
```

------------------------------------------------------------------------

# 10. HTTP Status Codes

  Code   Meaning
  ------ -----------------------
  200    OK
  201    Created
  204    No Content
  400    Bad Request
  401    Unauthorized
  403    Forbidden
  404    Not Found
  409    Conflict
  500    Internal Server Error

------------------------------------------------------------------------

# 11. Idempotency

Calling the API multiple times produces the same final result.

Idempotent

-   GET
-   PUT
-   DELETE

Usually Not Idempotent

-   POST

------------------------------------------------------------------------

# 12. REST Best Practices

-   Use nouns in URLs.
-   Use correct HTTP methods.
-   Return proper status codes.
-   Version APIs.
-   Keep APIs stateless.
-   Validate requests.
-   Return meaningful error messages.

------------------------------------------------------------------------

# 13. Spring Boot Mapping

``` java
@GetMapping("/users")
@PostMapping("/users")
@PutMapping("/users/{id}")
@PatchMapping("/users/{id}")
@DeleteMapping("/users/{id}")
```

------------------------------------------------------------------------

# 14. Interview Questions

### Why is REST stateless?

The server does not store client session state between requests.

### Difference between PUT and PATCH?

PUT replaces the entire resource.

PATCH updates only selected fields.

### Why use query parameters?

Filtering, sorting and pagination.

------------------------------------------------------------------------

# 15. Cheat Sheet

    GET    -> Read

    POST   -> Create

    PUT    -> Replace

    PATCH  -> Partial Update

    DELETE -> Remove

    200 OK

    201 Created

    400 Bad Request

    404 Not Found

    500 Internal Server Error

------------------------------------------------------------------------

Next Chapter: **Part 3.1 -- SQL Databases**
