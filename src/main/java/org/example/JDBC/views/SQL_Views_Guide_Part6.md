# SQL Views, Temporary Tables & Materialized Views

# Part 6 - Enterprise Architecture & Real-World Design

## Table of Contents

1.  Enterprise Database Layers
2.  Banking Case Study
3.  E-Commerce Case Study
4.  Healthcare Case Study
5.  Analytics & Data Warehouse
6.  Combining All Four Objects
7.  Design Patterns
8.  Anti-Patterns
9.  Production Best Practices
10. Troubleshooting
11. Interview Scenarios

------------------------------------------------------------------------

# 1. Enterprise Database Layers

    Frontend
        |
    Spring Boot APIs
        |
    -------------------------
    | Views               |
    | Materialized Views  |
    | Temporary Tables    |
    | Base Tables         |
    -------------------------
        |
    Database

Each object has a different responsibility.

-   Tables → Business data
-   Views → Security & abstraction
-   Temporary Tables → Intermediate processing
-   Materialized Views → Reporting

------------------------------------------------------------------------

# 2. Banking Case Study

## Tables

    Customer
    Account
    Transaction
    Loan
    Card

## Views

Customer Portal:

``` sql
CREATE VIEW customer_dashboard AS
SELECT account_no,balance
FROM Account;
```

Users cannot directly access sensitive columns.

## Temporary Tables

Nightly reconciliation:

    Import File
         |
    Temporary Table
         |
    Validation
         |
    Transaction Table

## Materialized View

Daily branch report:

``` sql
SELECT branch_id,
SUM(balance)
FROM Account
GROUP BY branch_id;
```

Refresh every night.

------------------------------------------------------------------------

# 3. E-Commerce Case Study

Tables

-   Product
-   Order
-   Customer
-   Inventory

Views

    Product Catalog

Shows only public information.

Temporary Tables

Flash sale processing:

    Eligible Products
            |
    Temp Table
            |
    Discount Engine

Materialized View

    Top Selling Products

Used by dashboards.

------------------------------------------------------------------------

# 4. Healthcare

Tables

-   Patient
-   Doctor
-   Appointment

View

Receptionists should not see diagnosis details.

``` sql
CREATE VIEW reception_view AS
SELECT patient_id,
patient_name,
appointment_date
FROM Appointment;
```

Materialized View

Daily appointment statistics.

------------------------------------------------------------------------

# 5. Analytics & Data Warehouse

Typical Flow

    CSV Files
        |
    Temporary Tables
        |
    Cleaning
        |
    Fact & Dimension Tables
        |
    Materialized Views
        |
    Power BI / Tableau

Materialized views reduce dashboard response time.

------------------------------------------------------------------------

# 6. Combining All Four

    Employee Table
          |
          +------ View --------> HR Portal
          |
          +------ Temp Table ---> Payroll Batch
          |
          +------ Materialized View ---> Executive Dashboard

The same base table supports multiple workloads.

------------------------------------------------------------------------

# 7. Design Patterns

## Security Pattern

    Users
       |
    Views
       |
    Tables

Applications never access tables directly.

------------------------------------------------------------------------

## Reporting Pattern

    OLTP Tables
          |
    Materialized View
          |
    Dashboard

------------------------------------------------------------------------

## ETL Pattern

    Import
       |
    Temp Table
       |
    Validation
       |
    Production Tables

------------------------------------------------------------------------

# 8. Anti-Patterns

❌ View on View on View

    View A
      |
    View B
      |
    View C

Hard to optimize.

❌ Materialized View refreshed every second.

Refresh cost may exceed query savings.

❌ Using temporary tables for permanent business records.

------------------------------------------------------------------------

# 9. Production Best Practices

-   Name views clearly.
-   Version complex reporting queries.
-   Refresh materialized views during off-peak hours.
-   Monitor execution plans.
-   Index base tables properly.
-   Clean temporary data promptly.

------------------------------------------------------------------------

# 10. Troubleshooting

## Slow View

Check:

-   Missing indexes
-   Large joins
-   Nested views
-   Execution plan

## Slow Materialized View Refresh

Check:

-   Refresh schedule
-   Incremental refresh support
-   Query complexity

## Temporary Table Issues

Check:

-   Session lifetime
-   Transaction boundaries
-   Cleanup

------------------------------------------------------------------------

# 11. Interview Scenarios

### Scenario 1

Your dashboard takes 45 seconds because it joins 12 tables.

**Answer:** Consider a Materialized View refreshed periodically.

------------------------------------------------------------------------

### Scenario 2

HR users should never see salaries.

**Answer:** Create a View exposing only required columns.

------------------------------------------------------------------------

### Scenario 3

You import a 5 GB CSV every night.

**Answer:** Load into a Temporary Table, validate, then move to
production tables.

------------------------------------------------------------------------

### Scenario 4

An API performs expensive calculations used only within one request.

**Answer:** Use a Temporary Table (or CTE if appropriate) instead of
permanent storage.

------------------------------------------------------------------------

# Summary

Enterprise systems rarely rely on only one database object.

A typical production architecture combines:

-   Tables for business data
-   Views for abstraction and security
-   Temporary Tables for processing
-   Materialized Views for reporting

Choosing the correct object improves maintainability, performance, and
security.

**Next Part:** Final revision guide, advanced interview questions,
exercises, and mini-project.
