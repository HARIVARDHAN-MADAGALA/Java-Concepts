# System Design Handbook

## Part 9.1 -- Monitoring & Observability

------------------------------------------------------------------------

# Table of Contents

1.  Why Monitoring?
2.  Monitoring vs Observability
3.  Metrics
4.  Logs
5.  Traces
6.  Four Golden Signals
7.  Infrastructure Monitoring
8.  Spring Boot Monitoring
9.  Popular Tools
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. Why Monitoring?

Building a system is only half the job.

Once deployed, you must know:

-   Is the application healthy?
-   Is it slow?
-   Is it failing?
-   Which server has the problem?

Monitoring answers these questions.

------------------------------------------------------------------------

# 2. Monitoring vs Observability

## Monitoring

Monitoring tells you **that** something is wrong.

Example:

    CPU = 98%

## Observability

Observability helps explain **why** something is wrong.

It combines:

-   Metrics
-   Logs
-   Traces

------------------------------------------------------------------------

# 3. Metrics

Metrics are numerical measurements collected over time.

Common metrics:

-   CPU Usage
-   Memory Usage
-   Disk Usage
-   Network Usage
-   Requests/Second
-   Response Time
-   Error Count

Example:

    CPU = 75%

    Memory = 60%

------------------------------------------------------------------------

# 4. Logs

Logs are timestamped records of application events.

Example

    2026-06-29 10:15:21

    INFO User 101 Logged In

Log Levels

-   DEBUG
-   INFO
-   WARN
-   ERROR

------------------------------------------------------------------------

# 5. Traces

A trace follows one request across multiple services.

Example

    Client

    ↓

    API Gateway

    ↓

    Order Service

    ↓

    Payment Service

    ↓

    Inventory Service

Each step records latency.

Useful in microservices.

------------------------------------------------------------------------

# 6. Four Golden Signals

Google SRE recommends monitoring:

## Latency

How long requests take.

## Traffic (Throughput)

Requests per second.

## Errors

Failed requests.

## Saturation

Resource utilization.

Example:

    CPU = 95%

    Memory = 92%

------------------------------------------------------------------------

# 7. Infrastructure Monitoring

Monitor:

-   CPU
-   Memory
-   Disk
-   Network
-   Database
-   Load Balancer
-   Cache
-   Kafka

Healthy infrastructure is essential for reliable systems.

------------------------------------------------------------------------

# 8. Spring Boot Monitoring

Spring Boot Actuator provides endpoints.

Examples

    /actuator/health

    /actuator/metrics

    /actuator/info

    /actuator/prometheus

Micrometer exports metrics to monitoring systems.

------------------------------------------------------------------------

# 9. Popular Tools

Metrics

-   Prometheus

Dashboards

-   Grafana

Logs

-   ELK Stack (Elasticsearch, Logstash, Kibana)

Tracing

-   Jaeger
-   Zipkin

Cloud

-   AWS CloudWatch
-   Azure Monitor

------------------------------------------------------------------------

# 10. Interview Questions

### Difference between Monitoring and Observability?

Monitoring detects issues.

Observability helps determine the root cause.

### What are the Four Golden Signals?

-   Latency
-   Traffic
-   Errors
-   Saturation

### Which Spring Boot endpoint exposes metrics?

`/actuator/metrics`

### Which tool visualizes Prometheus metrics?

Grafana.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Monitoring

    ↓

    Metrics
    Logs
    Traces

    ----------------

    Golden Signals

    Latency
    Traffic
    Errors
    Saturation

    ----------------

    Spring Boot

    /actuator/health
    /actuator/metrics

    ----------------

    Tools

    Prometheus
    Grafana
    ELK
    Jaeger
    Zipkin

------------------------------------------------------------------------

Next Chapter: **Part 10.1 -- System Design Case Study: Video Streaming
Application**
