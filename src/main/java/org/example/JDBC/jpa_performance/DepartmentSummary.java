package org.example.JDBC.jpa_performance;

/**
 * =====================================================================
 *  DTO Projection Interface: DepartmentSummary
 * =====================================================================
 *
 * CONCEPT: Interface Projection (Lightweight Read-Only DTO)
 *
 * ❌ Problem with Fetching Full Entities for READ-ONLY displays:
 *    When you do: List<Department> findAll();
 *    Hibernate fetches ALL columns, loads them into the Persistence Context,
 *    and tracks them for dirty checking. For a simple dropdown list of
 *    [ID + Name], this is massive overkill.
 *
 * ✅ Solution: Interface Projection
 *    - Hibernate only fetches the columns you need.
 *    - The result is NOT tracked in Persistence Context (no dirty checking).
 *    - Much less heap memory usage.
 *    - Works with both Spring Data derived query methods and @Query.
 *
 * How it works:
 *    - Spring Data generates a Proxy class at runtime that implements this interface.
 *    - Method names must match entity field names following JavaBean conventions:
 *      getId() → maps to field 'id'
 *      getName() → maps to field 'name'
 *
 * =====================================================================
 */
public interface DepartmentSummary {

    // Hibernate will only SELECT id and name columns from the database!
    Long getId();

    String getName();

    /**
     * SpEL Expression Projection:
     * You can also combine fields using Spring Expression Language (SpEL).
     * @Value is used inside projections for computed, virtual properties.
     *
     * Example use case: A "display label" combining ID + Name
     */
    // @Value("#{target.id + ' - ' + target.name}")
    // String getLabel();
}
