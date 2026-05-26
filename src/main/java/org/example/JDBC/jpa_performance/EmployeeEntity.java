package org.example.JDBC.jpa_performance;

import jakarta.persistence.*;

/**
 * =====================================================================
 *  JPA Entity: EmployeeEntity (Child Entity)
 * =====================================================================
 * Demonstrates:
 *  - @Version for Optimistic Locking.
 *  - GenerationType.SEQUENCE for JDBC Batch Insert support.
 *  - @Index for query optimization.
 *  - Composite @Index for multi-column filtering.
 * =====================================================================
 *
 * 🧠 COMPOSITE INDEX EXPLAINED (see below on @Table):
 *
 *  A composite index (idx_emp_dept_salary) covers: (department_id, salary)
 *
 *  Queries that BENEFIT from it:
 *  ✅ WHERE department_id = 5                      (uses left-most prefix)
 *  ✅ WHERE department_id = 5 AND salary > 50000   (uses both columns)
 *  ✅ ORDER BY department_id, salary               (uses both for sorted reads)
 *
 *  Queries that do NOT benefit (missing left-most prefix rule):
 *  ❌ WHERE salary > 50000                         (skips department_id entirely)
 *
 *  This is called the "Left-Most Prefix Rule" of composite indexes.
 * =====================================================================
 */
@Entity
@Table(
    name = "performance_employee",
    indexes = {
        // Index on name column — speeds up search-by-name queries
        @Index(name = "idx_emp_name", columnList = "name"),

        // Index on department_id foreign key — very important!
        // Without this, joining departments to employees would do a full scan on the employees table.
        @Index(name = "idx_emp_department", columnList = "department_id"),

        // Composite Index: covers filtering by department AND salary together.
        // Example query: "Find all employees in department 5 earning > 50000"
        @Index(name = "idx_emp_dept_salary", columnList = "department_id, salary")
    }
)
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_seq")
    @SequenceGenerator(name = "emp_seq", sequenceName = "performance_employee_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double salary;

    // Always override default EAGER on ManyToOne to LAZY!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // ⚡ OPTIMISTIC LOCKING: Auto-managed by Hibernate
    // On every UPDATE: Hibernate adds WHERE version = ? to SQL.
    @Version
    private Integer version;

    public EmployeeEntity() {}

    public EmployeeEntity(String name, Double salary) {
        this.name = name;
        this.salary = salary;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
