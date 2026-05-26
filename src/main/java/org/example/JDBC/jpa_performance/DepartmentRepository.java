package org.example.JDBC.jpa_performance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * =====================================================================
 *  DepartmentRepository: Complete JPA Performance Concepts
 * =====================================================================
 *  Concepts covered:
 *  1. JOIN FETCH         — Solves N+1 SELECT problem
 *  2. Pagination         — Page vs Slice
 *  3. EntityGraph        — Dynamic eager loading
 *  4. DTO Projections    — Lightweight read-only queries
 *  5. Bulk Modifying     — @Modifying + clearAutomatically = true (L1 cache gotcha!)
 * =====================================================================
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // =================================================================
    //  CONCEPT 1: JOIN FETCH (Solves N+1 SELECT Problem)
    // =================================================================
    /**
     * Without JOIN FETCH → 1 query for departments + N queries for each department's employees.
     * With JOIN FETCH   → exactly 1 SQL JOIN query fetches everything at once.
     *
     * ⚠️ CAVEAT: Do NOT use JOIN FETCH with Pageable (database pagination):
     *   Hibernate warns "HHH90003004: firstResult/maxResults specified with collection fetch;
     *   applying in memory!" — it fetches ALL rows into RAM, then paginates in memory. OOM risk!
     *   → Use EntityGraph instead for paginated queries with associations.
     */
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployeesJoinFetch();


    // =================================================================
    //  CONCEPT 2: PAGINATION (Page vs Slice)
    // =================================================================
    /**
     * PAGE: Fires SELECT + COUNT queries. Use when total pages/count is needed.
     */
    Page<Department> findByNameContaining(String name, Pageable pageable);

    /**
     * SLICE: Fires only SELECT with LIMIT+1. No COUNT. Use for infinite scroll / "Load More".
     */
    Slice<Department> queryByNameContaining(String name, Pageable pageable);


    // =================================================================
    //  CONCEPT 3: ENTITY GRAPH (Dynamic Fetching)
    // =================================================================
    /**
     * Ad-hoc EntityGraph: Override lazy fetch for specific method on the fly.
     */
    @EntityGraph(attributePaths = {"employees"})
    @Query("SELECT d FROM Department d WHERE d.name = :name")
    List<Department> findByNameWithAdHocEntityGraph(@Param("name") String name);

    /**
     * Named EntityGraph: References @NamedEntityGraph("Department.detail") defined on the entity.
     */
    @EntityGraph(value = "Department.detail")
    List<Department> findByName(String name);


    // =================================================================
    //  CONCEPT 4: DTO PROJECTION (Lightweight Read-Only Queries)
    // =================================================================
    /**
     * Returns a Spring proxy implementing DepartmentSummary — only fetches 'id' and 'name' columns.
     * The entity is NOT tracked in the Persistence Context → no dirty checking → faster!
     *
     * Generated SQL: SELECT d.id, d.name FROM performance_department d WHERE d.name LIKE ?
     * NOT:           SELECT d.id, d.name, d.version FROM performance_department d WHERE ...
     */
    List<DepartmentSummary> findSummaryByNameContaining(String name);

    /**
     * Custom JPQL Query returning a projection:
     */
    @Query("SELECT d.id AS id, d.name AS name FROM Department d")
    List<DepartmentSummary> findAllAsSummary();


    // =================================================================
    //  CONCEPT 5: BULK MODIFYING QUERY + L1 CACHE GOTCHA 🚨
    // =================================================================
    /**
     * 🧠 THE L1 CACHE (Persistence Context) PROBLEM WITH BULK UPDATES:
     *
     * Hibernate maintains an in-memory L1 cache (Persistence Context) of all entities
     * fetched in the current transaction. Any changes via @Modifying queries go
     * DIRECTLY to the database, completely bypassing this cache.
     *
     * ❌ DANGEROUS (Bug!):
     *    Department dept = repo.findById(1L).get();   // Loaded into L1 cache, salary = 50000
     *    repo.giveRaiseToAll(10000.0);                 // DB updated, but L1 cache still shows 50000!
     *    System.out.println(dept.getSalary());         // 💀 Still prints 50000 (STALE DATA!)
     *
     * ✅ FIXED with clearAutomatically = true:
     *    → Hibernate clears the entire L1 cache after the bulk query executes.
     *    → Next findById() will go to DB and get the fresh value.
     *
     * ⚠️ flushAutomatically = true:
     *    → Flushes any pending entity changes (dirty) to DB BEFORE the bulk query executes.
     *    → Prevents the bulk update from overwriting uncommitted dirty entity changes.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EmployeeEntity e SET e.salary = e.salary + :raise WHERE e.department.id = :deptId")
    int giveRaiseToDepartment(@Param("raise") Double raise, @Param("deptId") Long deptId);

    /**
     * Bulk DELETE — much faster than loading entities and calling delete() one by one.
     * Uses clearAutomatically to avoid Persistence Context desync.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM EmployeeEntity e WHERE e.department.id = :deptId")
    int deleteAllEmployeesInDepartment(@Param("deptId") Long deptId);
}
