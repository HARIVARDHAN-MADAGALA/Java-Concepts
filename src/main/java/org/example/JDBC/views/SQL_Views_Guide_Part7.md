# SQL Views, Temporary Tables & Materialized Views

# Part 7 - Advanced Interview Guide & Database-Specific Behavior

## Table of Contents

1.  MySQL vs PostgreSQL vs Oracle vs SQL Server
2.  Tricky Interview Questions
3.  Common Misconceptions
4.  Edge Cases
5.  Best Practices
6.  Decision Matrix
7.  Rapid Revision Notes

------------------------------------------------------------------------

# 1. Database-Specific Behavior

  Feature                  MySQL       PostgreSQL   Oracle   SQL Server
  ------------------------ ----------- ------------ -------- --------------
  Views                    ✅          ✅           ✅       ✅
  Materialized Views       ❌ Native   ✅           ✅       ❌ Native
  Temporary Tables         ✅          ✅           ✅       ✅
  CREATE OR REPLACE VIEW   ✅          ✅           ✅       ALTER/CREATE

## Notes

### MySQL

-   Supports Views.
-   Supports Temporary Tables.
-   No native Materialized Views (commonly simulated using tables +
    scheduled refresh).

### PostgreSQL

-   Supports all three.
-   Materialized Views require manual refresh.

### Oracle

-   Rich Materialized View support.
-   Fast Refresh, Complete Refresh, On Commit Refresh.
-   GLOBAL TEMPORARY TABLE stores the definition permanently while data
    remains temporary.

### SQL Server

-   Supports indexed views (with restrictions).
-   Uses `#` and `##` for local/global temporary tables.

------------------------------------------------------------------------

# 2. Tricky Interview Questions

### Q1. Does a View improve performance?

Answer: Not automatically. A View is expanded into its underlying SQL.
Performance depends on indexes, joins, statistics, and the execution
plan.

------------------------------------------------------------------------

### Q2. Can you create an index on a View?

Normal View: Usually no.

Materialized View: Often yes.

SQL Server: Indexed Views are supported under specific conditions.

------------------------------------------------------------------------

### Q3. Does a View duplicate table data?

No.

Only the SQL definition is stored.

------------------------------------------------------------------------

### Q4. Why is a Materialized View faster?

Because the query result is already stored.

------------------------------------------------------------------------

### Q5. Why isn't everyone using Materialized Views?

Trade-offs:

-   Refresh cost
-   Additional storage
-   Potentially stale data

------------------------------------------------------------------------

# 3. Common Misconceptions

❌ Views are copies of tables.

Reality: They are virtual tables.

------------------------------------------------------------------------

❌ Temporary Tables exist forever.

Reality: They disappear based on session or transaction scope (database
dependent).

------------------------------------------------------------------------

❌ Materialized Views update automatically.

Reality: Only if configured. Many databases require explicit refresh.

------------------------------------------------------------------------

# 4. Edge Cases

## View on View

    View A
      |
    View B
      |
    View C

Works, but excessive nesting hurts readability and may complicate
optimization.

------------------------------------------------------------------------

## Updating Join Views

Some databases allow limited updates.

Many reject updates because one change could affect multiple base
tables.

------------------------------------------------------------------------

## Temporary Tables in Connection Pools

Spring Boot applications often reuse database connections.

Be careful: A temporary table may survive longer than expected if the
same session is reused.

Always clean up explicitly when appropriate.

------------------------------------------------------------------------

## Materialized View Staleness

    Orders Updated

    Materialized View

    (not refreshed)

    ↓

    Dashboard shows old values.

------------------------------------------------------------------------

# 5. Best Practices

### Views

-   Keep business logic simple.
-   Use for security and reusable SQL.
-   Document dependencies.

### Temporary Tables

-   Use for staging.
-   Avoid large unnecessary indexes.
-   Drop early when possible.

### Materialized Views

-   Schedule refresh wisely.
-   Index frequently queried columns.
-   Monitor refresh duration.

------------------------------------------------------------------------

# 6. Decision Matrix

  Requirement              Recommended Object
  ------------------------ --------------------
  Permanent data           Table
  Hide sensitive columns   View
  Reuse SQL                View
  ETL staging              Temporary Table
  Dashboard                Materialized View
  Analytics                Materialized View
  One-time calculation     Temporary Table
  API security layer       View

------------------------------------------------------------------------

# 7. Rapid Revision

## Table

-   Stores business data
-   Permanent
-   CRUD operations

## View

-   Stores SQL only
-   Virtual
-   Security
-   Query abstraction

## Temporary Table

-   Stores temporary rows
-   Session/transaction scoped
-   ETL
-   Batch processing

## Materialized View

-   Stores query results
-   Requires refresh
-   Reporting
-   Dashboards
-   Analytics

------------------------------------------------------------------------

# 8. 20 Quick Interview Questions

1.  What is a View?
2.  Does a View store data?
3.  Why use Views?
4.  What is an updatable View?
5.  Why are aggregate Views read-only?
6.  What is a Temporary Table?
7.  Session vs Transaction scope?
8.  Local vs Global Temporary Tables?
9.  Why use Temporary Tables?
10. What is a Materialized View?
11. Why was it invented?
12. Complete Refresh?
13. Fast Refresh?
14. Can Materialized Views have indexes?
15. Why are dashboards a good fit?
16. Does MySQL support Materialized Views?
17. Can Spring Boot read a View?
18. Can JDBC create Temporary Tables?
19. Why might a View be slow?
20. Which object would you choose for reporting?

## Summary

Interviewers care less about syntax and more about **why** you choose
one object over another. Always explain the trade-offs in performance,
storage, security, maintenance, and freshness.

**Next:** Part 8 - Mini project, exercises, final cheat sheet, and
complete revision.
