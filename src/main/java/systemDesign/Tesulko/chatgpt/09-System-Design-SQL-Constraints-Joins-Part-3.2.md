# System Design Handbook

## Part 3.2 -- SQL Constraints, Joins & Relationships

------------------------------------------------------------------------

# Table of Contents

1.  Why Constraints Matter
2.  SQL Constraints
3.  Referential Integrity
4.  SQL Joins
5.  Relationship Types
6.  E-Commerce Example
7.  Spring Boot Mapping
8.  Interview Questions
9.  Cheat Sheet

------------------------------------------------------------------------

# 1. Why Constraints Matter

Databases should not store invalid data.

Constraints ensure data remains accurate and consistent.

Example:

A customer should not have two accounts with the same email.

------------------------------------------------------------------------

# 2. SQL Constraints

## PRIMARY KEY

-   Unique
-   NOT NULL
-   Identifies each row

## FOREIGN KEY

Links two tables.

## UNIQUE

Prevents duplicate values.

## NOT NULL

Column cannot contain NULL.

## DEFAULT

Provides a default value.

## CHECK

Validates a condition.

Example:

``` sql
age INT CHECK(age >= 18)
```

------------------------------------------------------------------------

# 3. Referential Integrity

Foreign keys ensure referenced data exists.

    Users
    +----+
    |101 |
    +----+

    Orders
    +----+---------+
    |1   | 101     |
    +----+---------+

`user_id=999` is invalid if user 999 doesn't exist.

------------------------------------------------------------------------

# 4. SQL Joins

## INNER JOIN

Returns matching rows only.

    Users
       ∩
    Orders

## LEFT JOIN

Returns all rows from the left table plus matching rows.

## RIGHT JOIN

Returns all rows from the right table plus matching rows.

## FULL OUTER JOIN

Returns every row from both tables.

## CROSS JOIN

Cartesian product.

Every row combines with every other row.

------------------------------------------------------------------------

# 5. Relationship Types

## One-to-One

    User → Passport

## One-to-Many

    Customer

    ↓

    Orders

## Many-to-Many

    Students

    ↕
    Courses

    ↓

    Student_Course

A junction table stores the mapping.

------------------------------------------------------------------------

# 6. E-Commerce Example

Tables

    Customers

    Orders

    Products

    Order_Items

Relationships

    Customer 1 --- * Orders

    Order 1 --- * Order_Items

    Product 1 --- * Order_Items

------------------------------------------------------------------------

# 7. Spring Boot Mapping

``` java
@OneToOne

@OneToMany

@ManyToOne

@ManyToMany
```

Example

``` java
class Order {

    @ManyToOne
    private Customer customer;
}
```

------------------------------------------------------------------------

# 8. Interview Questions

### Why use Foreign Keys?

To maintain referential integrity.

### Difference between INNER and LEFT JOIN?

INNER returns only matching rows.

LEFT returns all rows from the left table even if no match exists.

### When is a junction table required?

For Many-to-Many relationships.

------------------------------------------------------------------------

# 9. Cheat Sheet

    Constraints

    ↓

    PRIMARY KEY
    FOREIGN KEY
    UNIQUE
    NOT NULL
    DEFAULT
    CHECK

    -----------------

    Joins

    INNER
    LEFT
    RIGHT
    FULL
    CROSS

    -----------------

    Relationships

    1:1

    1:M

    M:N

------------------------------------------------------------------------

Next Chapter: **Part 3.3 -- NoSQL Databases**
