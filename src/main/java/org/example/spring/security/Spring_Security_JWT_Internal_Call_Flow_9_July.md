# Spring Security JWT - Internal Request Flow (Who Calls Whom)

This document explains the **actual execution flow** inside Spring
Security after a request reaches your application.

------------------------------------------------------------------------

# Big Picture

``` text
Client
   │
   ▼
Embedded Tomcat
   │
   ▼
DelegatingFilterProxy
   │
   ▼
FilterChainProxy
   │
   ▼
SecurityFilterChain
   │
   ▼
JwtAuthenticationFilter
   │
   ▼
UsernamePasswordAuthenticationFilter (used mainly for form login)
   │
   ▼
AuthorizationFilter
   │
   ▼
DispatcherServlet
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Database
```

------------------------------------------------------------------------

# Step 1 - Client Sends Request

Example

``` http
GET /employees
Authorization: Bearer eyJhbGc...
```

The browser or Postman sends the request.

------------------------------------------------------------------------

# Step 2 - Embedded Tomcat

Tomcat receives every HTTP request.

Tomcat knows nothing about JWT or Spring Security.

Its only job is to forward the request to the Spring application.

Input

-   HttpServletRequest
-   HttpServletResponse

Calls

``` text
DelegatingFilterProxy
```

------------------------------------------------------------------------

# Step 3 - DelegatingFilterProxy

Think of this as a bridge.

It connects the Servlet container (Tomcat) with Spring-managed filters.

Method

``` java
doFilter(request, response, chain)
```

Calls

``` text
FilterChainProxy
```

------------------------------------------------------------------------

# Step 4 - FilterChainProxy

This is Spring Security's master filter.

Responsibilities

-   Find the matching SecurityFilterChain
-   Execute security filters in order

Method

``` java
doFilter()
```

Calls

``` text
SecurityFilterChain
```

------------------------------------------------------------------------

# Step 5 - SecurityFilterChain

Configured in your

``` java
SecurityConfig
```

Example

``` java
http
   .authorizeHttpRequests(...)
   .addFilterBefore(jwtAuthenticationFilter,
       UsernamePasswordAuthenticationFilter.class);
```

This defines the order of filters.

------------------------------------------------------------------------

# Step 6 - JwtAuthenticationFilter

Usually

``` java
class JwtAuthenticationFilter extends OncePerRequestFilter
```

Method

``` java
doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
)
```

Input

-   Request
-   Response
-   FilterChain

Responsibilities

1.  Read Authorization header
2.  Extract JWT
3.  Validate JWT
4.  Load user
5.  Store Authentication in SecurityContext
6.  Continue filter chain

------------------------------------------------------------------------

# Step 7 - Read Header

``` java
String header =
request.getHeader("Authorization");
```

Gets

``` text
Bearer eyJhbGc...
```

------------------------------------------------------------------------

# Step 8 - Extract JWT

``` java
String token =
header.substring(7);
```

Now token contains only the JWT.

------------------------------------------------------------------------

# Step 9 - JwtUtil

Methods

``` java
extractUsername(token)

validateToken(token)
```

Checks

-   Signature
-   Expiry
-   Secret key

Returns username if valid.

------------------------------------------------------------------------

# Step 10 - UserDetailsService

Called as

``` java
loadUserByUsername(username)
```

Returns

``` java
UserDetails
```

Contains

-   username
-   password
-   authorities

------------------------------------------------------------------------

# Step 11 - Create Authentication

``` java
UsernamePasswordAuthenticationToken authentication =
new UsernamePasswordAuthenticationToken(
    userDetails,
    null,
    userDetails.getAuthorities()
);
```

Password is null because login already happened.

------------------------------------------------------------------------

# Step 12 - SecurityContextHolder

``` java
SecurityContextHolder
.getContext()
.setAuthentication(authentication);
```

Think of SecurityContext as a locker for the current request.

Any code can later do

``` java
SecurityContextHolder
.getContext()
.getAuthentication();
```

to know who the logged-in user is.

------------------------------------------------------------------------

# Step 13 - Continue

``` java
filterChain.doFilter(request,response);
```

Without this line, the request stops at the filter.

------------------------------------------------------------------------

# Step 14 - AuthorizationFilter

Now Spring checks

-   Is user authenticated?
-   Does the user have required role?

Example

``` java
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

If failed

``` text
403 Forbidden
```

If passed

Request continues.

------------------------------------------------------------------------

# Step 15 - DispatcherServlet

This is Spring MVC's front controller.

Responsibilities

-   Find matching controller
-   Invoke controller method

------------------------------------------------------------------------

# Step 16 - Controller

Example

``` java
@GetMapping("/employees")
```

Now controller executes because authentication and authorization already
succeeded.

Controller can access current user

``` java
Authentication authentication =
SecurityContextHolder
.getContext()
.getAuthentication();
```

------------------------------------------------------------------------

# Complete Call Stack

``` text
Client
 ↓
Tomcat
 ↓
DelegatingFilterProxy.doFilter()
 ↓
FilterChainProxy.doFilter()
 ↓
SecurityFilterChain
 ↓
JwtAuthenticationFilter.doFilterInternal()
 ↓
request.getHeader()
 ↓
JwtUtil.extractUsername()
 ↓
JwtUtil.validateToken()
 ↓
UserDetailsService.loadUserByUsername()
 ↓
UsernamePasswordAuthenticationToken
 ↓
SecurityContextHolder.setAuthentication()
 ↓
filterChain.doFilter()
 ↓
AuthorizationFilter
 ↓
DispatcherServlet
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
```

------------------------------------------------------------------------

# Interview Tip

Remember responsibilities instead of memorizing classes.

  -----------------------------------------------------------------------------
  Class                     Responsibility
  ------------------------- ---------------------------------------------------
  Tomcat                    Receives HTTP request

  DelegatingFilterProxy     Bridge to Spring Security

  FilterChainProxy          Runs all security filters

  SecurityFilterChain       Defines filter order

  JwtAuthenticationFilter   Reads and validates JWT

  JwtUtil                   Creates and validates JWT

  UserDetailsService        Loads user from database

  PasswordEncoder           Verifies password during login

  SecurityContextHolder     Stores authenticated user for current request

  DispatcherServlet         Calls the correct controller

  Controller                Handles business request
  -----------------------------------------------------------------------------
