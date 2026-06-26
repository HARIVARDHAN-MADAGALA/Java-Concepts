package org.example.JDBC.jdbc_calling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.SQLException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Way 1: Plain JDBC — create and call in the same service
 */
@Service
public class EmployeeJdbcService {

    @Autowired
    private DataSource dataSource;

    public void createAndExecuteProcedure() {
        String checkSql =
                "SELECT COUNT(*) " +
                        "FROM information_schema.ROUTINES " +
                        "WHERE ROUTINE_SCHEMA = DATABASE() " +
                        "AND ROUTINE_NAME = 'get_employee_count' " +
                        "AND ROUTINE_TYPE = 'PROCEDURE'";

        String createProcedure =
                "CREATE PROCEDURE get_employee_count() " +
                        "BEGIN " +
                        "   SELECT COUNT(*) FROM employee; " +
                        "END";

        String callProcedure = "{CALL get_employee_count()}";

        try (Connection conn = dataSource.getConnection()) {

            boolean exists = false;

            // 1. Check if stored procedure exists
            try (PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }

            // 2. Create procedure if missing
            if (!exists) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createProcedure);
                }
            }

            // 3. Execute procedure
            try (CallableStatement cs = conn.prepareCall(callProcedure);
                 ResultSet rs = cs.executeQuery()) {

                while (rs.next()) {
                    System.out.println("Count: " + rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error running plain JDBC Stored Procedure", e);
        }
    }
}

/**
 * Connection conn = dataSource.getConnection();
 *
 * PreparedStatement ps = conn.prepareStatement(checkSql);
 * ResultSet rs = ps.executeQuery();
 *
 * Statement stmt = conn.createStatement()
 * stmt.execute(createProcedure)
 *
 * CallableStatement cs = conn.prepareCall(callProcedure)
 * ResultSet rs = cs.executeQuery()
 */
