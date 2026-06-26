# SQL Views, Temporary Tables & Materialized Views

# Part 5 - Performance, Indexing & Choosing the Right Solution

## Table of Contents

1.  Performance Overview
2.  Query Execution Flow
3.  Query Optimizer
4.  Indexing
5.  Storage Comparison
6.  Execution Cost
7.  When to Use What
8.  Decision Tree
9.  Enterprise Scenarios
10. Common Mistakes
11. Interview Questions
12. Cheat Sheet

------------------------------------------------------------------------

# 1. Performance Overview

  Object              Read Speed         Write Speed        Storage
  ------------------- ------------------ ------------------ -----------
  Table               Fast               Fast               High
  View                Depends on query   Same as table      Very Low
  Temporary Table     Fast               Fast               Temporary
  Materialized View   Very Fast          Refresh Required   High

------------------------------------------------------------------------

# 2. Query Execution Flow

## Normal Table

    Application
        |
    SELECT
        |
    Read Table
        |
    Return Rows

## View

    Application
        |
    SELECT View
        |
    Expand SQL
        |
    Optimizer
        |
    Read Base Tables

## Materialized View

    Application
        |
    SELECT MV
        |
    Read Stored Rows
        |
    Return Result

------------------------------------------------------------------------

# 3. Query Optimizer

The optimizer decides:

-   Which indexes to use
-   Join order
-   Full table scan vs index scan
-   Sort strategy

A View does **not** bypass the optimizer. The optimizer expands the view
definition first.

------------------------------------------------------------------------

# 4. Indexing

## Tables

Indexes are commonly created.

``` sql
CREATE INDEX idx_employee_name
ON Employee(name);
```

## Views

Normal views usually cannot have their own indexes because they don't
store rows.

Performance comes from indexes on the underlying tables.

## Materialized Views

Many databases allow indexes because rows are physically stored.

Example:

``` sql
CREATE INDEX idx_sales_customer
ON sales_summary(customer_id);
```

------------------------------------------------------------------------

# 5. Storage Comparison

    Table
    ██████████████████████

    View
    █

    Temporary Table
    ██████ (temporary)

    Materialized View
    ██████████████

Views consume almost no storage because only metadata and SQL are
stored.

------------------------------------------------------------------------

# 6. Execution Cost

Imagine a report joining:

-   Customers
-   Orders
-   Products
-   Payments

Every execution of a normal View repeats the joins.

A Materialized View executes the join once during refresh, then serves
stored results.

------------------------------------------------------------------------

# 7. Which One Should You Use?

## Use a Table

When data is permanent.

Examples:

-   Employee
-   Customer
-   Orders

## Use a View

When:

-   Hiding columns
-   Simplifying SQL
-   Reusing joins

## Use a Temporary Table

When:

-   Staging imported files
-   Intermediate calculations
-   ETL processing

## Use a Materialized View

When:

-   Dashboards
-   BI reports
-   Analytics
-   Large aggregations
-   Data warehouse queries

------------------------------------------------------------------------

# 8. Decision Tree

    Need permanent data?

            Yes
             |
          Table
             |
    Need reusable SQL?
             |
           Yes
             |
           View
             |
    Need stored query result?
             |
           Yes
             |
    Materialized View

    Need temporary intermediate rows?
             |
    Temporary Table

------------------------------------------------------------------------

# 9. Enterprise Scenarios

## Banking

Tables: - Accounts - Transactions

Views: - Customer portal

Materialized Views: - Daily branch reports

Temporary Tables: - End-of-day settlement processing

------------------------------------------------------------------------

## E-commerce

Tables: - Products - Orders

Views: - Product catalog

Materialized Views: - Top-selling products

Temporary Tables: - Flash-sale calculations

------------------------------------------------------------------------

## ETL Pipeline

CSV \| Temp Table \| Validation \| Permanent Table \| Materialized View
\| Dashboard

------------------------------------------------------------------------

# 10. Common Mistakes

❌ Using Views for performance.

Views mainly improve maintainability and security.

❌ Using Materialized Views for rapidly changing OLTP systems.

Frequent refreshes can outweigh benefits.

❌ Forgetting to refresh Materialized Views.

Users may see stale data.

❌ Using Temporary Tables as permanent storage.

------------------------------------------------------------------------

# 11. Interview Questions

1.  Why don't normal Views improve performance automatically?
2.  Can Views have indexes?
3.  Why are Materialized Views faster?
4.  Which object consumes the least storage?
5.  Which object is best for ETL?
6.  Which object is best for dashboards?
7.  Why are Temporary Tables session-specific?

------------------------------------------------------------------------

# 12. Cheat Sheet

  Requirement               Best Choice
  ------------------------- -------------------
  Permanent Business Data   Table
  Hide Columns              View
  Reusable SQL              View
  Intermediate Processing   Temporary Table
  Reporting Dashboard       Materialized View
  Data Warehouse            Materialized View
  ETL Staging               Temporary Table
  Security Layer            View

## Summary

-   **Tables** store business data.
-   **Views** encapsulate SQL and provide abstraction.
-   **Temporary Tables** hold short-lived working data.
-   **Materialized Views** store expensive query results for fast
    reporting.

**Next Part:** Real-world enterprise architecture, design patterns,
anti-patterns, and advanced use cases.
