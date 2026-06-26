# SQL Views, Temporary Tables & Materialized Views

## Part 1 - Views: History, Concepts, SQL & Java

> Author: ChatGPT Study Notes

------------------------------------------------------------------------

# Table of Contents

1.  History of Databases
2.  Why Views Were Invented
3.  Evolution of Views
4.  What is a View?
5.  Internal Architecture
6.  How a View Executes
7.  Creating Views
8.  Types of Views
9.  WITH CHECK OPTION
10. Advantages & Disadvantages
11. Java Integration
12. Real-world Use Cases
13. Common Mistakes
14. Interview Questions

------------------------------------------------------------------------

# 1. History of Databases

Early applications stored data directly in files.

Problems:

-   Data duplication
-   Difficult searching
-   Poor security
-   Multiple applications maintaining the same data

Databases introduced:

-   Centralized storage
-   SQL
-   Transactions
-   Security
-   Data consistency

------------------------------------------------------------------------

# 2. Why Were Views Invented?

Imagine this table:

``` sql
Employee
--------------------------------------------
id | name | salary | password | aadhaar
```

HR should see everything.

Employees should **not** see salary, password or Aadhaar.

Instead of copying data into another table, databases introduced
**Views**.

A View behaves like a virtual table.

------------------------------------------------------------------------

# 3. Evolution

    Files
       │
       ▼
    Relational Tables
       │
       ▼
    Views
       │
       ▼
    Materialized Views

Views solved:

-   Security
-   Query reuse
-   Simpler SQL
-   Logical abstraction

------------------------------------------------------------------------

# 4. What is a View?

A View stores **only the SQL query**, not the rows.

Example:

``` sql
CREATE VIEW employee_basic AS
SELECT id,name
FROM Employee;
```

Internally:

    Database
       │
       ├── Employee Table
       │
       └── employee_basic
              │
              └── SELECT id,name FROM Employee

------------------------------------------------------------------------

# 5. Internal Execution

    Application

    SELECT * FROM employee_basic

            │

    Database

    Find View

            │

    Replace View with SQL

            │

    SELECT id,name FROM Employee

            │

    Read Employee Table

            │

    Return Result

The database expands the view definition before executing.

------------------------------------------------------------------------

# 6. SQL Commands

## Create

``` sql
CREATE VIEW employee_view AS
SELECT id,name,salary
FROM Employee;
```

## Query

``` sql
SELECT * FROM employee_view;
```

## Replace

``` sql
CREATE OR REPLACE VIEW employee_view AS
SELECT id,name
FROM Employee;
```

## Drop

``` sql
DROP VIEW employee_view;
```

------------------------------------------------------------------------

# 7. Types of Views

## Simple View

Uses one table.

``` sql
CREATE VIEW employee_names AS
SELECT id,name
FROM Employee;
```

## Join View

``` sql
CREATE VIEW employee_department AS
SELECT e.name,d.department_name
FROM Employee e
JOIN Department d
ON e.department_id=d.id;
```

## Aggregate View

``` sql
CREATE VIEW department_salary AS
SELECT department_id,
AVG(salary) average_salary
FROM Employee
GROUP BY department_id;
```

------------------------------------------------------------------------

# 8. WITH CHECK OPTION

``` sql
CREATE VIEW it_employees AS
SELECT *
FROM Employee
WHERE department='IT'
WITH CHECK OPTION;
```

Now inserting an HR employee through this view is rejected.

------------------------------------------------------------------------

# 9. Advantages

-   Hide sensitive columns
-   Reuse complex SQL
-   Cleaner application code
-   Logical abstraction
-   Easier maintenance

# Disadvantages

-   Can become slow for complex joins
-   Cannot always update data
-   Depends on underlying tables

------------------------------------------------------------------------

# 10. Java Example (Spring Boot)

Entity:

``` java
@Entity
@Table(name="employee_view")
public class EmployeeView{

    @Id
    private Long id;

    private String name;

    private Double salary;
}
```

Repository:

``` java
public interface EmployeeViewRepository
extends JpaRepository<EmployeeView,Long>{
}
```

Usage:

``` java
List<EmployeeView> employees=
repository.findAll();
```

The application doesn't know whether it is reading from a table or a
view.

------------------------------------------------------------------------

# 11. Real-world Example

Banking:

    Customer Table

    Account Number
    Balance
    PAN
    Password
    PIN

Customer portal View:

    Account Number
    Balance

Sensitive columns remain hidden.

------------------------------------------------------------------------

# 12. Common Mistakes

❌ Assuming a View stores data.

Reality: It stores only SQL.

❌ Thinking every View is updatable.

Many are read-only because of joins, GROUP BY, DISTINCT or aggregates.

------------------------------------------------------------------------

# 13. Interview Questions

1.  What is a View?
2.  Why were Views invented?
3.  Difference between Table and View?
4.  Are Views stored physically?
5.  Can Views improve security?
6.  Why are some Views not updateable?
7.  Difference between View and Materialized View?
8.  Can indexes be created on Views?
9.  What happens internally when querying a View?
10. When should you avoid using Views?

------------------------------------------------------------------------

# Summary

  Feature            Table    View
  ------------------ -------- ----------
  Stores Data        ✅       ❌
  Stores SQL         ❌       ✅
  Occupies Storage   High     Very Low
  Can Join Tables    N/A      ✅
  Security           Medium   High
  Used in Java       ✅       ✅

**Next Part:** Temporary Tables, session lifecycle, transactions, JDBC
and Spring Boot examples.
