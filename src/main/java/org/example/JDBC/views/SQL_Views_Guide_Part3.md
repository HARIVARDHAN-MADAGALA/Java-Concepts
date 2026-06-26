# SQL Views, Temporary Tables & Materialized Views

# Part 3 - Temporary Tables

## Table of Contents

1.  Why Temporary Tables Were Invented
2.  History & Evolution
3.  What is a Temporary Table?
4.  Session vs Transaction Scope
5.  Local vs Global Temporary Tables
6.  Internal Architecture
7.  SQL Commands
8.  Java Examples
9.  Real-world Use Cases
10. Best Practices
11. Interview Questions

------------------------------------------------------------------------

# 1. Why Temporary Tables Were Invented

Applications needed a place to store **intermediate data** without
creating permanent tables.

Problems with normal tables: - Cleanup required - User data collisions -
Unnecessary storage growth

Temporary tables solve these problems.

# 2. What is a Temporary Table?

A temporary table stores **real rows** but only for a limited lifetime.

  View                   Temporary Table
  ---------------------- ------------------
  Stores SQL             Stores Data
  Permanent definition   Temporary object

# 3. Session Lifecycle

    Connect
       |
    Create Temp Table
       |
    Insert / Query
       |
    Disconnect
       |
    Dropped Automatically

# 4. Session vs Transaction Scope

**Session Scope** - Exists until the session ends.

**Transaction Scope** - Exists only until COMMIT or ROLLBACK (database
dependent).

# 5. Local vs Global Temporary Tables

SQL Server Local:

``` sql
CREATE TABLE #Orders(
 id INT,
 amount DECIMAL(10,2)
);
```

SQL Server Global:

``` sql
CREATE TABLE ##Orders(
 id INT,
 amount DECIMAL(10,2)
);
```

Oracle: - GLOBAL TEMPORARY TABLE keeps the definition permanently. -
Data remains temporary.

# 6. Internal Architecture

    Permanent Tables
    ----------------
    Employee
    Orders

    Temporary Area
    --------------
    temp_employee

# 7. SQL Commands

``` sql
CREATE TEMPORARY TABLE temp_employee(
 id INT,
 name VARCHAR(100)
);

INSERT INTO temp_employee VALUES(1,'Hari');

SELECT * FROM temp_employee;

DROP TABLE temp_employee;
```

# 8. JDBC Example

``` java
Connection con = DriverManager.getConnection(url,user,password);

Statement st = con.createStatement();

st.execute("CREATE TEMPORARY TABLE temp_employee(id INT,name VARCHAR(100))");

st.executeUpdate("INSERT INTO temp_employee VALUES(1,'Hari')");

ResultSet rs = st.executeQuery("SELECT * FROM temp_employee");
```

# 9. Spring Boot

``` java
entityManager.createNativeQuery(
"CREATE TEMPORARY TABLE temp_ids(id INT)"
).executeUpdate();
```

# 10. Real-world Uses

-   ETL staging
-   Banking transaction validation
-   Importing CSV files
-   Large reporting pipelines

# 11. Best Practices

-   Use only for intermediate data.
-   Drop early if possible.
-   Keep transactions short.
-   Don't use as permanent storage.

# 12. Interview Questions

1.  Why use temporary tables?
2.  Temporary table vs View?
3.  Local vs Global temporary tables?
4.  Session vs Transaction scope?
5.  Do temporary tables consume storage?

# Summary

Temporary tables physically store temporary data and are ideal for
staging, reporting, and ETL workflows.

**Next Part:** Materialized Views.
