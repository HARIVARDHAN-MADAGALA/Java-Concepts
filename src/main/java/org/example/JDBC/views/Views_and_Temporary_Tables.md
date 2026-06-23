# Views and Temporary Tables — Origin, Types, Behavior, and Differences

This document explains database views and temporary tables end-to-end: history and origin, the different kinds of views (virtual/simple/complex/updatable/materialized/indexed), lifecycle and refresh strategies, temporary tables (local vs global), contrasts between views and temp tables, practical examples across popular RDBMS, and recommended best practices.

---

## 1. What is a View?

- A *view* is a named query stored in the database catalog. Conceptually it is a virtual table: when you select from a view the DBMS runs the underlying query and returns results as if from a table.
- Views provide abstraction, encapsulation, security, simplified queries, and a stable logical schema even when base tables change.

Origin note: views appeared early in the history of relational databases (1970s/1980s) as a way to expose logical relations without duplicating data. Over time vendors added optimizations (query rewrite) and persisted/materialized variants to improve performance.

## 2. Types of Views

1. Simple (Virtual) View
   - Defined by a single-table SELECT without aggregates, GROUP BY, DISTINCT, or set operations.
   - Often updatable (depends on DBMS): INSERT/UPDATE/DELETE can map to the underlying table.

2. Complex (Derived) View
   - Uses joins, aggregates, GROUP BY, DISTINCT, UNION, window functions, or subqueries.
   - Usually read-only; not updatable in most systems.

3. Updatable View
   - A view that allows DML (INSERT/UPDATE/DELETE) which the database translates to DML on the underlying base table(s).
   - Strict rules apply; many DBMS require the view to reference a single base table and not use aggregates.

4. Read-only View
   - Explicitly or implicitly non-updatable. Often used for reporting or security.

5. Materialized View (MV)
   - The view result is physically stored (materialized) in the database as a table.
   - Improves read performance for expensive queries but introduces maintenance overhead: the MV must be refreshed to reflect base table changes.
   - Refresh strategies: immediate (on commit), on demand, scheduled, incremental/fast/complete/force refresh (terminology varies by vendor).
   - Some DBMS support query rewrite — optimizer automatically uses an MV to answer queries against base tables.

6. Indexed View (SQL Server) / Materialized View with Index
   - A persisted view that has an index to speed queries. SQL Server calls these "indexed views"; Oracle/Postgres provide materialized views which can be indexed.

7. Partitioned/Partition-Aware Materialized Views
   - Some systems support partitioned MVs for very large data volumes and efficient refreshes.

8. Schema-bound View
   - In SQL Server, a view can be created WITH SCHEMABINDING to prevent changes to base objects that would invalidate the view.

## 3. Materialized View Refresh Modes (common patterns)

- Complete (Full) Refresh: recompute the entire MV by re-running its defining query. Simple but expensive.
- Fast / Incremental Refresh: only apply deltas (requires materialized view logs / change tracking or replication metadata). More complex but efficient for large datasets.
- On commit / Immediate: refresh as part of transaction commit — keeps MV consistent but more overhead.
- Scheduled / Manual: refresh when convenient (nightly, hourly) — acceptable when slightly stale data is fine.

Vendor differences: Oracle has sophisticated materialized view logs and refresh groups; PostgreSQL supports REFRESH MATERIALIZED VIEW (can be CONCURRENTLY with limitations); MySQL historically lacked native MVs (can emulate using table + triggers or use MySQL 8+ features or third-party engines); SQL Server uses indexed views.

## 4. Temporary Tables (Local and Global)

- A *temporary table* is a physical table that exists for the duration of a session (local temp table) or across sessions (global temp table) depending on the DBMS.

Types and behavior:
- Local Temporary Table (session-scoped)
  - SQL Server: `CREATE TABLE #temp (...)` exists only in the creating session and is dropped automatically when the session ends.
  - PostgreSQL: `CREATE TEMP TABLE temp (...)` is session-local by default and isolated per session.
  - MySQL: `CREATE TEMPORARY TABLE temp (...)` is limited to the current connection.

- Global Temporary Table
  - SQL Server: `##globalTemp` shared across sessions but data visibility follows rules; global temp tables drop when last session referencing them ends.
  - Some systems provide variants or do not support true global temp tables.

Lifecycle and performance
- Temporary tables store data physically (in tempdb or equivalent). They are good for intermediate transformations, complex ETL-like steps, and allowing indexes/statistics to speed repeated access.
- They incur IO and resource usage but avoid materializing permanent structures and can improve performance for multi-step queries.

## 5. Views vs Temporary Tables — When to use which

Comparison summary:

- Data Persistence
  - View: virtual (no storage) unless materialized
  - Temp table: physically stored for the session

