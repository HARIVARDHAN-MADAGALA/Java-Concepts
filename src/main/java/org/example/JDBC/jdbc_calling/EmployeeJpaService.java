package org.example.JDBC.jdbc_calling;

import java.util.List;

import org.example.JDBC.Employee;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.ParameterMode;

/**
 * Way 3: Spring Data JPA — create and call in same service
 */
@Service
public class EmployeeJpaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;   // to create the proc

    @PersistenceContext
    private EntityManager entityManager;  // to call the proc

    // ── CREATE the procedure on startup
    @PostConstruct
    public void createProcedure() {

        jdbcTemplate.execute(
                "DROP PROCEDURE IF EXISTS get_employees_by_dept");

        jdbcTemplate.execute("""
            CREATE PROCEDURE get_employees_by_dept(
                IN p_department VARCHAR(100)
            )
            BEGIN
                SELECT id, name, department, salary
                FROM   employee
                WHERE  department = p_department;
            END
            """);
    }

    // ── CALL the procedure via EntityManager
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Employee> getByDepartment(String dept) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery(
                        "get_employees_by_dept", Employee.class)
                .registerStoredProcedureParameter(
                        "p_department", String.class, ParameterMode.IN);

        query.setParameter("p_department", dept);
        query.execute();

        return query.getResultList();
    }
}
