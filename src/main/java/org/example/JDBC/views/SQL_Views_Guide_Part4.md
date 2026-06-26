# SQL Views, Temporary Tables & Materialized Views

# Part 4 - Materialized Views

## Table of Contents

1.  Why Materialized Views Were Invented
2.  History & Evolution
3.  What is a Materialized View?
4.  Internal Architecture
5.  Refresh Mechanisms
6.  SQL Commands
7.  Performance
8.  Java Integration
9.  Real-world Use Cases
10. Comparison
11. Best Practices
12. Interview Questions

------------------------------------------------------------------------

# 1. Why Were Materialized Views Invented?

Normal Views execute their SQL every time they are queried.

Example:

    Sales
    Orders
    Customers
    Products

A reporting query joining millions of rows can take several seconds or
minutes.

To avoid recalculating the same expensive query repeatedly, databases
introduced **Materialized Views**.

They store the **query result**, not just the SQL.

------------------------------------------------------------------------

# 2. Evolution

    Tables
       │
       ▼
    Views
    (SQL Only)
       │
       ▼
    Materialized Views
    (SQL + Stored Result)

------------------------------------------------------------------------

# 3. What is a Materialized View?

A Materialized View is a database object that stores the output of a
query physically.

Unlike a normal View:

  Feature              View      Materialized View
  -------------------- --------- -------------------
  Stores SQL           ✅        ✅
  Stores Result Data   ❌        ✅
  Fast Reads           Depends   Yes
  Needs Refresh        No        Yes

------------------------------------------------------------------------

# 4. Internal Architecture

    Orders
    Customers
    Products
          │
     Complex Query
          │
          ▼
    Materialized View
    (Stored Rows)
          │
     Application Reads

Applications query the stored result instead of recalculating the joins.

------------------------------------------------------------------------

# 5. Refresh Mechanisms

## Complete Refresh

Rebuilds the entire materialized view.

    Delete Old Data
          │
    Run Original Query
          │
    Store New Result

Best for small to medium datasets.

### Example

``` sql
REFRESH MATERIALIZED VIEW sales_summary;
```

------------------------------------------------------------------------

## Fast (Incremental) Refresh

Only changed rows are refreshed.

Advantages: - Faster - Less I/O - Suitable for frequently changing data

Requires database support and change tracking.

------------------------------------------------------------------------

## On Demand

Refresh only when explicitly requested.

Useful for nightly reports.

------------------------------------------------------------------------

## On Commit

Refresh automatically after transaction commit.

Provides fresher data but may slow write operations.

------------------------------------------------------------------------

# 6. SQL Examples

Oracle:

``` sql
CREATE MATERIALIZED VIEW sales_summary
BUILD IMMEDIATE
REFRESH COMPLETE
AS
SELECT customer_id,
SUM(amount) total_sales
FROM Orders
GROUP BY customer_id;
```

PostgreSQL:

``` sql
CREATE MATERIALIZED VIEW sales_summary AS
SELECT customer_id,
SUM(amount) total_sales
FROM Orders
GROUP BY customer_id;
```

Refresh:

``` sql
REFRESH MATERIALIZED VIEW sales_summary;
```

------------------------------------------------------------------------

# 7. Performance

Without Materialized View

    Application
          │
    Run Complex Join
          │
    Millions of Rows
          │
    Result

With Materialized View

    Application
          │
    Read Stored Result
          │
    Immediate Response

Trade-off:

-   Reads become much faster.
-   Writes may require refresh.

------------------------------------------------------------------------

# 8. Java Integration

Spring Boot Native Query

``` java
@Query(value = "SELECT * FROM sales_summary",
nativeQuery = true)
List<SalesSummary> findAll();
```

Refreshing (database-specific):

``` java
entityManager.createNativeQuery(
"REFRESH MATERIALIZED VIEW sales_summary"
).executeUpdate();
```

JPA treats it like a read-only table in most applications.

------------------------------------------------------------------------

# 9. Real-world Use Cases

## Business Intelligence

Dashboards showing:

-   Daily revenue
-   Monthly sales
-   Top products

------------------------------------------------------------------------

## Banking

Precompute:

-   Branch-wise balances
-   Daily transaction summaries

------------------------------------------------------------------------

## E-commerce

Store:

-   Top-selling products
-   Category statistics
-   Sales rankings

------------------------------------------------------------------------

## Data Warehousing

Materialized Views are heavily used in reporting systems where data is
refreshed hourly or nightly.

------------------------------------------------------------------------

# 10. Comparison

  Feature          View        Temp Table   Materialized View
  ---------------- ----------- ------------ -------------------
  Stores SQL       Yes         No           Yes
  Stores Data      No          Yes          Yes
  Lifetime         Permanent   Temporary    Permanent
  Refresh Needed   No          No           Yes
  Reporting        Moderate    Temporary    Excellent

------------------------------------------------------------------------

# 11. Best Practices

-   Use for expensive reporting queries.
-   Refresh during low-traffic periods if possible.
-   Don't use for rapidly changing OLTP data unless freshness
    requirements allow.
-   Monitor refresh duration.

------------------------------------------------------------------------

# 12. Interview Questions

1.  Why were Materialized Views invented?
2.  Difference between View and Materialized View?
3.  What is Complete Refresh?
4.  What is Fast Refresh?
5.  What is On Commit Refresh?
6.  Why are Materialized Views faster?
7.  Can Spring Boot query a Materialized View?
8.  When should you avoid using one?

------------------------------------------------------------------------

# Summary

Materialized Views trade storage and refresh cost for much faster query
performance. They are ideal for reporting, analytics, dashboards, and
data warehouses where read speed is more important than real-time
freshness.

**Next Part:** Performance tuning, execution plans, indexing, and
detailed comparisons of Tables, Views, Temporary Tables, and
Materialized Views.
