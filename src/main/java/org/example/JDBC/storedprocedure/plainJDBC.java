package org.example.JDBC.storedprocedure;

import org.example.JDBC.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class plainJDBC {

    private DatabaseMetaData dataSource;

    public List<Employee> findByDept(String dept) {
        List<Employee> list = new ArrayList<>();

        // { CALL proc_name(?) } is the JDBC escape syntax for stored procs
        String sql = "{ CALL get_employees_by_dept(?) }";

        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dept); // set the IN parameter
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("name"),
                        rs.getLong("id"),
                        rs.getString("department"),
                        rs.getDouble("salary")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Proc call failed", e);
        }
        return list;
    }
}
