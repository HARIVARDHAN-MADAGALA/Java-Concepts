# System Design Handbook

## Part 3.1 -- SQL Databases

------------------------------------------------------------------------

# Table of Contents

1.  What is a Database?
2.  Why SQL?
3.  Tables, Rows & Columns
4.  Primary Key
5.  Foreign Key
6.  Constraints
7.  Relationships
8.  SQL vs File Storage
9.  Spring Boot Mapping
10. Interview Questions
11. Cheat Sheet

------------------------------------------------------------------------

# 1. What is a Database?

A database is software used to store, retrieve and manage data
efficiently.

Examples:

-   MySQL
-   PostgreSQL
-   Oracle
-   SQL Server

Without a database, application data is lost after restart.

------------------------------------------------------------------------

# 2. Why SQL?

SQL (Structured Query Language) works with **relational databases**.

It stores data in structured tables.

Example:

    Users Table

    +----+--------+----------------+
    | ID | Name   | Email          |
    +----+--------+----------------+
    |101 | Hari   | h@example.com  |
    |102 | Ravi   | r@example.com  |
    +----+--------+----------------+

------------------------------------------------------------------------

# 3. Tables, Rows & Columns

**Table** → Collection of related data.

**Row** → One record.

**Column** → One attribute.

Example

    Table: Users

    Columns:
    - id
    - name
    - email

    One Row:
    101 | Hari | h@example.com

------------------------------------------------------------------------

# 4. Primary Key

A Primary Key uniquely identifies each row.

Properties:

-   Unique
-   Not NULL
-   One Primary Key per table

Example

    Users

    ID (PK)

------------------------------------------------------------------------

# 5. Foreign Key

A Foreign Key creates a relationship between tables.

Example

    Users
    -----
    id

    Orders
    ------
    id
    user_id (FK)

`user_id` references `Users.id`.

------------------------------------------------------------------------

# 6. Constraints

Constraints enforce data integrity.

Common constraints:

-   PRIMARY KEY
-   FOREIGN KEY
-   NOT NULL
-   UNIQUE
-   CHECK
-   DEFAULT

Example

``` sql
email VARCHAR(100) UNIQUE
```

No duplicate emails are allowed.

------------------------------------------------------------------------

# 7. Relationships

## One-to-One

    User ---- Passport

## One-to-Many

    Customer

    ↓

    Orders

One customer can have many orders.

## Many-to-Many

    Students

    ↕
    Courses

Implemented using a junction table.

------------------------------------------------------------------------

# 8. SQL vs File Storage

Files

-   Difficult to search
-   Duplicate data
-   No relationships

SQL

-   Fast querying
-   Relationships
-   Transactions
-   Constraints

------------------------------------------------------------------------

# 9. Spring Boot Mapping

``` java
@Entity
class User {

    @Id
    private Long id;

    private String name;
}
```

Relationships

``` java
@OneToMany
@ManyToOne
@OneToOne
@ManyToMany
```

------------------------------------------------------------------------

# 10. Interview Questions

### Why use Primary Keys?

To uniquely identify each record.

### Why use Foreign Keys?

To maintain relationships and referential integrity.

### Difference between UNIQUE and PRIMARY KEY?

PRIMARY KEY is unique + NOT NULL.

UNIQUE allows only unique values but may allow NULL depending on the
database.

------------------------------------------------------------------------

# 11. Cheat Sheet

    Database
        ↓
    Table
        ↓
    Rows
        ↓
    Columns

    PK → Unique Row

    FK → Relationship

    Constraints → Data Integrity

------------------------------------------------------------------------

Next Chapter: **Part 3.2 -- SQL Constraints, Joins & Relationships**