- Freshness
  - View: always reflects current base table data (unless underlying data changes during execution); materialized view can be stale between refreshes
  - Temp table: snapshot captured at creation time; stays unchanged unless explicitly modified

- Performance
  - View: cheap to create; performance depends on optimizer and complexity; repeated expensive view evaluation can be costly
  - Temp table: potentially faster for repeated access, because data and indexes are materialized; good for complex multi-step processing

- Updatability
  - View: may be updatable (with restrictions); many are read-only
  - Temp table: full DML support like a table

- Use cases
  - Views: simplify queries, encapsulate logic, enforce security (column/row-level via view), present logical schema
  - Materialized views: reporting/BI where query cost is high and slightly stale data is acceptable
  - Temp tables: intermediate results in ETL, complex multi-join step breakdowns, storing query results for repeated processing

## 6. Temporary Tables vs Materialized Views

- Similarity: both physically store results and can speed reads.
- Differences:
  - Temp tables are session-local and often short-lived; MVs are persistent objects managed by the catalog and have refresh semantics.
  - MVs can support query rewrite by optimizer; temp tables are explicit and require manual creation/population.

## 7. Security, Permissions, and Ownership

- Views can encapsulate and limit exposure: grant SELECT on the view but not on base tables.
- Materialized view access typically follows the same privilege model as tables.
- Temporary tables are owned by the creating session; permission models vary.

## 8. Examples

PostgreSQL - simple view
```sql
CREATE VIEW sales_by_customer AS
SELECT customer_id, SUM(amount) total
FROM sales
GROUP BY customer_id;

SELECT * FROM sales_by_customer WHERE total > 1000;
```

PostgreSQL - materialized view
```sql
CREATE MATERIALIZED VIEW mv_sales_by_customer AS
SELECT customer_id, SUM(amount) total
FROM sales
GROUP BY customer_id;

-- Refresh manually
REFRESH MATERIALIZED VIEW mv_sales_by_customer;

-- Concurrent refresh (keeps old data available while refreshing)
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_sales_by_customer; -- requires unique index
```

SQL Server - indexed view (persisted result with an index)
```sql
CREATE VIEW dbo.vOrders WITH SCHEMABINDING AS
SELECT o.CustomerID, COUNT_BIG(*) AS OrderCount
FROM dbo.Orders o
GROUP BY o.CustomerID;

CREATE UNIQUE CLUSTERED INDEX IX_vOrders ON dbo.vOrders(CustomerID);
-- Now queries can use the indexed view
```

MySQL - temporary table (emulate MV)
```sql
CREATE TEMPORARY TABLE tmp_sales AS
SELECT customer_id, SUM(amount) total
FROM sales
GROUP BY customer_id;

SELECT * FROM tmp_sales WHERE total > 1000;
```

Oracle - materialized view with refresh
```sql
CREATE MATERIALIZED VIEW mv_sales
REFRESH FAST ON COMMIT
AS
SELECT customer_id, SUM(amount) total
FROM sales
GROUP BY customer_id;
```

## 9. Performance considerations & best practices

- Prefer views to encapsulate logic and present consistent API. Use materialized views or temp tables when repeated expensive computation must be avoided.
- If using MVs, choose an appropriate refresh strategy: incremental when possible, scheduled if stale data is acceptable.
- Monitor storage and refresh time for MVs — they consume disk and maintenance windows.
- For temp tables: create appropriate indexes if you read repeatedly; drop or allow automatic cleanup to free temp space.
- Beware concurrency effects with MVs refreshed on commit — can affect transaction throughput.
- Use query rewrite features (if available) carefully and test with the optimizer to ensure the MV is being used.

## 10. Quick decision guide

- Need logical abstraction, security, or compatibility layer: use VIEW.
- Need fast repeated reads of expensive query and can tolerate staleness: use MATERIALIZED VIEW.
- Need intermediate, session-lifetime storage for staged processing or ETL: use TEMPORARY TABLE.
- Need persistent precomputed results with optimizer support and indexes: use INDEXED VIEW / MATERIALIZED VIEW + indexes.

## 11. Limitations and gotchas

- Updatability rules vary across DBMS — do not assume a view is updatable.
- Materialized view refresh semantics, concurrency, and logging differ widely; read vendor docs.
- Temporary table storage location (tempdb vs in-memory) affects performance; heavy temp usage can impact production systems.

---

If you want, I can:
- Add a short comparison table for Oracle / PostgreSQL / SQL Server / MySQL (syntax and feature support).
- Produce a few unit-test-like SQL scripts to demonstrate performance differences (small dataset) with timings.

File: `src/main/java/org/example/concepts/JDBC/Views_and_Temporary_Tables.md`

