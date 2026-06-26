# SQL Views, Temporary Tables & Materialized Views

# Part 8 - Final Revision, Mini Project & Hands-on Exercises

## Complete Comparison

  Feature       Table       View        Temporary Table       Materialized View
  ------------- ----------- ----------- --------------------- -------------------
  Stores Data   Yes         No          Yes                   Yes
  Stores SQL    No          Yes         No                    Yes
  Lifetime      Permanent   Permanent   Session/Transaction   Permanent
  Best Use      OLTP        Security    ETL                   Reporting

## Decision Flow

``` text
Permanent business data? -> Table
Need reusable SQL? -> View
Need temporary processing? -> Temporary Table
Need fast reporting? -> Materialized View
```

## Mini Project

### Base Tables

``` sql
Employee(id,name,department_id,salary)
Department(id,name)
```

### View

``` sql
CREATE VIEW employee_profile AS
SELECT id,name,department_id
FROM Employee;
```

### Temporary Table

``` sql
CREATE TEMPORARY TABLE temp_payroll(
 emp_id INT,
 net_salary DECIMAL(10,2)
);
```

### Materialized View

``` sql
CREATE MATERIALIZED VIEW department_salary_summary AS
SELECT department_id,AVG(salary) avg_salary
FROM Employee
GROUP BY department_id;
```

## Hands-on Exercises

1.  Create a View hiding salary.
2.  Create a Join View.
3.  Create a Temporary Table and insert data.
4.  Refresh a Materialized View.
5.  Explain why aggregate views are normally read-only.

## Spring Boot

``` java
@Repository
public interface EmployeeViewRepository
extends JpaRepository<EmployeeView,Long>{}
```

``` java
@Query(value="SELECT * FROM employee_profile",nativeQuery=true)
List<EmployeeView> findAllProfiles();
```

## Common Production Problems

-   Slow Views: missing indexes, complex joins.
-   Temporary Tables: session ends unexpectedly.
-   Materialized Views: stale data due to missed refresh.

## Final Cheat Sheet

-   Table = Permanent business data
-   View = Virtual table (stores SQL)
-   Temporary Table = Short-lived working data
-   Materialized View = Stored query result

## Interview Revision

-   View does not store rows.
-   Temporary Tables store rows temporarily.
-   Materialized Views require refresh.
-   Explain trade-offs: performance, storage, freshness, maintenance.

# Congratulations

You have completed the complete guide on Views, Temporary Tables, and
Materialized Views.
