package org.example.JDBC.jpa_performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * =====================================================================
 *  JPA Performance Concepts: Complete Demo Service
 * =====================================================================
 *
 *  Concepts covered:
 *   1. JOIN FETCH                 — Solving N+1 SELECTs
 *   2. Pagination                 — Page vs Slice
 *   3. EntityGraph                — Dynamic eager loading
 *   4. DTO / Interface Projection — Lightweight read-only queries
 *   5. Bulk Modifying Query       — @Modifying + L1 Cache Gotcha
 *   6. Optimistic Locking         — @Version to prevent lost updates
 * =====================================================================
 */
@Service
@Transactional(readOnly = true)
public class JpaPerformanceDemoService {

    @Autowired
    private DepartmentRepository departmentRepository;

    // =================================================================
    //  1. JOIN FETCH DEMO
    // =================================================================
    /**
     * 🧠 N+1 PROBLEM:
     *  - "SELECT d FROM Department d" → 1 query
     *  - Accessing dept.getEmployees() in a loop → N extra queries
     *
     * 🔑 KEY DIFFERENCE (very common interview question!):
     *  ─────────────────────────────────────────────────────────────
     *  JOIN FETCH (no LEFT) → INNER JOIN
     *    → Departments with NO employees are EXCLUDED from results!
     *
     *  LEFT JOIN FETCH → LEFT OUTER JOIN
     *    → ALL departments returned, even those with 0 employees.
     *    → Almost always the correct choice for @OneToMany.
     *  ─────────────────────────────────────────────────────────────
     *
     * ⚠️ WARNING: Never use JOIN FETCH with Pageable!
     *    Hibernate will load ALL records in memory then paginate → OOM risk.
     *    Use EntityGraph for paginated queries that need associations.
     */
    public void demonstrateJoinFetch() {
        System.out.println("🚀 JOIN FETCH → Exactly 1 SQL (LEFT OUTER JOIN)");
        List<Department> departments = departmentRepository.findAllWithEmployeesJoinFetch();
        for (Department dept : departments) {
            // ✅ Already loaded — NO lazy queries triggered here
            System.out.println(dept.getName() + " → " + dept.getEmployees().size() + " employees");
        }
    }


    // =================================================================
    //  2. PAGINATION DEMO
    // =================================================================
    /**
     * 🧠 PAGE vs SLICE:
     *
     *  Page<T>:
     *   → Fires TWO queries: SELECT (data) + SELECT COUNT(*) (total)
     *   → Has: getTotalElements(), getTotalPages(), isFirst(), isLast()
     *   → Best for: Traditional paginated tables (e.g. "Page 3 of 20")
     *
     *  Slice<T>:
     *   → Fires ONE query: SELECT with LIMIT = pageSize + 1
     *   → Has: hasNext(), hasPrevious() — but NO total count!
     *   → Best for: Infinite scroll / "Load More" on mobile/frontend
     *   → Much faster on large datasets (no expensive COUNT query)
     */
    public void demonstratePagination() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

        System.out.println("── Page (fires SELECT + COUNT) ──");
        Page<Department> page = departmentRepository.findByNameContaining("Eng", pageable);
        System.out.println("Total records: " + page.getTotalElements());
        System.out.println("Total pages:   " + page.getTotalPages());
        System.out.println("Is last page:  " + page.isLast());

