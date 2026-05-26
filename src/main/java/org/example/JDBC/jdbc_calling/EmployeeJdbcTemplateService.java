package org.example.JDBC.jdbc_calling;

import java.util.List;
import java.util.Map;

import org.example.JDBC.Employee;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import jakarta.annotation.PostConstruct;

/**
 * Way 2: JdbcTemplate — create and call in same service
 */
@Service
public class EmployeeJdbcTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall jdbcCall;

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

        // ── Build the SimpleJdbcCall after proc is created
        jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("get_employees_by_dept")
                .returningResultSet("employees",
                        (rs, rowNum) -> new Employee(
                                rs.getString("name"),
                                rs.getLong("id"),
                                rs.getString("department"),
                                rs.getDouble("salary")
                        )
                );
    }

    // ── CALL the procedure
    @SuppressWarnings("unchecked")
    public List<Employee> getByDepartment(String dept) {
        SqlParameterSource in =
                new MapSqlParameterSource("p_department", dept);

        Map<String, Object> out = jdbcCall.execute(in);

        return (List<Employee>) out.get("employees");
    }
}
