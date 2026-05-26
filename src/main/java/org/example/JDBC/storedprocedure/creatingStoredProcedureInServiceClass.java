package org.example.JDBC.storedprocedure;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.example.JDBC.Employee;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.ParameterMode;

@Service
public class creatingStoredProcedureInServiceClass {

    /// Plain JDBC — create and call in same service
    // Made static so Spring can instantiate it properly as an inner bean
    @Service
    public static class EmployeeJdbcService {

        @Autowired
        private DataSource dataSource;

        public void createAndExecuteProcedure() {
            String checkSql = "SELECT COUNT(*) " +
                    "FROM information_schema.ROUTINES " +
                    "WHERE ROUTINE_SCHEMA = DATABASE() " +
                    "AND ROUTINE_NAME = 'get_employee_count' " +
                    "AND ROUTINE_TYPE = 'PROCEDURE'";

            String createProcedure = "CREATE PROCEDURE get_employee_count() " +
                    "BEGIN " +
                    "   SELECT COUNT(*) FROM employee; " +
                    "END";

            String callProcedure = "{CALL get_employee_count()}";

            try (Connection conn = dataSource.getConnection()) {

                boolean exists = false;

                // check
                try (PreparedStatement ps = conn.prepareStatement(checkSql);
                        ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }

                // create if missing
                if (!exists) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(createProcedure);
                    }
                }

                // execute
                try (CallableStatement cs = conn.prepareCall(callProcedure);
                        ResultSet rs = cs.executeQuery()) {

                    while (rs.next()) {
                        System.out.println("Count: " + rs.getInt(1));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /// JdbcTemplate — create and call in same service
    @Service
    public static class EmployeeJdbcTemplateService {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private SimpleJdbcCall jdbcCall;

        // ── CREATE the procedure
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

            // ── Build the SimpleJdbcCall after proc is created
            jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("get_employees_by_dept")
                    .returningResultSet("employees",
                            (rs, rowNum) -> new Employee(
                                    rs.getString("name"),
                                    rs.getLong("id"),
                                    rs.getString("department"),
                                    rs.getDouble("salary")));
        }

        // ── CALL the procedure
        @SuppressWarnings("unchecked")
        public List<Employee> getByDepartment(String dept) {
            SqlParameterSource in = new MapSqlParameterSource("p_department", dept);

            Map<String, Object> out = jdbcCall.execute(in);

            return (List<Employee>) out.get("employees");
        }
    }

    /// Spring Data JPA — create and call in same service
    @Service
    public static class EmployeeJpaService {

        @Autowired
        private JdbcTemplate jdbcTemplate; // to create the proc

        @PersistenceContext
        private EntityManager entityManager; // to call the proc

        // ── CREATE the procedure
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
}
