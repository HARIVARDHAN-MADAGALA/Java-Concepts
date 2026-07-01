# SQL Objects Cheat Sheet (SQL + Java)

## 1. VIEW

### Simple SQL

``` sql
CREATE VIEW employee_view AS
SELECT id, name, salary
FROM employee;
```

Use:

``` sql
SELECT * FROM employee_view;
```

### Java (JDBC)

``` java
String sql = "SELECT * FROM employee_view";
PreparedStatement ps = connection.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
```

### Complex SQL

``` sql
CREATE VIEW employee_department_view AS
SELECT e.id,
       e.name,
       d.department_name,
       e.salary
FROM employee e
JOIN department d
ON e.department_id = d.id
WHERE e.salary > 50000;
```

``` java
String sql = """
SELECT *
FROM employee_department_view
WHERE department_name = ?
""";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1,"IT");
ResultSet rs = ps.executeQuery();
```

------------------------------------------------------------------------

## 2. MATERIALIZED VIEW

``` sql
CREATE MATERIALIZED VIEW employee_mv AS
SELECT id,name,salary
FROM employee;
```

Refresh:

``` sql
REFRESH MATERIALIZED VIEW employee_mv;
```

Java:

``` java
connection.createStatement()
.execute("REFRESH MATERIALIZED VIEW employee_mv");
```

Complex:

``` sql
CREATE MATERIALIZED VIEW monthly_sales_mv AS
SELECT customer_id,
SUM(amount) total_sales
FROM orders
GROUP BY customer_id;
```

------------------------------------------------------------------------

## 3. TEMPORARY TABLE

``` sql
CREATE TEMP TABLE temp_employee(
 id INT,
 name VARCHAR(100)
);
```

``` sql
CREATE TEMP TABLE temp_high_salary AS
SELECT *
FROM employee
WHERE salary>60000;
```

Java:

``` java
Statement st = connection.createStatement();
st.execute("CREATE TEMP TABLE temp_employee(id INT,name VARCHAR(100))");
```

------------------------------------------------------------------------

## 4. INDEX

Simple:

``` sql
CREATE INDEX idx_employee_name
ON employee(name);
```

Composite:

``` sql
CREATE INDEX idx_dept_salary
ON employee(department_id,salary);
```

Java:

``` java
connection.createStatement()
.execute("CREATE INDEX idx_employee_name ON employee(name)");
```

------------------------------------------------------------------------

## 5. STORED PROCEDURE

Simple:

``` sql
DELIMITER //
CREATE PROCEDURE GetEmployees()
BEGIN
SELECT * FROM employee;
END//
DELIMITER ;
```

Call:

``` sql
CALL GetEmployees();
```

Java:

``` java
CallableStatement cs =
connection.prepareCall("{CALL GetEmployees()}");
ResultSet rs = cs.executeQuery();
```

IN parameter:

``` sql
DELIMITER //
CREATE PROCEDURE GetEmployeeByDept(IN deptId INT)
BEGIN
SELECT *
FROM employee
WHERE department_id=deptId;
END//
DELIMITER ;
```

Java:

``` java
CallableStatement cs =
connection.prepareCall("{CALL GetEmployeeByDept(?)}");
cs.setInt(1,10);
ResultSet rs = cs.executeQuery();
```

OUT parameter:

``` sql
DELIMITER //
CREATE PROCEDURE EmployeeCount(OUT total INT)
BEGIN
SELECT COUNT(*) INTO total FROM employee;
END//
DELIMITER ;
```

Java:

``` java
CallableStatement cs =
connection.prepareCall("{CALL EmployeeCount(?)}");
cs.registerOutParameter(1, java.sql.Types.INTEGER);
cs.execute();
int count = cs.getInt(1);
```

------------------------------------------------------------------------

## 6. FUNCTION

Simple:

``` sql
CREATE FUNCTION bonus(salary DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
RETURN salary*0.10;
```

Use:

``` sql
SELECT name, bonus(salary)
FROM employee;
```

Java:

``` java
PreparedStatement ps =
connection.prepareStatement(
"SELECT name, bonus(salary) FROM employee");
```

Complex:

``` sql
CREATE FUNCTION annualSalary(monthlySalary DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
RETURN monthlySalary*12;
```

------------------------------------------------------------------------

## Quick Summary

  Object              SQL                        Java
  ------------------- -------------------------- -------------------
  View                CREATE VIEW                PreparedStatement
  Materialized View   CREATE MATERIALIZED VIEW   Statement
  Temporary Table     CREATE TEMP TABLE          Statement
  Index               CREATE INDEX               Statement
  Stored Procedure    CREATE PROCEDURE           CallableStatement
  Function            CREATE FUNCTION            PreparedStatement

### Rule to Remember

-   SQL creates database objects.
-   Java calls or queries those objects using JDBC.
-   Use **PreparedStatement** for SELECT/INSERT/UPDATE/DELETE.
-   Use **CallableStatement** for Procedures.
-   Use **Statement** mostly for DDL (CREATE/DROP/ALTER).
