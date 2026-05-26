// package org.example.JDBC.storedprocedure;
//
// import javax.sql.DataSource;
//
// @Repository
// public class JDBCTemplate {
//
// private final SimpleJdbcCall jdbcCall;
//
// @Autowired
// public EmployeeRepository(DataSource ds) {
// // tell Spring which procedure to call and how to map each row
// jdbcCall = new SimpleJdbcCall(new JdbcTemplate(ds))
// .withProcedureName("get_employees_by_dept")
// .returningResultSet("employees", (rs, rowNum) ->
// new Employee(
// rs.getLong("id"),
// rs.getString("name"),
// rs.getString("department"),
// rs.getDouble("salary")
// )
// );
// }
//
// public List<Employee> findByDept(String dept) {
// SqlParameterSource in =
// new MapSqlParameterSource("p_department", dept);
//
// Map<String, Object> out = jdbcCall.execute(in);
//
// return (List<Employee>) out.get("employees");
// }
// }
