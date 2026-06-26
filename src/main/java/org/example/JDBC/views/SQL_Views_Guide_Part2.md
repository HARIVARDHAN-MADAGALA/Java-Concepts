# SQL Views, Temporary Tables & Materialized Views

## Part 2 - Advanced Views, Performance, Security & Java

# Table of Contents

1.  Updatable vs Non-Updatable Views
2.  ALTER / CREATE OR REPLACE VIEW
3.  View Execution & Query Optimizer
4.  Performance Considerations
5.  Security with Views
6.  Views in JDBC
7.  Views in Spring Boot & Hibernate
8.  Enterprise Use Cases
9.  Best Practices
10. Interview Questions

------------------------------------------------------------------------

# 1. Updatable Views

A view is **updatable** when the database can clearly identify which row
in the base table should be modified.

``` sql
CREATE VIEW employee_public AS
SELECT id,name,salary
FROM Employee;
```

``` sql
UPDATE employee_public
SET salary=80000
WHERE id=1;
```

Execution:

    Application
        |
    UPDATE View
        |
    Database rewrites
        |
    UPDATE Employee

------------------------------------------------------------------------

# 2. Non-Updatable Views

``` sql
CREATE VIEW department_average AS
SELECT department_id,
AVG(salary) avg_salary
FROM Employee
GROUP BY department_id;
```

Reasons:

-   GROUP BY
-   DISTINCT
-   Aggregate Functions
-   UNION
-   Complex joins

------------------------------------------------------------------------

# 3. ALTER / REPLACE VIEW

``` sql
CREATE OR REPLACE VIEW employee_public AS
SELECT id,name,email
FROM Employee;
```

------------------------------------------------------------------------

# 4. Query Optimizer

    SELECT * FROM employee_public
    WHERE salary>50000;

    ↓

    Optimizer expands

    ↓

    SELECT id,name,salary
    FROM Employee
    WHERE salary>50000;

Views store SQL, not rows.

------------------------------------------------------------------------

# 5. Performance

Good:

-   Security
-   Reusable SQL
-   Reporting

Avoid:

-   Deep nested views
-   Heavy joins inside multiple layers
-   Assuming views automatically improve speed

------------------------------------------------------------------------

# 6. Security

Hide confidential columns:

``` sql
CREATE VIEW employee_portal AS
SELECT id,name
FROM Employee;
```

Grant access to the view instead of the base table.

------------------------------------------------------------------------

# 7. JDBC Example

``` java
PreparedStatement ps =
con.prepareStatement(
"SELECT * FROM employee_public WHERE id=?");

ps.setInt(1,1);

ResultSet rs = ps.executeQuery();
```

------------------------------------------------------------------------

# 8. Spring Boot

``` java
@Entity
@Table(name="employee_public")
public class EmployeePublic{

    @Id
    private Long id;

    private String name;
}
```

``` java
@Repository
public interface EmployeeRepository
extends JpaRepository<EmployeePublic,Long>{
}
```

------------------------------------------------------------------------

# 9. Enterprise Examples

## Banking

Support team:

-   Account Number
-   Customer Name

Not:

-   PIN
-   Password
-   Aadhaar

## HR

Managers use one view.

Payroll team uses another view.

------------------------------------------------------------------------

# 10. Best Practices

-   Keep views simple.
-   Avoid nested views.
-   Use views for security.
-   Test execution plans.

------------------------------------------------------------------------

# 11. Interview Questions

1.  What is an updatable view?
2.  Why are aggregate views read-only?
3.  Does a view store rows?
4.  Can JPA map a view?
5.  When should you use a materialized view instead?

------------------------------------------------------------------------

# Summary

  Feature    Updatable   Non-Updatable
  ---------- ----------- ---------------
  INSERT     Yes         No
  UPDATE     Yes         No
  DELETE     Yes         No
  GROUP BY   No          Yes

**Next:** Temporary Tables.