        System.out.println("\n── Slice (fires only SELECT LIMIT+1) ──");
        Slice<Department> slice = departmentRepository.queryByNameContaining("Eng", pageable);
        System.out.println("Has next page: " + slice.hasNext());
        System.out.println("Items on this page: " + slice.getNumberOfElements());
    }


    // =================================================================
    //  3. ENTITY GRAPH DEMO
    // =================================================================
    /**
     * 🧠 ENTITY GRAPH vs JOIN FETCH:
     *
     *  ─────────────────────────────────────────────────────────────────
     *  Feature              │ JOIN FETCH          │ EntityGraph
     *  ─────────────────────┼─────────────────────┼───────────────────
     *  SQL Generated        │ You control it      │ Always LEFT JOIN
     *  Works with Pageable? │ ❌ (in-memory risk) │ ✅ Safe
     *  Where defined?       │ In @Query string    │ On method or entity
     *  Multiple collections │ ❌ Cartesian product │ ✅ (with subgraphs)
     *  ─────────────────────────────────────────────────────────────────
     *
     * Use EntityGraph when:
     *  → You need pagination WITH associations.
     *  → You want to override lazy for a single method without writing HQL.
     */
    public void demonstrateEntityGraph() {
        System.out.println("── Ad-hoc EntityGraph (defined on repository method) ──");
        List<Department> result1 = departmentRepository.findByNameWithAdHocEntityGraph("Engineering");

        System.out.println("\n── Named EntityGraph (references @NamedEntityGraph on entity) ──");
        List<Department> result2 = departmentRepository.findByName("Engineering");
    }


    // =================================================================
    //  4. DTO PROJECTION DEMO
    // =================================================================
    /**
     * 🧠 WHY PROJECTIONS?
     *
     *  Full entity fetch: SELECT d.id, d.name, d.version + Hibernate tracks it in L1 cache.
     *  Projection fetch:  SELECT d.id, d.name only + NOT tracked in L1 cache.
     *
     *  Projection is ideal when:
     *  → Building dropdowns / autocomplete (only ID + Name needed)
     *  → Dashboard statistics (aggregated values)
     *  → Any READ-ONLY view that doesn't need to update the entity
     */
    public void demonstrateProjection() {
        System.out.println("── Interface Projection (fetches only id + name columns) ──");
        List<DepartmentSummary> summaries = departmentRepository.findAllAsSummary();
        for (DepartmentSummary s : summaries) {
            System.out.println("ID: " + s.getId() + " | Name: " + s.getName());
        }
    }


    // =================================================================
    //  5. BULK MODIFYING QUERY + L1 CACHE GOTCHA
    // =================================================================
    /**
     * 🧠 THE L1 CACHE DESYNC PROBLEM (Critical Bug Pattern!):
     *
     *  Hibernate keeps entities in its L1 cache (Persistence Context).
     *  A @Modifying bulk UPDATE bypasses this cache and goes straight to DB.
     *
     *  ❌ WITHOUT clearAutomatically = true:
     *     Department dept = repo.findById(1L).get();   // In L1 cache
     *     repo.giveRaiseToDepartment(1000.0, 1L);      // DB updated, cache NOT cleared
     *     // dept.getSalary() still shows old value from cache → STALE DATA BUG!
     *
     *  ✅ WITH clearAutomatically = true:
     *     Hibernate wipes the L1 cache after bulk update.
     *     Next findById() will re-query the DB → fresh data.
     *
     *  Also: flushAutomatically = true ensures any dirty entities are flushed
     *  to DB BEFORE the bulk query runs, to avoid overwriting your changes.
     */
    @Transactional  // Override class-level readOnly = true
    public void demonstrateBulkUpdate(Long deptId) {
        System.out.println("── Bulk Salary Update with L1 Cache Safety ──");
        int updatedRows = departmentRepository.giveRaiseToDepartment(5000.0, deptId);
        System.out.println("Updated " + updatedRows + " employees (L1 cache cleared safely).");
    }


    // =================================================================
    //  6. OPTIMISTIC LOCKING DEMO
    // =================================================================
    /**
     * 🧠 OPTIMISTIC LOCKING WITH @Version:
     *
     *  Problem: Two users (Thread A & B) read the same record simultaneously.
     *   - Thread A updates salary to 70000. version: 0 → 1. DB saved.
     *   - Thread B (working from stale version 0) also updates salary to 80000.
     *   - WITHOUT @Version: Thread B silently overwrites Thread A's work! (Lost Update)
     *   - WITH @Version: Hibernate adds "WHERE version = 0" to Thread B's UPDATE.
     *     DB returns 0 rows (version is now 1, not 0) → throws OptimisticLockException.
     *     Thread B is forced to retry with fresh data. → No silent corruption!
     *
     *  Generated SQL (with @Version):
     *   UPDATE performance_department
     *   SET name = ?, version = 1
     *   WHERE id = ? AND version = 0  ← version check!
     *
     *  Note: @Version works AUTOMATICALLY — you never call it manually.
     *  Just having the field triggers this behavior in Hibernate.
     */
    @Transactional
    public void demonstrateOptimisticLocking() {
        try {
            Department dept = departmentRepository.findById(1L).orElseThrow();
            System.out.println("Loaded version: " + dept.getVersion());

            dept.setName("Updated Name");
            departmentRepository.save(dept);  // Hibernate checks version on UPDATE

            System.out.println("✅ Saved successfully. New version: " + dept.getVersion());

        } catch (ObjectOptimisticLockingFailureException ex) {
            // This exception fires when another transaction updated the same row first
            System.out.println("❌ Optimistic Lock conflict! Record was modified by another transaction.");
            System.out.println("   Solution: Re-fetch the entity and retry the operation.");
        }
    }
}
