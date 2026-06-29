# System Design Handbook

## Part 2.1 -- DNS (Domain Name System)

------------------------------------------------------------------------

# Table of Contents

1.  What is DNS?
2.  Why DNS is Needed
3.  Domain vs IP Address
4.  DNS Resolution Flow
5.  Root Server
6.  TLD Server
7.  Authoritative Name Server
8.  DNS Caching
9.  DNS Records
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. What is DNS?

DNS (Domain Name System) translates a human-readable domain name into an
IP address.

Example:

    google.com

    ↓

    142.x.x.x

Computers communicate using IP addresses, not names.

------------------------------------------------------------------------

# 2. Why DNS is Needed

Humans remember:

    amazon.com

Computers understand:

    54.239.x.x

DNS acts like the Internet's phonebook.

------------------------------------------------------------------------

# 3. Domain vs IP Address

    Domain

    google.com

    ↓

    DNS

    ↓

    IP Address

    142.x.x.x

------------------------------------------------------------------------

# 4. DNS Resolution Flow

    Browser

    ↓

    DNS Resolver

    ↓

    Root Server

    ↓

    TLD Server (.com)

    ↓

    Authoritative Name Server

    ↓

    IP Address

    ↓

    Web Server

------------------------------------------------------------------------

# 5. Root Server

The Root Server knows where Top-Level Domain (TLD) servers are located.

Examples of TLDs:

-   .com
-   .org
-   .net
-   .in

It does NOT know the website IP.

------------------------------------------------------------------------

# 6. TLD Server

The TLD server knows which Authoritative Name Server manages a domain.

Example

    google.com

    ↓

    TLD (.com)

    ↓

    Google Name Server

------------------------------------------------------------------------

# 7. Authoritative Name Server

Stores the actual DNS records.

Example

    google.com

    ↓

    142.x.x.x

This is the final answer returned to the browser.

------------------------------------------------------------------------

# 8. DNS Caching

DNS lookups are expensive.

Caches exist in:

-   Browser
-   Operating System
-   ISP Resolver

```{=html}
<!-- -->
```
    Browser Cache

    ↓

    OS Cache

    ↓

    ISP Cache

    ↓

    Root → TLD → Authoritative

Caching reduces latency.

------------------------------------------------------------------------

# 9. Common DNS Records

## A Record

Maps domain to IPv4.

## AAAA Record

Maps domain to IPv6.

## CNAME

Alias for another domain.

## MX

Mail server.

## TXT

Verification / metadata.

------------------------------------------------------------------------

# Example

    Browser

    ↓

    www.google.com

    ↓

    DNS Resolver

    ↓

    Root

    ↓

    .com

    ↓

    Google Authoritative Server

    ↓

    142.x.x.x

    ↓

    Google Server

    ↓

    HTML Response

------------------------------------------------------------------------

# Interview Questions

### Why do we need DNS?

Humans remember names; computers communicate using IP addresses.

### Does Root Server know website IP?

No.

It only points to the correct TLD.

### Why cache DNS?

To reduce lookup time and internet traffic.

### What is a CNAME?

An alias pointing one domain to another.

------------------------------------------------------------------------

# Cheat Sheet

    Domain

    ↓

    Resolver

    ↓

    Root

    ↓

    TLD

    ↓

    Authoritative

    ↓

    IP Address

    ↓

    Website

------------------------------------------------------------------------

Next Chapter: **Part 2.2 -- APIs & Communication (REST, SOAP, GraphQL,
gRPC, WebSockets)**
