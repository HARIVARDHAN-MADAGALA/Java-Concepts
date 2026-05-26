package org.example.JDBC.jpa_performance;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 *  JPA Entity: Department (Parent Entity)
 * =====================================================================
 * Demonstrates:
 *  - @NamedEntityGraph for fine-grained fetch control.
 *  - @Version for Optimistic Locking.
 *  - GenerationType.SEQUENCE to enable JDBC Batch Inserts.
 *  - @Index on @Table for database-level query optimization.
 * =====================================================================
 *
 * 🧠 INDEXES EXPLAINED:
 *
 *  What is a DB Index?
 *  → A database index is a special sorted lookup structure (like a book index)
 *    that allows the database to find rows WITHOUT scanning every row in the table.
 *
 *  Without Index:
 *  → DB does a Full Table Scan (O(N)) — reads every row.
 *  → Fast for tiny tables, CATASTROPHIC on millions of rows.
 *
 *  With Index:
 *  → DB uses a B-Tree (Binary Search, O(log N)) — jumps directly to results.
 *  → On 1 million rows, reduces from 1,000,000 reads to ~20!
 *
 *  Trade-offs:
 *  ✅ Reads become dramatically faster.
 *  ❌ Writes become slightly slower (index must be updated on INSERT/UPDATE/DELETE).
 *  ❌ Takes additional storage space.
 *
 *  Rule of Thumb:
 *  → Index columns used in WHERE, ORDER BY, GROUP BY, or JOIN ON clauses.
 *  → Never index columns with very few distinct values (e.g., boolean is_active).
 *  → Composite indexes: column ORDER matters!
 *    (name, city) index → can serve queries on name alone, or (name + city) together.
 *    But CANNOT efficiently serve queries filtering on city alone.
 *
 *  Types:
 *  - Regular Index: Fast reads, allows duplicates.
 *  - Unique Index: Enforces uniqueness + fast reads. (@Column(unique=true) creates one automatically)
 *  - Composite Index: Index on multiple columns for multi-column filtering or sorting.
 * =====================================================================
 */
@Entity
@Table(
    name = "performance_department",
    indexes = {
        // Single-column index on 'name' for fast queries like:
        // WHERE name = 'Engineering' or ORDER BY name
        @Index(name = "idx_dept_name", columnList = "name"),

        // Unique index — enforces that department names must be globally unique in the DB
        @Index(name = "idx_dept_name_unique", columnList = "name", unique = true)
    }
)
@NamedEntityGraph(
    name = "Department.detail",
    attributeNodes = {
        @NamedAttributeNode("employees")
    }
)
public class Department {

    // ⚡ CRITICAL FOR BATCHING: GenerationType.IDENTITY disables JDBC batching in Hibernate.
    // Hibernate must run INSERT immediately to get the DB-generated ID.
    // GenerationType.SEQUENCE pre-allocates IDs in memory → Hibernate can buffer
    // multiple INSERTs and flush them in a single batch network call.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dept_seq")
    @SequenceGenerator(name = "dept_seq", sequenceName = "performance_department_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeEntity> employees = new ArrayList<>();

    // ⚡ OPTIMISTIC LOCKING:
    // Hibernate auto-manages this field.
    // On every UPDATE: Hibernate adds "WHERE version = ?" to the SQL.
    // If another transaction updated the row first (incremented version),
    // this check fails → throws OptimisticLockException → prevents silent data corruption.
    @Version
    private Integer version;

    public Department() {}

    public Department(String name) {
        this.name = name;
    }

    public void addEmployee(EmployeeEntity employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<EmployeeEntity> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeEntity> employees) { this.employees = employees; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
